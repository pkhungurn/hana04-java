package hana04.yuri.accel;

import hana04.gfxbase.gfxtype.Aabb3d;
import hana04.gfxbase.gfxtype.Ray;
import hana04.gfxbase.gfxtype.TupleUtil;
import hana04.yuri.surface.Intersection;
import hana04.yuri.surface.PatchIntersection;
import hana04.yuri.surface.RayIntersector;
import hana04.shakuyaku.surface.Surface;
import hana04.shakuyaku.surface.SurfacePatchInfo;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class PatchBvh {
  /**
   * The logger
   */
  private static Logger logger = LoggerFactory.getLogger(PatchBvh.class);
  // Build-related parameters.
  /**
   * Switch to a serial build when less than 32 triangles are left.
   */
  private static final int SERIAL_THRESHOLD = 32;
  /**
   * Switch to a more careful build at this element count.
   */
  private static final int CAREFUL_THRESHOLD = 1000000000;
  /**
   * Heuristic cost value for traveral operation.
   */
  private static final double TRAVERSAL_COST = 1;
  /**
   * Hueristic cost value for intersection operations.
   */
  private static final double INTERSECTION_COST = 1;

  // Fields
  /**
   * List of surfacePatchInfo registered with this BVH.
   */
  private ArrayList<Surface> surfaces = new ArrayList<>();
  /**
   * Index of the first triangle of each mesh.
   */
  private ArrayList<Integer> shapeOffset = new ArrayList<>();
  /**
   * BVH nodes.
   */
  private BvhNode[] nodes;
  /**
   * Triangle indices referenced by the BVH nodes.
   */
  private int[] indices;
  /**
   * Centroids
   */
  private Point3d[] centroids;
  /**
   * Sorted indices.
   */
  private int[][] centroidSortedIndices;
  /**
   * Left side.
   */
  private boolean[] leftSide;

  /**
   * The surface area of the bounding box of triangles to the left of the reference index.
   * Used when computing the SAH cost of a split.
   */
  private Double[] leftAreas;
  /**
   * Bounding box of the entire BVH.
   */
  private Aabb3d bbox = new Aabb3d();
  /**
   * The fork-join pool.
   */
  ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

  /**
   * Create a new and empty BVH.
   */
  private PatchBvh() {
    shapeOffset.add(0);
  }

  public static class Builder {
    private final ArrayList<Surface> surfaces = new ArrayList<>();

    public Builder() {
      // NO-OP
    }

    public Builder addSurface(Surface surface) {
      surfaces.add(surface);
      return this;
    }

    public PatchBvh build() {
      PatchBvh bvh = new PatchBvh();
      surfaces.forEach(surface -> bvh.addSurface(surface));
      bvh.build();
      return bvh;
    }
  }

  /**
   * Register a surface for inclusion in the BVH.
   *
   * @param surface the surface
   */
  private void addSurface(Surface surface) {
    surfaces.add(surface);
    SurfacePatchInfo surfacePatchInfo = surface.getExtension(SurfacePatchInfo.Vv.class).value();
    shapeOffset.add(surfacePatchInfo.getPatchCount() + shapeOffset.get(shapeOffset.size() - 1));
    Aabb3d shapeBBox = new Aabb3d();
    surfacePatchInfo.getAabb(shapeBBox);
    bbox.expandBy(shapeBBox);
  }

  /**
   * Return the number of registered surfacePatchInfo.
   *
   * @return the number of registered surfacePatchInfo
   */
  public int getSurfaceCount() {
    return surfaces.size();
  }

  /**
   * Return the shape with the given index.
   *
   * @param index
   * @return the shape with the given index
   */
  public Surface getSurface(int index) {
    return surfaces.get(index);
  }

  /**
   * Get the bounding box of the whole BVH.
   */
  public void getBoundingBox(Aabb3d bbox) {
    bbox.set(this.bbox);
  }

  /**
   * Get the number of patches;
   */
  private int getPatchCount() {
    return shapeOffset.get(shapeOffset.size() - 1);
  }

  /**
   * Return the index of the surface containing the patch with the given index.
   *
   * @param patchIndex the index of the patch
   * @return the index of the surface containing the patch with the given index
   */
  private int findSurface(int patchIndex) {
    int searchResult = Collections.binarySearch(shapeOffset, patchIndex);
    if (searchResult >= 0)
      return searchResult;
    else
      return -(searchResult + 1) - 1;
  }

  /**
   * Get the centroid of the patch with the given index.
   *
   * @param patchIndex the index of the patch
   * @param centroid   the receiver of the centroid value
   */
  private void getPatchCentroid(int patchIndex, Tuple3d centroid) {
    int meshIndex = findSurface(patchIndex);
    patchIndex -= shapeOffset.get(meshIndex);
    surfaces.get(meshIndex).getExtension(SurfacePatchInfo.Vv.class).value().getPatchCentroid(patchIndex, centroid);
  }

  /**
   * Get the bounding box of the part with the given index.
   *
   * @param partIndex the index of the triangle
   * @param bbox      the receiver of the bounding box value
   */
  private void getPatchAabb(int partIndex, Aabb3d bbox) {
    int meshIndex = findSurface(partIndex);
    partIndex -= shapeOffset.get(meshIndex);
    surfaces.get(meshIndex).getExtension(SurfacePatchInfo.Vv.class).value().getPatchAabb(partIndex, bbox);
  }

  /**
   * Build the BVH.
   */
  private void build() {
    int size = getPatchCount();
    if (size == 0)
      return;
    logger.info("Constructing a SAH BVH from " + surfaces.size() + " mesh(es) with "
      + size + " triangles.");

    long start = System.currentTimeMillis();

    nodes = new BvhNode[2 * size];
    leftAreas = new Double[size];
    indices = new int[size];
    centroidSortedIndices = new int[3][size];
    centroids = new Point3d[size];
    leftSide = new boolean[size];
    for (int i = 0; i < indices.length; i++) {
      indices[i] = i;
      centroids[i] = new Point3d();
      getPatchCentroid(i, centroids[i]);

      centroidSortedIndices[0][i] = i;
      centroidSortedIndices[1][i] = i;
      centroidSortedIndices[2][i] = i;
    }
    Integer[] v = new Integer[size];
    for (int axis = 0; axis < 3; axis++) {
      for (int i = 0; i < v.length; i++) {
        v[i] = centroidSortedIndices[axis][i];
      }
      Arrays.parallelSort(v,
        0, centroidSortedIndices[axis].length,
        new CentroidComparator(axis));
      for (int i = 0; i < v.length; i++) {
        centroidSortedIndices[axis][i] = v[i];
      }
      logger.info("Sorting SurfaceBVH axis = {} DONE!", axis);
    }

    BvhBuildTask task = new BvhBuildTask(0, 0, size);
    forkJoinPool.invoke(task);

    long end = System.currentTimeMillis();
    long elapsed = end - start;
    logger.info("BVH building took {} min(s) {} second(s) {} ms",
      elapsed / (60 * 1000), (elapsed / 1000) % 60, elapsed % 1000);

    leftAreas = null;
    centroidSortedIndices = null;
    centroids = null;
    leftSide = null;
  }

  private class BvhNode {
    Aabb3d bbox = new Aabb3d();
    int start;
    int size;
    int axis;
    int rightChild;
    boolean isLeaf = true;
  }

  private BvhNode createLeaf(int start, int size) {
    BvhNode node = new BvhNode();
    node.isLeaf = true;
    node.start = start;
    node.size = size;
    return node;
  }

  private BvhNode createInternalNode(int axis, int rightChild) {
    BvhNode node = new BvhNode();
    node.isLeaf = false;
    node.axis = axis;
    node.rightChild = rightChild;
    return node;
  }

  private class CentroidComparator implements Comparator<Integer> {
    int axis = 0;

    CentroidComparator(int axis) {
      this.axis = axis;
    }

    @Override
    public int compare(Integer i1, Integer i2) {
      Point3d centroid1 = centroids[i1];
      Point3d centroid2 = centroids[i2];
      return Double.compare(TupleUtil.getComponent(centroid1, axis),
        TupleUtil.getComponent(centroid2, axis));
    }
  }

  /*
  private void checkRep(int start, int end) {
    System.out.println("start = " + start + ", end = " + end);
    for (int axis = 0; axis < 3; axis++) {
      for (int i = start; i < end-1; i++) {
        double v0 = TupleUtil.getComponent(centroids[centroidSortedIndices[axis][i]], axis);
        double v1 = TupleUtil.getComponent(centroids[centroidSortedIndices[axis][i+1]], axis);
        if (v0 > v1) {
          throw new RuntimeException("Something is really wrong here! (start = " + start +
            ", end = " + end + ", axis = " + axis + ")");
        }
      }
    }
  }
  */

  protected class BvhBuildTask extends RecursiveAction {
    int nodeIndex;
    int start;
    int end;

    BvhBuildTask(int nodeIndex, int start, int end) {
      this.nodeIndex = nodeIndex;
      this.start = start;
      this.end = end;
    }

    public void compute() {
      //checkRep(start , end);
      int size = end - start;

      if (size < SERIAL_THRESHOLD) {
        executeSerially(nodeIndex, start, end);
        return;
      }

      Aabb3d nodeBbox = new Aabb3d();
      int bestAxis = -1;
      int bestIndex = -1;
      if (size > 1) {
        if (size < CAREFUL_THRESHOLD) {
          Pair<Integer, Integer> bestAxisIndex = getBestAxisAndBestSplitIndex(start, end, nodeBbox);
          bestAxis = bestAxisIndex.getLeft();
          bestIndex = bestAxisIndex.getRight();
        } else {
          Aabb3d partBbox = new Aabb3d();
          for (int i = start; i < end; i++) {
            int index = centroidSortedIndices[0][i];
            getPatchAabb(index, partBbox);
            nodeBbox.expandBy(partBbox);
          }
          bestAxis = 0;
          for (int axis = 1; axis < 3; axis++) {
            if (nodeBbox.getExtent(bestAxis) < nodeBbox.getExtent(axis)) {
              bestAxis = axis;
            }
          }
          bestIndex = size / 2;
        }
      } else {
        Aabb3d partBbox = new Aabb3d();
        for (int i = start; i < end; i++) {
          int index = centroidSortedIndices[0][i];
          getPatchAabb(index, partBbox);
          nodeBbox.expandBy(partBbox);
        }
      }

      if (bestIndex == -1) {
        // Splitting does not reduce the cost, make a leaf.
        BvhNode node = createLeaf(start, size);
        node.bbox.set(nodeBbox);
        nodes[nodeIndex] = node;
      } else {
        rearrangeIndices(start, end, bestAxis, bestIndex);

        int leftCount = bestIndex;
        int nodeIndexLeft = nodeIndex + 1;
        int nodeIndexRight = nodeIndex + 2 * leftCount;

        BvhNode node = createInternalNode(bestAxis, nodeIndexRight);
        node.bbox.set(nodeBbox);
        nodes[nodeIndex] = node;

        BvhBuildTask leftTask = new BvhBuildTask(nodeIndexLeft, start, start + leftCount);
        BvhBuildTask rightTask = new BvhBuildTask(nodeIndexRight, start + leftCount, end);
        invokeAll(leftTask, rightTask);
      }
    }

    private Pair<Integer, Integer> getBestAxisAndBestSplitIndex(int start, int end, Aabb3d nodeBbox) {
      int size = end - start;
      double bestCost = INTERSECTION_COST * size;
      int bestIndex = -1;
      int bestAxis = -1;

      for (int axis = 0; axis < 3; axis++) {
        /* Sort all triangles based on their centroid positions projected on the axis */
        Aabb3d bbox = new Aabb3d();
        Aabb3d triBbox = new Aabb3d();
        for (int i = start; i < end; i++) {
          getPatchAabb(centroidSortedIndices[axis][i], triBbox);
          bbox.expandBy(triBbox);
          leftAreas[i] = bbox.getSurfaceArea();
        }

        if (axis == 0)
          nodeBbox.set(bbox);

        // Choose the best splitting plane.
        bbox.reset();
        double triFactor = INTERSECTION_COST / nodeBbox.getSurfaceArea();
        for (int i = size - 1; i >= 1; i--) {
          int index = start + i;
          int triIndex = centroidSortedIndices[axis][index];
          getPatchAabb(triIndex, triBbox);
          bbox.expandBy(triBbox);
          double leftArea = leftAreas[index - 1];
          double rightArea = bbox.getSurfaceArea();
          int primsLeft = i;
          int primsRight = size - i;
          double sahCost = 2.0 * TRAVERSAL_COST
            + triFactor * (primsLeft * leftArea + primsRight * rightArea);
          if (sahCost < bestCost) {
            bestCost = sahCost;
            bestIndex = i;
            bestAxis = axis;
          }
        }
      }

      return new ImmutablePair<>(bestAxis, bestIndex);
    }

    private void rearrangeIndices(int start, int end, int bestAxis, int bestIndex) {
      for (int i = start; i < end; i++) {
        leftSide[centroidSortedIndices[bestAxis][i]] = i < start + bestIndex;
      }

      for (int axis = 0; axis < 3; axis++) {
        if (axis == bestAxis)
          continue;
        int leftPos = start;
        int rightPos = start + bestIndex;
        for (int i = start; i < end; i++) {
          int index = centroidSortedIndices[axis][i];
          if (leftSide[index]) {
            indices[leftPos] = index;
            leftPos++;
          } else {
            indices[rightPos] = index;
            rightPos++;
          }
        }
        for (int i = start; i < end; i++) {
          centroidSortedIndices[axis][i] = indices[i];
        }
      }

      for (int i = start; i < end; i++) {
        indices[i] = centroidSortedIndices[bestAxis][i];
      }
    }

    private void executeSerially(int nodeIndex, int start, int end) {
      //System.out.println("nodeIndex = " + nodeIndex);
      //checkRep(start , end);
      int size = end - start;

      Aabb3d nodeBbox = new Aabb3d();
      int bestAxis = -1;
      int bestIndex = -1;
      if (size > 1) {
        if (size < CAREFUL_THRESHOLD) {
          Pair<Integer, Integer> bestAxisIndex = getBestAxisAndBestSplitIndex(start, end, nodeBbox);
          bestAxis = bestAxisIndex.getLeft();
          bestIndex = bestAxisIndex.getRight();
        } else {
          Aabb3d partBbox = new Aabb3d();
          for (int i = start; i < end; i++) {
            int index = centroidSortedIndices[0][i];
            getPatchAabb(index, partBbox);
            nodeBbox.expandBy(partBbox);
          }
          bestAxis = 0;
          for (int axis = 1; axis < 3; axis++) {
            if (nodeBbox.getExtent(bestAxis) < nodeBbox.getExtent(axis)) {
              bestAxis = axis;
            }
          }
          bestIndex = size / 2;
        }
      } else {
        Aabb3d partBbox = new Aabb3d();
        for (int i = start; i < end; i++) {
          int index = centroidSortedIndices[0][i];
          getPatchAabb(index, partBbox);
          nodeBbox.expandBy(partBbox);
        }
      }

      if (bestIndex == -1) {
        // Splitting does not reduce the cost, make a leaf.
        BvhNode node = createLeaf(start, size);
        node.bbox.set(nodeBbox);
        nodes[nodeIndex] = node;
      } else {
        rearrangeIndices(start, end, bestAxis, bestIndex);
        //CentroidComparator comparator = new CentroidComparator(bestAxis);
        //Arrays.sort(indices, start, end, comparator);

        int leftCount = bestIndex;
        int nodeIndexLeft = nodeIndex + 1;
        int nodeIndexRight = nodeIndex + 2 * leftCount;

        BvhNode node = createInternalNode(bestAxis, nodeIndexRight);
        node.bbox.set(nodeBbox);
        nodes[nodeIndex] = node;

        executeSerially(nodeIndexLeft, start, start + leftCount);
        executeSerially(nodeIndexRight, start + leftCount, end);
      }
    }
  }

  /**
   * Intersect a ray against all patches registered with the BVH.
   * <p>
   * The shadowRay parameter specifies whether to terminate the intersection procedure when the any hit is found.
   * This is used to make tracing shadow rays faster.
   * <p>
   * WARNING: The ray's maxt field is modified to be the time of the intersection.
   *
   * @param ray    the ray
   * @param anyHit whether to terminate the intersection process when a hit is found
   * @return the surface that we found the intersection and the information of the intersection of the ray with a patch
   * of the surface
   */
  private Optional<Pair<Surface, PatchIntersection>> rayIntersect(Ray ray, boolean anyHit) {
    if (nodes == null || ray.maxt < ray.mint) {
      return Optional.empty();
    }
    int nodeIdx = 0;
    int stackIdx = 0;
    int[] stack = new int[64];
    double[] dRcp = new double[]{1.0 / ray.d.x, 1.0 / ray.d.y, 1.0 / ray.d.z};
    Optional<Pair<Surface, PatchIntersection>> result = Optional.empty();

    boolean foundIntersection = false;
    while (true) {
      BvhNode node = nodes[nodeIdx];
      if (!node.bbox.rayIntersectFast(ray, dRcp)) {
        if (stackIdx == 0) {
          break;
        }
        stackIdx--;
        nodeIdx = stack[stackIdx];
        continue;
      }
      if (!node.isLeaf) {
        stack[stackIdx] = node.rightChild;
        stackIdx++;
        nodeIdx++;
        continue;
      }
      for (int i = node.start; i < node.start + node.size; i++) {
        int idx = indices[i];
        int surfaceIdx = findSurface(idx);
        Surface surface = surfaces.get(surfaceIdx);
        int patchIndex = idx - shapeOffset.get(surfaceIdx);
        RayIntersector rayIntersector = surface.getExtension(RayIntersector.Vv.class).value();
        Optional<PatchIntersection> its = rayIntersector.rayIntersect(patchIndex, ray);
        if (!its.isPresent()) {
          continue;
        }
        if (anyHit) {
          return Optional.of(ImmutablePair.of(surface, its.get()));
        }
        foundIntersection = true;
        result = Optional.of(ImmutablePair.of(surface, its.get()));
      }
      if (stackIdx == 0) {
        break;
      }
      stackIdx--;
      nodeIdx = stack[stackIdx];
    }
    return foundIntersection ? result : Optional.empty();
  }

  /**
   * Find the first hit between the given {@link Ray} and the patches registered in the BVH and, if there is one,
   * return the surface and the detailed intersection information.
   * <p>
   * WARNING: The ray's maxt field is modified to be the time of the intersection.
   */
  public Optional<Pair<Surface, Intersection>> rayIntersect(Ray ray) {
    Optional<Pair<Surface, PatchIntersection>> its = rayIntersect(ray, false);
    if (!its.isPresent()) {
      return Optional.empty();
    }
    Surface surface = its.get().getKey();
    PatchIntersection patchIntersection = its.get().getRight();
    Intersection intersection =
      surface.getExtension(RayIntersector.Vv.class).value().createIntersectionRecord(patchIntersection);
    return Optional.of(ImmutablePair.of(surface, intersection));
  }

  /**
   * Check whether the given {@link Ray} intersects with any patch registered with the BVH.
   * <p>
   * WARNING: The ray's maxt field is modified to be the time of the intersection.
   */
  public boolean checkRayIntersection(Ray ray) {
    return rayIntersect(ray, true).isPresent();
  }
}

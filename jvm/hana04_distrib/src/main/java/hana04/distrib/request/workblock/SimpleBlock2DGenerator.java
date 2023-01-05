package hana04.distrib.request.workblock;

import java.util.Iterator;

public class SimpleBlock2DGenerator implements Iterator<Block2DSpecification> {
  private final int sizeX;
  private final int sizeY;
  private final int blockSize;
  private final int blockCount;
  private final int countX;
  private int blocksLeft;

  /**
   * Create a block generator
   *
   * @param sizeX     the width of the image
   * @param sizeY     the height of the image
   * @param blockSize the maximum size of the individual block
   */
  public SimpleBlock2DGenerator(int sizeX, int sizeY, int blockSize) {
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    this.blockSize = blockSize;
    countX = sizeX / blockSize + ((sizeX % blockSize == 0) ? 0 : 1);
    int countY = sizeY / blockSize + ((sizeY % blockSize == 0) ? 0 : 1);
    blockCount = countX * countY;
    blocksLeft = blockCount;
  }

  @Override
  public synchronized boolean hasNext() {
    return blocksLeft > 0;
  }

  @Override
  public synchronized Block2DSpecification next() {
    if (blocksLeft == 0) {
      return null;
    }
    int blockIndex = blockCount - blocksLeft;
    blocksLeft--;
    int ix = blockIndex % countX;
    int iy = blockIndex / countX;
    int sx = Math.min(blockSize, sizeX - ix * blockSize);
    int sy = Math.min(blockSize, sizeY - iy * blockSize);
    return new Block2DSpecification.Builder()
      .sizeX(sx)
      .sizeY(sy)
      .offsetX(ix * blockSize)
      .offsetY(iy * blockSize)
      .build();
  }
}

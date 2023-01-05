package hana04.yuri.emitter;

import com.google.common.base.Preconditions;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import java.util.Optional;

public class EmitterEvalInput {
  /**
   * World position of the point where lighting is going to be evaluated.
   */
  public final Point3d p = new Point3d();
  /**
   * The incoming light direction to Point p in world space.
   * It must point away from p.
   */
  public final Vector3d wiWorld = new Vector3d();
  /**
   * A point on the light source, if the light source is an area light source.
   */
  public Optional<Point3d> q = Optional.empty();
  /**
   * The normal vector at point q.
   */
  public Optional<Vector3d> nq = Optional.empty();

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Point3d p = null;
    private Vector3d wiWorld = null;
    Optional<Point3d> q = Optional.empty();
    Optional<Vector3d> nq = Optional.empty();

    public Builder p(double x, double y, double z) {
      if (this.p == null) {
        this.p = new Point3d(x,y,z);
      } else {
        this.p.set(x,y,z);
      }
      return this;
    }

    public Builder p(Tuple3d p) {
      return p(p.x, p.y, p.z);
    }

    public Builder wiWorld(double x, double y, double z) {
      if (this.wiWorld == null) {
        this.wiWorld = new Vector3d(x,y,z);
      } else {
        this.wiWorld.set(x,y,z);
      }
      return this;
    }

    public Builder wiWorld(Tuple3d wiWorld) {
      return wiWorld(wiWorld.x, wiWorld.y, wiWorld.z);
    }

    public Builder q(double x, double y, double z) {
      if (!this.q.isPresent()) {
        this.q = Optional.of(new Point3d(x,y,z));
      } else {
        this.q.get().set(x,y,z);
      }
      return this;
    }

    public Builder q(Tuple3d q) {
      return q(q.x, q.y, q.z);
    }

    public Builder q(Optional<? extends Tuple3d> q) {
      if (!q.isPresent()) {
        this.q = Optional.empty();
      } else {
        Tuple3d qq = q.get();
        return q(qq);
      }
      return this;
    }

    public Builder nq(double x, double y, double z) {
      if (!this.nq.isPresent()) {
        this.nq = Optional.of(new Vector3d(x,y,z));
      } else {
        this.nq.get().set(x,y,z);
      }
      return this;
    }

    public Builder nq(Tuple3d nq) {
      return nq(nq.x, nq.y, nq.z);
    }

    public Builder nq(Optional<? extends Tuple3d> nq) {
      if (!nq.isPresent()) {
        this.nq = Optional.empty();
      } else {
        nq(nq.get());
      }
      return this;
    }

    public EmitterEvalInput build() {
      Preconditions.checkNotNull(p);
      Preconditions.checkNotNull(wiWorld);
      EmitterEvalInput eei = new EmitterEvalInput();
      eei.p.set(p);
      eei.wiWorld.set(wiWorld);
      eei.q = q;
      eei.nq = nq;
      return eei;
    }
  }
}

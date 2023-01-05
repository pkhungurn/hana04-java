package hana04.distrib;

import hana04.distrib.request.donothing.DoNothingRequest;

/**
 * This project's type IDs begin with 20000.
 */
public final class TypeIds {
  /**
   * {@link DoNothingRequest}
   */
  public static final int TYPE_ID_DO_NOTHING_REQUEST = 20001;

  private TypeIds() {
    // NO-OP
  }
}

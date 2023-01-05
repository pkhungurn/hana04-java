package hana04.base;

import hana04.base.donothingobject.DoNothingObject;
import hana04.base.serialize.HanaSerializable;
import hana04.base.util.HanaMapEntry;

/**
 * This project's typeID starts with 10000. The typeIDs are:
 *
 *
 * <ul>
 *   <li>1: Look up by UUID {@link TypeIds#TYPE_ID_LOOKUP}</li>
 * </ul>
 */
public final class TypeIds {
  /**
   * Type ID for looking up {@link HanaSerializable} objects with their UUIDs.
   */
  public static final int TYPE_ID_LOOKUP = 1;

  /**
   * {@link DoNothingObject}
   */
  public static final int TYPE_ID_DO_NOTHING_OBJECT = 10001;

  /**
   * {@link HanaMapEntry}
   */
  public static final int TYPE_ID_HANA_MAP_ENTRY = 10002;

  private TypeIds() {
    // NO-OP
  }
}

package hana04.serialize;

/**
 * This project's type IDs begin with 30000.
 */
public final class TypeIds {
  public static final int TYPE_ID_INTEGER = 30001;

  public static final int TYPE_ID_FLOAT = 30002;

  public static final int TYPE_ID_DOUBLE = 30003;

  public static final int TYPE_ID_LONG = 30004;

  public static final int TYPE_ID_BOOLEAN = 30005;

  public static final int TYPE_ID_STRING = 30006;

  public static final int TYPE_ID_UUID = 30017;

  public static final int TYPE_ID_FILE_PATH = 30020;

  public static final int TYPE_ID_DIRECT = 30021;

  public static final int TYPE_ID_CACHED = 30022;

  public static final int TYPE_ID_CACHE_KEY = 30023;

  // Old IDs = 30025

  private TypeIds() {
    // NO-OP
  }
}

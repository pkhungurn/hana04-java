package hana04.serialize;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PrimitiveParsingUtil {
  public static Integer parseInteger(Object value) {
    if (value instanceof Integer) {
      return (Integer) value;
    } else if (value instanceof Long) {
      return ((Long) value).intValue();
    } else if (value instanceof BigInteger) {
      return ((BigInteger) value).intValue();
    } else if (value instanceof BigDecimal) {
      return ((BigDecimal) value).intValue();
    } else if (value instanceof String) {
      return Integer.parseInt((String) value);
    } else {
      throw new RuntimeException("The given value cannot be converted to an integer value.");
    }
  }

  public static Long parseLong(Object value) {
    if (value instanceof Integer) {
      return Long.valueOf((Integer) value);
    } else if (value instanceof Long) {
      return (Long) value;
    } else if (value instanceof BigInteger) {
      return ((BigInteger) value).longValue();
    } else if (value instanceof BigDecimal) {
      return ((BigDecimal) value).longValue();
    } else if (value instanceof String) {
      return Long.parseLong((String) value);
    } else {
      throw new RuntimeException("The given value cannot be converted to an integer value.");
    }
  }

  public static Double parseDouble(Object value) {
    if (value instanceof Double) {
      return (Double) value;
    } else if (value instanceof Integer) {
      return ((Integer) value).doubleValue();
    } else if (value instanceof BigInteger) {
      return ((BigInteger) value).doubleValue();
    } else if (value instanceof BigDecimal) {
      return ((BigDecimal) value).doubleValue();
    } else if (value instanceof String) {
      return Double.parseDouble((String) value);
    } else {
      throw new RuntimeException("The given value cannot be converted to a floating-point value: " + value);
    }
  }
}

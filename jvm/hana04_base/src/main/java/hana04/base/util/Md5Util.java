package hana04.base.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;

public final class Md5Util {
  public static String hash(String s) {
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      messageDigest.update(s.getBytes());
      byte[] digest = messageDigest.digest();
      return Base64.encodeBase64URLSafeString(digest);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    String s = "data/sumire/tachie/ready-to-use/games/bungou-to-alchemist/北原白秋/北原白秋【衣装十一】.png";
    try {
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      messageDigest.update(s.getBytes());
      byte[] digest = messageDigest.digest();
      System.out.println(Hex.encodeHexString(digest));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

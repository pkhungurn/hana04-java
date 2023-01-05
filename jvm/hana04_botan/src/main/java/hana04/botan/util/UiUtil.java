package hana04.botan.util;


import javax.swing.UIManager;

public class UiUtil {
  public static void initSwingLookAndField() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      throw new RuntimeException("Could set system look and field for Swing");
    }
  }
}

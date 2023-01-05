package hana04.mikumikubake.ui;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BasicAction extends AbstractAction {
  private final ActionListener actionListener;

  public BasicAction(String name, ActionListener actionListener) {
    super(name);
    this.actionListener = actionListener;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    this.actionListener.actionPerformed(e);
  }
}

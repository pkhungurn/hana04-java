package hana04.opengl.trial._01;

import org.lwjgl.glfw.GLFW;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingWindow extends JFrame implements ActionListener {
  JButton quitButton;
  Trial01 trial01;

  SwingWindow(Trial01 trial01) {
    super();
    this.trial01 = trial01;
    setSize(200,200);
    quitButton = new JButton();
    quitButton.setText("Quit");
    add(quitButton);
    quitButton.addActionListener(this);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == quitButton) {
      GLFW.glfwSetWindowShouldClose(trial01.windowId, true);
    }
  }
}

package hana04.mikumikubake.opengl.renderer00.camera.ui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import hana04.mikumikubake.opengl.renderer00.camera.BoxedValue;
import hana04.mikumikubake.opengl.renderer00.camera.YUpCameraView;
import hana04.mikumikubake.ui.MouseButtonEvent;
import info.clearthought.layout.TableLayout;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.LayoutManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.GLFW_MOD_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT;

public abstract class AbstractYUpCameraViewControl implements CameraViewControl<YUpCameraView> {
  public static final double NO_UPDATE_EPSILON = 0.001;

  protected final JTextField xAngleTextField = new JTextField();
  protected final JTextField yAngleTextField = new JTextField();
  protected final JTextField zAngleTextField = new JTextField();
  protected final JTextField centerXTextField = new JTextField();
  protected final JTextField centerYTextField = new JTextField();
  protected final JTextField centerZTextField = new JTextField();
  protected final JTextField distanceTextField = new JTextField();

  final ImmutableSet<JTextField> uiTextFieldSet = ImmutableSet.of(
    xAngleTextField,
    yAngleTextField,
    zAngleTextField,
    centerXTextField,
    centerYTextField,
    centerZTextField,
    distanceTextField
  );

  private JPanel uiPanel;

  protected final YUpCameraView cameraView;

  private final DecimalFormat paramsNumberFormat = new DecimalFormat("0.000");
  private final ImmutableMap<JTextField, BoxedValue<Double>> uiTextFieldToValue;

  private final GLFWMouseButtonCallbackI mouseButtonCallback;
  private final GLFWCursorPosCallbackI mouseCursorCallback;

  private DocumentListener documentListener = new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
      textFieldChanged(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      textFieldChanged(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      textFieldChanged(e);
    }
  };

  private FocusListener focusListener = new FocusListener() {
    @Override
    public void focusGained(FocusEvent e) {
      // NO-OP
    }

    @Override
    public void focusLost(FocusEvent e) {
      textFieldFocusLost(e);
    }
  };

  public AbstractYUpCameraViewControl(YUpCameraView cameraView) {
    this.cameraView = cameraView;

    uiTextFieldToValue = ImmutableMap.<JTextField, BoxedValue<Double>>builder()
      .put(xAngleTextField, cameraView.boxedXAngle)
      .put(yAngleTextField, cameraView.boxedYAngle)
      .put(zAngleTextField, cameraView.boxedZAngle)
      .put(centerXTextField, cameraView.boxedCenterX)
      .put(centerYTextField, cameraView.boxedCenterY)
      .put(centerZTextField, cameraView.boxedCenterZ)
      .put(distanceTextField, cameraView.boxedDistance)
      .build();

    this.uiPanel = createUiPanel();
    this.mouseButtonCallback = new GlMouseButtonCallback(this);
    this.mouseCursorCallback = new GlMouseCursorCallback(this);
  }

  private JPanel createUiPanel() {
    JPanel cameraPanel = new JPanel();
    cameraPanel.setLayout(createCameraPanelLayout());

    cameraPanel.add(new JLabel("X Rot:"), "0,0,0,0");
    cameraPanel.add(new JLabel("Y Rot:"), "4,0,4,0");
    cameraPanel.add(new JLabel("Z Rot:"), "8,0,8,0");
    cameraPanel.add(new JLabel("Center X:"), "0,2,0,2");
    cameraPanel.add(new JLabel("Center Y:"), "4,2,4,2");
    cameraPanel.add(new JLabel("Center Z:"), "8,2,8,2");
    cameraPanel.add(new JLabel("Distance:"), "0,4,0,4");

    cameraPanel.add(xAngleTextField, "2,0,2,0");
    xAngleTextField.setPreferredSize(new Dimension(100,10));
    cameraPanel.add(yAngleTextField, "6,0,6,0");
    yAngleTextField.setPreferredSize(new Dimension(100,10));
    cameraPanel.add(zAngleTextField, "10,0,10,0");
    zAngleTextField.setPreferredSize(new Dimension(100,10));
    cameraPanel.add(centerXTextField, "2,2,2,2");
    centerXTextField.setPreferredSize(new Dimension(100,10));
    cameraPanel.add(centerYTextField, "6,2,6,2");
    centerYTextField.setPreferredSize(new Dimension(100,10));
    cameraPanel.add(centerZTextField, "10,2,10,2");
    centerZTextField.setPreferredSize(new Dimension(100,10));
    cameraPanel.add(distanceTextField, "2,4,2,4");
    distanceTextField.setPreferredSize(new Dimension(100,10));

    uiTextFieldSet.forEach(textField -> textField.getDocument().addDocumentListener(documentListener));
    uiTextFieldSet.forEach(textField -> textField.addFocusListener(focusListener));

    return cameraPanel;
  }

  private LayoutManager createCameraPanelLayout() {
    double[][] tableLayoutSizes = {
      {
        TableLayout.MINIMUM, 5, TableLayout.PREFERRED, 5,
        TableLayout.MINIMUM, 5, TableLayout.PREFERRED, 5,
        TableLayout.MINIMUM, 5, TableLayout.PREFERRED
      },
      {
        TableLayout.MINIMUM, 5,
        TableLayout.MINIMUM, 5,
        TableLayout.MINIMUM
      }
    };
    return new TableLayout(tableLayoutSizes);
  }

  private void textFieldChanged(DocumentEvent e) {
    Optional<JTextField> optionalCameraUiTextField = getCorrespondingUiTextField(e.getDocument());
    optionalCameraUiTextField.ifPresent(this::updateUiTextField);
  }

  private Optional<JTextField> getCorrespondingUiTextField(Document document) {
    return uiTextFieldToValue.keySet().stream()
      .filter(textField -> textField.getDocument() == document).findFirst();
  }

  private void textFieldFocusLost(FocusEvent e) {
    if (e.getComponent() instanceof JTextField) {
      JTextField textField = (JTextField) e.getComponent();
      if (!uiTextFieldToValue.containsKey(textField)) {
        return;
      }
      textField.setText(paramsNumberFormat.format(uiTextFieldToValue.get(textField).get()));
    }
  }

  private Optional<Double> parseDouble(String text) {
    if (!NumberUtils.isParsable(text)) {
      return Optional.empty();
    }
    return Optional.of(Double.parseDouble(text));
  }

  void updateUiTextField(JTextField uiTextField) {
    Optional<Double> parsed = parseDouble(uiTextField.getText());
    BoxedValue<Double> value = uiTextFieldToValue.get(uiTextField);
    if (!parsed.isPresent()) {
      return;
    }
    double newValue = parsed.get();
    if (Math.abs(newValue - value.get()) < NO_UPDATE_EPSILON) {
      return;
    }
    value.set(newValue);
  }

  @Override
  public YUpCameraView getCameraView() {
    return cameraView;
  }

  @Override
  public void updateUi() {
    EventQueue.invokeLater(() -> {
      uiTextFieldToValue.forEach((textField, value) ->
        textField.setText(paramsNumberFormat.format(value.get())));
    });
  }

  @Override
  public GLFWCursorPosCallbackI getMouseCursorCallback() {
    return mouseCursorCallback;
  }

  @Override
  public GLFWMouseButtonCallbackI getMouseButtonCallback() {
    return mouseButtonCallback;
  }

  @Override
  public JPanel getPanel() {
    return uiPanel;
  }

  public abstract void handleMouseButtonEvent(MouseButtonEvent event);

  public abstract void handleMouseMoveEvent(double xPos, double yPos);

  static class GlMouseCursorCallback implements GLFWCursorPosCallbackI {

    private AbstractYUpCameraViewControl control;

    GlMouseCursorCallback(AbstractYUpCameraViewControl cameraControl) {
      this.control = cameraControl;
    }

    @Override
    public void invoke(long window, double xpos, double ypos) {
      control.handleMouseMoveEvent(xpos, ypos);
    }
  }

  static class GlMouseButtonCallback implements GLFWMouseButtonCallbackI {

    private AbstractYUpCameraViewControl control;

    GlMouseButtonCallback(AbstractYUpCameraViewControl cameraControl) {
      this.control = cameraControl;
    }

    @Override
    public void invoke(long window, int button, int action, int mods) {
      MouseButtonEvent event = MouseButtonEvent.builder()
        .button(MouseButtonEvent.Button.parse(button))
        .action(MouseButtonEvent.Action.parse(action))
        .shiftPressed((mods & GLFW_MOD_SHIFT) != 0)
        .ctrlPressed((mods & GLFW_MOD_CONTROL) != 0)
        .altPressed((mods & GLFW_MOD_ALT) != 0)
        .build();
      control.handleMouseButtonEvent(event);
    }
  }
}
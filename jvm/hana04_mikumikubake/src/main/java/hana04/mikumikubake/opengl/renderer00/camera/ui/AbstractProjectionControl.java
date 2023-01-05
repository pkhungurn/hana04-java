package hana04.mikumikubake.opengl.renderer00.camera.ui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hana04.mikumikubake.opengl.renderer00.camera.BoxedValue;
import info.clearthought.layout.TableLayout;
import org.apache.commons.lang3.math.NumberUtils;
import org.inferred.freebuilder.FreeBuilder;

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

public class AbstractProjectionControl {
  private static final double NO_UPDATE_EPSILON = 0.001;

  private final ImmutableList<Parameter> parameters;
  private final ImmutableList<JTextField> uiTextFields;
  private final ImmutableMap<JTextField, BoxedValue<Double>> uiTextFieldToValue;

  private final DocumentListener documentListener = new DocumentListener() {
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

  private final FocusListener focusListener = new FocusListener() {
    @Override
    public void focusGained(FocusEvent e) {
      // NO-OP
    }

    @Override
    public void focusLost(FocusEvent e) {
      textFieldFocusLost(e);
    }
  };

  private final JPanel uiPanel;

  private final DecimalFormat paramsNumberFormat = new DecimalFormat("0.000");

  public AbstractProjectionControl(ImmutableList<Parameter> parameters) {
    this.parameters = parameters;
    this.uiTextFields = createTextFields();
    this.uiTextFieldToValue = createUiTextFieldToValue();
    this.uiPanel = createUiPanel();
  }

  private ImmutableList<JTextField> createTextFields() {
    ImmutableList.Builder<JTextField> output = ImmutableList.builder();
    for (int i = 0; i < parameters.size(); i++) {
      JTextField textField = new JTextField();
      textField.getDocument().addDocumentListener(documentListener);
      textField.addFocusListener(focusListener);
      output.add(textField);
    }
    return output.build();
  }

  private ImmutableMap<JTextField, BoxedValue<Double>> createUiTextFieldToValue() {
    ImmutableMap.Builder<JTextField, BoxedValue<Double>> output = ImmutableMap.builder();
    for (int i = 0; i < parameters.size(); i++) {
      output.put(uiTextFields.get(i), parameters.get(i).boxedValue());
    }
    return output.build();
  }

  private JPanel createUiPanel() {
    JPanel cameraPanel = new JPanel();
    cameraPanel.setLayout(createUiPanelLayout(parameters.size()));

    for (int i = 0; i < parameters.size(); i++) {
      Parameter parameter = parameters.get(i);
      int row = i / 3;
      int col = i % 3;

      String labelPosition = String.format("%d,%d,%d,%d", 4 * col, 2 * row, 4 * col, 2 * row);
      JLabel label = new JLabel(parameter.label());
      cameraPanel.add(label, labelPosition);

      String textFieldPosition = String.format("%d,%d,%d,%d", 4 * col + 2, 2 * row, 4 * col + 2, 2 * row);
      cameraPanel.add(uiTextFields.get(i), textFieldPosition);
      uiTextFields.get(i).setPreferredSize(new Dimension(100,10));
    }

    return cameraPanel;
  }

  private LayoutManager createUiPanelLayout(final int parameterCount) {
    int numRow = parameterCount / 3;
    if (parameterCount % 3 != 0) {
      numRow += 1;
    }

    double[] rowSpec = new double[2 * numRow - 1];
    rowSpec[0] = TableLayout.MINIMUM;
    for (int i = 0; i < numRow-1; i++) {
      rowSpec[2 * i + 1] = 5;
      rowSpec[2 * i + 2] = TableLayout.MINIMUM;
    }

    double[][] tableLayoutSizes = {
      {
        TableLayout.MINIMUM, 5, TableLayout.PREFERRED, 5,
        TableLayout.MINIMUM, 5, TableLayout.PREFERRED, 5,
        TableLayout.MINIMUM, 5, TableLayout.PREFERRED
      },
      rowSpec
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

  private void updateUiTextField(JTextField uiTextField) {
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

  public void updateUi() {
    EventQueue.invokeLater(() -> {
      uiTextFieldToValue.forEach((textField, value) ->
        textField.setText(paramsNumberFormat.format(value.get())));
    });
  }

  public JPanel getPanel() {
    return uiPanel;
  }

  @FreeBuilder
  public interface Parameter {
    String label();

    BoxedValue<Double> boxedValue();

    class Builder extends AbstractProjectionControl_Parameter_Builder {
    }

    static Builder newBuilder() {
      return new Builder();
    }
  }
}

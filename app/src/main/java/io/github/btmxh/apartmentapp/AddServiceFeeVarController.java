package io.github.btmxh.apartmentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class AddServiceFeeVarController {
    @FXML
    private Node root;

    @FXML
    private TextField nameField;

    @FXML
    private TextField valueField;

    public String getName() {
        final var name = nameField.getText().trim();
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Variable name is empty");
        }

        return name;
    }

    public ServiceFee.FormulaTerminal getTerminal() {
        final var valueStr = valueField.getText().trim();
        try {
            final var value = Double.parseDouble(valueStr);
            return new ServiceFee.ConstFormulaTerminal(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid variable value: " + valueStr);
        }
    }

    public void removeSelf(ActionEvent e) {
        ((Pane) root.getParent()).getChildren().remove(root);
    }

    public void setTerminal(String name, ServiceFee.FormulaTerminal term) throws Exception {
        nameField.setText(name);
        if(term != null) {
            valueField.setText(Double.toString(term.calculate()));
        }
    }
}

package io.github.btmxh.apartmentapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.commons.compiler.CompileException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AddServiceFeeController {
    private static final Logger logger = LogManager.getLogger();

    @FXML
    private TextField nameField;
    @FXML
    private TextField formulaField;
    @FXML
    private VBox variableBox;

    private ServiceFee fee;
    private Stage stage;

    public void initialize() {
//        Utils.initMonthComboBox(monthBox);
    }

    public void cancel(ActionEvent e) {
        stage.close();
    }

    public void submit(ActionEvent e) {
        final var name = nameField.getText();
        final var formulaExpr = formulaField.getText();

        final ServiceFee.Formula formula;
        final Map<String, ServiceFee.FormulaTerminal> variables = new HashMap<>();
        final var variableControllers = variableBox.getChildren().stream()
                .map(Node::getUserData)
                .filter(n -> n instanceof AddServiceFeeVarController)
                .map(n -> (AddServiceFeeVarController) n)
                .toList();
        for (final var c : variableControllers) {
            try {
                final var varName = c.getName();
                final var varTerm = c.getTerminal();

                if (variables.put(varName, varTerm) != null) {
                    Announcement.show("Lỗi", "Xuất hiện biến trùng lặp", "Biến " + varName + " xuất hiện hai lần trong khai báo biến");
                    return;
                }
            } catch (IllegalArgumentException ex) {
                Announcement.show("Lỗi", "Tên/giá trị biến không hợp lệ", ex.getMessage());
                return;
            }
        }
        try {
            formula = new ServiceFee.Formula(formulaExpr, variables);
        } catch (CompileException ex) {
            Announcement.show("Lỗi", "Không thể phân tích biểu thức tính phí", ex.getMessage());
            return;
        }

        if(fee.getId() == ServiceFee.NULL_ID) {
            try {
                DatabaseConnection.getInstance().updateServiceFee(fee, name, formula);
            } catch (SQLException | IOException ex) {
                logger.warn("Unable to add service fee", ex);
                Announcement.show("Lỗi", "Không thể thêm phí dịch vụ vào CSDL", ex.getMessage());
                return;
            }
        }

        stage.close();
    }

    private void newVariable(String name, ServiceFee.FormulaTerminal term) {
        try {
            final var loader = Utils.fxmlLoader("/add-service-fee-var.fxml");
            final Node content = loader.load();
            final AddServiceFeeVarController controller = loader.getController();
            content.setUserData(controller);
            controller.setTerminal(name, term);
            variableBox.getChildren().add(content);
        } catch (Exception e) {
            logger.fatal("Error loading FXML file", e);
            Announcement.show("Lỗi", "Không thể tải FXML cho giao diện thêm biến phí", e.getMessage());
        }
    }

    public void addVariable(ActionEvent _e) {
        newVariable("", null);
    }

    public static ServiceFee open(Window window, ServiceFee fee) throws IOException {
        final var loader = Utils.fxmlLoader("/add-service-fee.fxml");
        final Parent content = loader.load();
        final AddServiceFeeController controller = loader.getController();
        final var stage = new Stage();
        if(fee == null) {
            fee = new ServiceFee(ServiceFee.NULL_ID, "", null);
        }

        stage.initOwner(window);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(content));
        controller.setStage(stage);
        controller.setServiceFee(fee);
        stage.showAndWait();
        return controller.getServiceFee();
    }

    public void setServiceFee(ServiceFee fee) {
        this.fee = fee;
        nameField.setText(fee.getName());
        formulaField.setText(fee.getFormula().getExpression());
        for(final var v : fee.getFormula().getVariables()) {
            newVariable(v.getKey(), v.getValue());
        }
    }

    private ServiceFee getServiceFee() {
        return fee;
    }

    private void setStage(Stage stage) {
        this.stage = stage;
    }
}

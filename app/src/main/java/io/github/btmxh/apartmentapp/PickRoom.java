package io.github.btmxh.apartmentapp;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.sql.SQLException;

public class PickRoom {
    private Stage stage;
    public TextField searchBar;
    public ListView<String> result;
    private String ans;

    public void initialize() {
        result.setCellFactory(l -> new ListCell<>(){
            @Override
            protected void updateItem(String room, boolean b) {
                super.updateItem(room, b);
                if(b || room == null) {
                    setText("");
                } else {
                    setText(room);
                }
            }
        });
        searchBar.textProperty().addListener((a1, a2, value) -> {
            try {
                updateItems(value);
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            updateItems("");
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateItems(String query) throws SQLException, IOException {
        final var ans = DatabaseConnection.getInstance().getRooms(query);
        result.setItems(FXCollections.observableArrayList(ans));
    }

    public void submit(ActionEvent actionEvent) {
        if(result.getSelectionModel().getSelectedItem() == null) {
            Announcement.show("Lỗi", "Chưa chọn căn hộ nào", "Hãy nhấn chọn căn hộ trong bảng kết quả");
            return;
        }

        ans = result.getSelectionModel().getSelectedItem();
        stage.close();
    }

    public void cancel(ActionEvent actionEvent) {
        stage.close();
    }

    public void setStage(Stage stage) {
        searchBar.setText("");
        this.stage = stage;
    }

    public String getAns() {
        return ans;
    }

    public static String open(Window window) throws IOException {
        final var loader = Utils.fxmlLoader("/pick-room.fxml");
        final Parent content = loader.load();
        final PickRoom controller = loader.getController();
        final var stage = new Stage();

        stage.initOwner(window);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(content));
        controller.setStage(stage);
        stage.showAndWait();

        return controller.getAns();
    }
}


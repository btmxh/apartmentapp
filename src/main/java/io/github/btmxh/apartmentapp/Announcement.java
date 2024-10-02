package io.github.btmxh.apartmentapp;

import javafx.scene.control.Alert;

public class Announcement {

    public static void show(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

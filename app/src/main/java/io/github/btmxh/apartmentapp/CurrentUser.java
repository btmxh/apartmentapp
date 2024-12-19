package io.github.btmxh.apartmentapp;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;


public class CurrentUser {

    private final StringProperty username = new SimpleStringProperty();

    public CurrentUser(String username) {
        this.username.set(username);
    }

    public StringProperty getUsername() {
        return username;
    }
}
package io.github.btmxh.apartmentapp;

import java.beans.ConstructorProperties;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;


public class User {

    private final StringProperty username = new SimpleStringProperty();

    @ConstructorProperties({"username"})
    public User(String username) {
        this.username.set(username);
    }

    public StringProperty getUsername() {
        return username;
    }
}
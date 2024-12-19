package io.github.btmxh.apartmentapp;

import io.github.btmxh.apartmentapp.DatabaseConnection.Role;
import javafx.beans.property.SimpleStringProperty;

public class User {
    private int id;
    private String name;
    private SimpleStringProperty fullname = new SimpleStringProperty();
    private String phoneNum;
    private Role role;

    public User(int id, String name, String fullname, String phoneNum, Role role) {
        this.id = id;
        this.name = name;
        this.fullname.set(fullname);
        this.phoneNum = phoneNum;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFullname() {
        return fullname.get();
    }

    public SimpleStringProperty fullname() {
        return fullname;
    }

    public String getPhoneNum() {
        return phoneNum;
    }
}

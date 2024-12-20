package io.github.btmxh.apartmentapp;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Room {
    public static final int NULL_ID = -1;
    private int id = NULL_ID;
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty ownerName = new SimpleStringProperty();
    private SimpleFloatProperty area = new SimpleFloatProperty();
    private SimpleIntegerProperty numMotors = new SimpleIntegerProperty();
    private SimpleIntegerProperty numCars = new SimpleIntegerProperty();

    public Room(int id, String name, String ownerName, float area, int numMotors, int numCars) {
        this.id = id;
        this.name.set(name);
        this.ownerName.set(ownerName);
        this.area.set(area);
        this.numMotors.set(numMotors);
        this.numCars.set(numCars);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty name() {
        return name;
    }

    public float getArea() {
        return area.get();
    }

    public void setArea(float area) {
        this.area.set(area);
    }

    public String getOwnerName() {
        return ownerName.get();
    }

    public void setOwnerName(String ownerName) {
        this.ownerName.set(ownerName);
    }

    public int getNumCars() {
        return numCars.get();
    }

    public void setNumCars(int numCars) {
        this.numCars.set(numCars);
    }

    public int getNumMotors() {
        return numMotors.get();
    }

    public void setNumMotors(int numMotors) {
        this.numMotors.set(numMotors);
    }
}


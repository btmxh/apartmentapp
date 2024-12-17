package io.github.btmxh.apartmentapp;

public class Room {
    public static final int NULL_ID = -1;
    private int id = NULL_ID;
    private String name;
    private int ownerId;
    private float area;
    private int numMotors;
    private int numCars;

    public Room(int id, String name, int ownerId, float area, int numMotors, int numCars) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.area = area;
        this.numMotors = numMotors;
        this.numCars = numCars;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getNumCars() {
        return numCars;
    }

    public void setNumCars(int numCars) {
        this.numCars = numCars;
    }

    public int getNumMotors() {
        return numMotors;
    }

    public void setNumMotors(int numMotors) {
        this.numMotors = numMotors;
    }
}


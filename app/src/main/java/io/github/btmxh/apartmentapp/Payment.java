package io.github.btmxh.apartmentapp;

import javafx.beans.property.*;
import java.time.LocalDateTime;

public class Payment {
    public static final int NULL_ID = -1;
    private int id = -1;
    private final SimpleObjectProperty<ServiceFee> fee = new SimpleObjectProperty<>();
    private final SimpleStringProperty roomId = new SimpleStringProperty();
    private final SimpleLongProperty amount = new SimpleLongProperty();
    private final SimpleObjectProperty<LocalDateTime> committedTimestamp = new SimpleObjectProperty<>();
    private final SimpleStringProperty roomOwner = new SimpleStringProperty(); // Normal StringProperty for roomOwner
    private final SimpleObjectProperty<User> user = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<Room> room = new SimpleObjectProperty<>();


    public Payment(int id, ServiceFee fee, String roomId, long amount, LocalDateTime committedTimestamp, String roomOwner) {
        this.id = id;
        this.fee.set(fee);
        this.roomId.set(roomId);
        this.amount.set(amount);
        this.committedTimestamp.set(committedTimestamp);
        this.roomOwner.set(roomOwner);// Initialize roomOwner
    }

    public Payment(int id, ServiceFee fee, Room room, long amount, LocalDateTime committedTimestamp, User user) {
        this.id = id;
        this.fee.set(fee);
        this.room.set(room);
        this.amount.set(amount);
        this.committedTimestamp.set(committedTimestamp);
        this.user.set(user);// Initialize roomOwner
    }

    public Room getRoom() {
        return room.get();
    }

    public void setRoom(Room room) {
        this.room.set(room);
    }

    public User getUser() {
        return user.get();
    }

    public void setUser(User user) {
        this.user.set(user);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters and Setters for feeId
    public ServiceFee getFee() {
        return fee.get();
    }

    public void setFee(ServiceFee fee) {
        this.fee.set(fee);
    }

    // Getters and Setters for roomId
    public String getRoomId() {
        return roomId.get();
    }

    public void setRoomId(String roomId) {
        this.roomId.set(roomId);
    }

    public StringProperty roomIdProperty() {
        return roomId;
    }

    // Getters and Setters for amount
    public long getAmount() {
        return amount.get();
    }

    public void setAmount(long amount) {
        this.amount.set(amount);
    }

    public LongProperty amountProperty() {
        return amount;
    }

    // Getters and Setters for committedTimestamp
    public LocalDateTime getCommittedTimestamp() {
        return committedTimestamp.get();
    }

    public void setCommittedTimestamp(LocalDateTime committedTimestamp) {
        this.committedTimestamp.set(committedTimestamp);
    }

    public ObjectProperty<LocalDateTime> committedTimestampProperty() {
        return committedTimestamp;
    }

    // Getters and Setters for roomOwner
    public String getRoomOwner() {
        return roomOwner.get();
    }

    public void setRoomOwner(String roomOwner) {
        this.roomOwner.set(roomOwner);
    }

    public StringProperty roomOwnerProperty() {
        return roomOwner;
    }
}

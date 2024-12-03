package io.github.btmxh.apartmentapp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Citizen {
    private int id;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String passportId;
    private String nationality;
    private String room;
    private boolean owner;
    private LocalDateTime createdAt, updatedAt;

    public Citizen(int id, String fullName, LocalDate dateOfBirth, Gender gender, String passportId, String nationality, String room, boolean owner, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.passportId = passportId;
        this.nationality = nationality;
        this.room = room;
        this.owner = owner;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPassportId() {
        return passportId;
    }

    public String getNationality() {
        return nationality;
    }

    public String getRoom() {
        return room;
    }

    public boolean isOwner() {
        return owner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public enum Gender {
        MALE,
        FEMALE,
        OTHER;
    }


}

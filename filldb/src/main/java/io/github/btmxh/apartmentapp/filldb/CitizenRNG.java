package io.github.btmxh.apartmentapp.filldb;

import io.github.btmxh.apartmentapp.Citizen;
import io.github.btmxh.apartmentapp.DatabaseConnection.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;


public class CitizenRNG {

    public static List<Citizen> generateCitizens(int room) {
        var rng = ThreadLocalRandom.current();
        return IntStream.range(0, rng.nextInt(1, 3)).mapToObj(_ijk -> RandomVietnameseName.generateName()).map(name -> new Citizen(-1, name, generateRandomDateOfBirth(), generateRandomGender(), generateRandomPassportId(), "Vietnam", String.valueOf(room), LocalDateTime.now().minusDays(rng.nextInt(10, 1000)), LocalDateTime.now())).toList();
    }

    private static LocalDate generateRandomDateOfBirth() {
        int year = 1950 + (int) (Math.random() * 50); // Random year between 1950 and 2000
        int month = 1 + (int) (Math.random() * 12);  // Random month
        int day = 1 + (int) (Math.random() * 28);    // Random day
        return LocalDate.of(year, month, day);
    }

    private static Gender generateRandomGender() {
        Gender[] genders = Gender.values();
        return genders[(int) (Math.random() * genders.length)];
    }

    private static String generateRandomPassportId() {
        return String.format("%012d", (long) (Math.random() * 1_000_000_000_000L));
    }
}

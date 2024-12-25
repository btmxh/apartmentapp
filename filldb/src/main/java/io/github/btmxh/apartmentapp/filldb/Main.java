package io.github.btmxh.apartmentapp.filldb;

import io.github.btmxh.apartmentapp.DatabaseConnection;
import io.github.btmxh.apartmentapp.Payment;
import io.github.btmxh.apartmentapp.Room;
import io.github.btmxh.apartmentapp.ServiceFee;
import io.github.btmxh.apartmentapp.DatabaseConnection.FeeType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) throws Exception {
        final var db = DatabaseConnection.getInstance();
        db.createUsersTable();
        db.createServiceFeeTable();
        db.createPaymentsTable();
        db.createCitizensTable();
        db.signup("admin", "Trần Hùng Cường", "0984849343", "admin");
        db.signup("huycan", "Vũ Quang Huy", "0984849343", "chimcut");
        db.signup("dbl", "Đặng Bảo Long", "0984849343", "dabuolo");
        db.signup("leducanh", "Lê Đức Anh",   "0987654321", "ducanh123");
        db.signup("method123", "Phạm Nhật Minh",   "0987456321", "kizunamethod");
        db.signup("hat", "Hoàng Anh Tú", "0987452981", "hatxulemicu");
        var users = db.getNonAdminUserList(1000, 0);
        final var fees = new ArrayList<ServiceFee>();
        for(int m = 5; m <= 11; ++m) {
            fees.add(new ServiceFee(
                    -1,
                    FeeType.MANAGEMENT,
                    "Tiền quản lý tháng " + m,
                    ThreadLocalRandom.current().nextInt(20, 25) * 1000L,
                    0,
                    LocalDate.of(2024, m, 25),
                    LocalDate.of(2024, m + 1, 5)
            ));
            fees.add(new ServiceFee(
                    -1,
                    FeeType.SERVICE,
                    "Tiền dịch vụ tháng " + m,
                    ThreadLocalRandom.current().nextInt(5, 10) * 1000L,
                    0,
                    LocalDate.of(2024, m, 25),
                    LocalDate.of(2024, m + 1, 5)
            ));
            fees.add(new ServiceFee(
                    -1,
                    FeeType.PARKING,
                    "Tiền gửi xe tháng " + m,
                    ThreadLocalRandom.current().nextInt(100, 200) * 1000L,
                    ThreadLocalRandom.current().nextInt(1000, 1500) * 1000L,
                    LocalDate.of(2024, m, 25),
                    LocalDate.of(2024, m + 1, 5)
            ));
            fees.add(new ServiceFee(
                    -1,
                    FeeType.PARKING,
                    "Tiền đóng góp tháng " + m,
                    0, 0,
                    LocalDate.of(2024, m, 25),
                    LocalDate.of(2024, m + 1, 5)
            ));
        }
        for(int floor = 1; floor <= 10; ++floor) {
            for(int num = 1; num <= 21; ++num) {
                int roomNum = floor * 100 + num;
                var citizens = CitizenRNG.generateCitizens(roomNum);
                for(var citizen : citizens) db.addCitizenToDB(citizen);
                var room = new Room(-1, "Phòng " + roomNum, citizens.getFirst().getFullName(), ThreadLocalRandom.current().nextInt(14, 621) / 2, ThreadLocalRandom.current().nextInt(1, 4), ThreadLocalRandom.current().nextInt(1, 4));
                db.updateRoom(room);
                for(var fee : fees) {
                    var user = users.get(ThreadLocalRandom.current().nextInt(users.size()));
                    db.updatePayment(new Payment(-1, fee, user, room, calcValue(fee, room), LocalDateTime.now()));
                }
            }
        }
        for(final var fee : fees) {
            db.updateServiceFee(fee);
        }
    }

    private static long calcValue(ServiceFee fee, Room room) {
        return switch (fee.getType()) {
            case MANAGEMENT, SERVICE -> (long) (fee.getValue1() * room.getArea());
            case PARKING -> fee.getValue1() * room.getNumMotors() + fee.getValue2() * room.getNumCars();
            case DONATION -> ThreadLocalRandom.current().nextInt(50000, 100000);
        };
    }
}

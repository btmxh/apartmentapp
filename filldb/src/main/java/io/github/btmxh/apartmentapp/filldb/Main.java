package io.github.btmxh.apartmentapp.filldb;

import io.github.btmxh.apartmentapp.DatabaseConnection;
import io.github.btmxh.apartmentapp.Payment;
import io.github.btmxh.apartmentapp.ServiceFee;

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
        final var fees = new ArrayList<ServiceFee>();
        for(int i = 10; i >= 0; --i) {
            final var ym = YearMonth.now().minusMonths(i);
            final var nextYm = ym.plusMonths(1);
            final var m = ym.getMonthValue();
            fees.add(new ServiceFee(
                    -1,
                    "Tiền nhà tháng " + m, "",
                    ThreadLocalRandom.current().nextInt(40, 50) * 100000L,
                    0,
                    ym.atDay(25), nextYm.atDay(5), 0, 0
            ));
            fees.add(new ServiceFee(
                    -1,
                    "Tiền nước tháng " + m," ",
                    ThreadLocalRandom.current().nextInt(80, 120) * 1000L, 0,
                    ym.atDay(25), nextYm.atDay(5)
            ));
            fees.add(new ServiceFee(
                    -1,
                    "Tiền gửi xe tháng " + m,"",
                    ThreadLocalRandom.current().nextInt(100, 200) * 1000L, 0,
                    ym.atDay(25), nextYm.atDay(5)
            ));
            fees.add(new ServiceFee(
                    -1,
                    "Tiền dịch vụ tháng " + m,"",
                    ThreadLocalRandom.current().nextInt(100, 200) * 1000L, 0,
                    ym.atDay(25), nextYm.atDay(5)
            ));
            fees.add(new ServiceFee(
                    -1,
                    "Tiền tình nguyện tháng " + m,
                    "",-1, 0,
                    ym.atDay(25), nextYm.atDay(5)
            ));
        }

        final var set = new HashSet<String>();
        for(final var citizen : CitizenRNG.generateCitizens()) {
            db.addCitizenToDB(citizen);
            set.add(citizen.getRoom());
        }
    }

}

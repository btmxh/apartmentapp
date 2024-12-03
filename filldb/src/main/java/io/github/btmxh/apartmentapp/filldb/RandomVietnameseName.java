package io.github.btmxh.apartmentapp.filldb;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomVietnameseName {

    private static final String[] HO = {
            "Nguyễn", "Trần", "Lê", "Phạm", "Hoàng", "Huỳnh", "Phan", "Vũ", "Võ", "Đặng",
            "Bùi", "Đỗ", "Hồ", "Ngô", "Dương", "Lý"
    };

    private static final String[] TEN_DEM = {
            "Thị", "Văn", "Hữu", "Ngọc", "Minh", "Đức", "Thanh", "Hoài", "Tấn", "Gia",
            "Quốc", "Xuân", "Bảo", "Anh", "Công"
    };

    private static final String[] TEN = {
            "Hưng", "Dũng", "Quang", "Tùng", "Trang", "Lan", "Phương", "Hải", "Hương",
            "Linh", "Sơn", "Hiếu", "Anh", "Tuấn", "Hạnh", "Bình", "Nhung", "Hoa", "Khánh",
            "Ngân", "Khoa", "Vy", "Thảo", "Phúc", "Thịnh"
    };

    private static final Random random = ThreadLocalRandom.current();

    public static String generateName() {
        String ho = HO[random.nextInt(HO.length)];
        String tenDem = TEN_DEM[random.nextInt(TEN_DEM.length)];
        String ten = TEN[random.nextInt(TEN.length)];
        return ho + " " + tenDem + " " + ten;
    }
}

package io.github.btmxh.apartmentapp.filldb;

import io.github.btmxh.apartmentapp.DatabaseConnection;
import io.github.btmxh.apartmentapp.ServiceFee;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        final var db = DatabaseConnection.getInstance();
        db.createUsersTable();
        db.createServiceFeeTable();
        db.createPaymentsTable();
        db.signup("admin", "admin@aa.com", "0123456789", "admin");
        db.signup("b", "b@b.com", "0987654321", "resident");
        db.signup("c", "c@c.com", "1234567890", "resident");
        db.signup("d", "d@d.com", "2345678901", "resident");
        db.signup("e", "e@e.com", "3456789012", "resident");
        db.signup("f", "f@f.com", "4567890123", "resident");
        db.signup("g", "g@g.com", "5678901234", "resident");
        db.signup("h", "h@h.com", "6789012345", "resident");
        db.signup("i", "i@i.com", "7890123456", "resident");
        db.signup("j", "j@j.com", "8901234567", "resident");
        db.signup("k", "k@k.com", "9012345678", "resident");
        db.signup("l", "l@l.com", "0123445566", "resident");
        db.signup("m", "m@m.com", "1234556677", "resident");
        db.signup("n", "n@n.com", "2345667788", "resident");
        db.signup("o", "o@o.com", "3456778899", "resident");
        db.signup("p", "p@p.com", "4567889900", "resident");
        db.signup("q", "q@q.com", "5678990011", "resident");
        db.signup("r", "r@r.com", "6789001122", "resident");
        db.signup("s", "s@s.com", "7890112233", "resident");
        db.signup("t", "t@t.com", "8901223344", "resident");
        db.signup("u", "u@u.com", "9012334455", "resident");
        db.signup("v", "v@v.com", "0123445566", "resident");
        db.signup("w", "w@w.com", "1234556677", "resident");
        db.signup("x", "x@x.com", "2345667788", "resident");
        db.signup("y", "y@y.com", "3456778899", "resident");
        db.signup("z", "z@z.com", "4567889900", "resident");
        db.updateServiceFee(new ServiceFee(), "Tien cuu hoc bong thang 11", new ServiceFee.Formula("1 + 1", Map.of()));
        db.updateServiceFee(new ServiceFee(), "Tien cuu hoc bong thang 12", new ServiceFee.Formula("a + 1", Map.of("a", new ServiceFee.ConstFormulaTerminal(727))));
    }
}

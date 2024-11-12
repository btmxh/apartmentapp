package io.github.btmxh.apartmentapp;

import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.Month;
import java.util.function.Function;

public class Utils {
    public static <T> StringConverter<T> readOnlyStringConverter(Function<T, String> toString) {
        return new StringConverter<>() {
            @Override
            public String toString(T t) {
                return toString.apply(t);
            }

            @Override
            public T fromString(String s) {
                throw new UnsupportedOperationException("Only read-only operation supported");
            }
        };
    }

    public static FXMLLoader fxmlLoader(String path) {
        return new FXMLLoader(Utils.class.getResource(path));
    }

    public static void initMonthComboBox(ComboBox<Month> monthBox) {
        monthBox.setItems(FXCollections.observableArrayList(Month.values()));
        monthBox.setValue(LocalDate.now().getMonth());
        monthBox.setConverter(Utils.readOnlyStringConverter(m -> "Tháng " + m.getValue()));
    }

    public static <S, T> void initNoColumn(TableColumn<S, T> numCol, int offset) {
        numCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(T value, boolean b) {
                super.updateItem(value, b);

                if (getTableRow() != null && value != null) {
                    setText((getTableRow().getIndex() + 1 + offset) + "");
                } else {
                    setText("");
                }
            }
        });
    }
}

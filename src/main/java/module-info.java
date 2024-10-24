module com.example.ejercicioi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.ejercicioi to javafx.fxml;
    exports com.example.ejercicioi;
}
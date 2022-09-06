module com.example.assignment1487wgui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.assignment1487wgui to javafx.fxml;
    exports com.example.assignment1487wgui;
}
module com.example.mouseracegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.mouseracegame to javafx.fxml;
    exports com.example.mouseracegame;
    exports com.example.element;
    opens com.example.element to javafx.fxml;
}
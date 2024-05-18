module com.checkers.checkers {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.checkers to javafx.fxml;
    exports com.checkers;
}
module com.checkers.checkers {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    exports com.checkers.client;
    opens com.checkers.client to javafx.fxml;
    exports com.checkers.client.mechanics;
    opens com.checkers.client.mechanics to javafx.fxml;
    exports com.checkers.client.ui.views;
    opens com.checkers.client.ui.views to javafx.fxml;
    exports com.checkers.client.ui.elements;
    opens com.checkers.client.ui.elements to javafx.fxml;
    exports com.checkers.client.mechanics.game_logic.move;
    opens com.checkers.client.mechanics.game_logic.move to javafx.fxml;
    exports com.checkers.client.mechanics.game_logic.capture;
    opens com.checkers.client.mechanics.game_logic.capture to javafx.fxml;
    exports com.checkers.client.mechanics.game_logic.ai;
    opens com.checkers.client.mechanics.game_logic.ai to javafx.fxml;
    exports com.checkers.client.mechanics.sound;
    opens com.checkers.client.mechanics.sound to javafx.fxml;
    exports com.checkers.communicationClientServer.elementsDTO;
    opens com.checkers.communicationClientServer.elementsDTO to javafx.fxml;
    exports com.checkers.communicationClientServer.connectionInformation;
    opens com.checkers.communicationClientServer.connectionInformation to javafx.fxml;
}
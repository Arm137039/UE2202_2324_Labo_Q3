
module ue2202_2324_labo_q3 {
    requires java.desktop;
    requires javafx.swing;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    exports client;
    opens client to javafx.fxml;
    exports client.controller;
    opens client.controller to javafx.fxml;
    exports client.model;
    opens client.model to javafx.fxml;
    exports client.view;
    opens client.view to javafx.fxml;
}

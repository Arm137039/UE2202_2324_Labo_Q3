package client.view;

import client.controller.ClientController;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientView {
    private final Stage stage;
    private ClientController controller;
    private TextArea messageArea;
    private TextField inputField;

    public ClientView(Stage stage) {
        this.stage = stage;
        initialize();
    }

    private void initialize() {
        VBox root = new VBox();
        messageArea = new TextArea();
        messageArea.setEditable(false);
        inputField = new TextField();
        inputField.setOnAction(e -> controller.sendMessage(inputField.getText()));

        root.getChildren().addAll(messageArea, inputField);
        stage.setScene(new Scene(root, 400, 300));
        stage.setTitle("Pictionary Client");
    }

    public void setController(ClientController controller) {
        this.controller = controller;
    }

    public void appendMessage(String message) {
        messageArea.appendText(message + "\n");
    }

    public void show() {
        stage.show();
    }
}

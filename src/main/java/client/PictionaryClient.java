package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import client.controller.ClientController;
import client.model.ClientModel;
import client.view.ClientView;

public class PictionaryClient extends Application {

    private static final String HOST = "localhost";
    private static final int PORT = 3001;
    private ClientModel model;
    private ClientView view;
    private ClientController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        model = new ClientModel();
        view = new ClientView(primaryStage);
        controller = new ClientController(model, view, HOST, PORT);

    }
}

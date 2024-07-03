package client;

import javafx.application.Application;
import javafx.stage.Stage;
import client.controller.ClientController;
import client.model.ClientModel;
import client.view.ClientView;

public class PictionaryClient extends Application {
    @Override
    public void start(Stage primaryStage) {
        ClientModel model = new ClientModel();
        ClientView view = new ClientView(primaryStage);
        ClientController controller = new ClientController(model, view);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

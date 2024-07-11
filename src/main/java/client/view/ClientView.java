package client.view;

import client.controller.ClientController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.IOException;


public class ClientView{
    private Stage stage;
    private ClientController controller;

    @FXML
    private Label scoreMessage;

    public ClientView() {

    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void setController(ClientController controller) {
        this.controller = controller;
    }

    public void showWaitView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("waitView.fxml"));
            Parent root = loader.load();

            Scene waitScene = new Scene(root, 600, 400);
            Platform.runLater(() -> {
                stage.setScene(waitScene);
                stage.setTitle("Attente...");
                stage.show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showEndGame() {
        stage.close();
        stage = new Stage();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("endGameView.fxml"));
            Parent root = loader.load();


            Scene endScene = new Scene(root, 580, 500);

            Platform.runLater(() -> {
                stage.setScene(endScene);
                stage.setTitle("Partie terminée");
                stage.show();
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateScore(int newScore) {
        Platform.runLater(() -> this.scoreMessage.setText(
                "Votre score final est de " + newScore + " points !"
        ));
    }
}

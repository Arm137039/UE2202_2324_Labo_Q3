package client.view;

import client.controller.ClientController;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;

public abstract class ClientViewAbstract {

    protected Stage stage;
    protected ClientController controller;
    protected List<Canvas> canvasList = new ArrayList<>();
    protected List<TextField> textFieldList = new ArrayList<>();

    @FXML
    private Label scoreMessage;
    @FXML
    private Label endMessage;


    public ClientViewAbstract() {
    }

    public void initialize(Stage stage) {
        this.stage = stage;
    }

    public void setController(ClientController controller) {
        this.controller = controller;
    }

    public abstract void setStage(Stage stage);
    public void updateScore(int newScore) {
        Platform.runLater(() -> {
            this.scoreMessage.setText("Votre score final est de " + newScore + " points !");
        });
    }

    ///////////////////////////////////////////////

    public abstract void showGameView();

    protected abstract void createNewDraw();

    public abstract void updateCanvas(String base64String, int index);

    public abstract void updateTestField(int number);
}
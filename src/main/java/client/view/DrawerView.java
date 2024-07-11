package client.view;

import java.util.ArrayList;

import client.controller.ClientController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.control.ProgressBar;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import javafx.application.Platform;
import javafx.util.Duration;


public class DrawerView extends ClientViewAbstract {

    private Stage stage;
    private ClientController controller;
    private Label score = new Label();
    private List<Canvas> canvasList = new ArrayList<>();
    private List<TextField> textFieldList = new ArrayList<>();
    private int points = 0;

    @FXML
    private Label scoreMessage;
    @FXML
    private Label endMessage;
    private VBox pairsContainer;

    public DrawerView() {

    }

    @FXML
    public void initialize() {
        canvasList.clear();
        textFieldList.clear();
    }
    @Override
    public void showGameView() {
        stage.close();
        stage = new Stage();
        BorderPane root = new BorderPane();

        ProgressBar timeBar = new ProgressBar(0);
        timeBar.setMaxWidth(Double.MAX_VALUE);
        timeBar.setPadding(new Insets(0, 10, 50, 0)); // Adjust padding to add space between time bar and score label

        Label scoreLabel = new Label("Score: 0"); // Initialize the score label with a default score
        this.score = scoreLabel; // Assuming 'this.score' is a Label defined in your class to keep track of the score

        HBox topContainer = new HBox(10, timeBar, scoreLabel); // Create a container for the time bar and score label
        topContainer.setAlignment(Pos.CENTER_LEFT); // Align items to the left of the container

        //canvasList.clear(); // Uncomment if you want to clear the canvas list before adding new canvases
        canvasList.add(new Canvas(500, 500));
        canvasList.add(new Canvas(500, 500));
        canvasList.add(new Canvas(500, 500));
        canvasList.forEach(this::initializeCanvas); // Initialize each canvas

        final int[] canvasCounter = {0}; // Use an array for mutable integer

        VBox layout = new VBox(topContainer); // Add the top container to the VBox layout
        layout.getChildren().add(canvasList.get(0)); // Initially add the first canvas

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if(timeBar.getProgress() <= 0.9){
                timeBar.setProgress(timeBar.getProgress() + 0.1);
            }else{
                timeBar.setProgress(0);
                layout.getChildren().remove(1); // Remove the current canvas
                canvasCounter[0] = (canvasCounter[0] + 1) % canvasList.size(); // Cycle through the canvas list
                layout.getChildren().add(canvasList.get(canvasCounter[0])); // Add the next canvas
                // Update score or any other action when cycling through canvases
                controller.sendMessage((canvasCounter[0]*2));
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(0, 0, 40, 35));
        root.setBottom(layout);

        Scene scene = new Scene(root, 850, 720);
        stage.setScene(scene);
        stage.setTitle("Dessinez");
        stage.show();
    }

    @Override
    protected void createNewDraw() {

    }
    @Override
    public void updateCanvas(String base64String, int index) {

    }
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @Override
    public void updateTestField(int index) {
    }

    public void setController(ClientController controller) {
        this.controller = controller;
    }

    public void updateScore(int newScore) {
        Platform.runLater(() -> this.scoreMessage.setText(
                "Votre score final est de " + newScore + " points !"
        ));
    }

    private void initializeCanvas(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK); // Set canvas background to black
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Fill the entire canvas with black
        gc.setLineWidth(2);
        gc.setStroke(Color.WHITE); // Set drawing color to white

        canvas.setOnMousePressed(e -> {
            gc.beginPath();
            gc.moveTo(e.getX(), e.getY());
            gc.stroke();
        });

        canvas.setOnMouseDragged(e -> {
            gc.lineTo(e.getX(), e.getY());
            gc.stroke();

            String coordinates = e.getX() + "," + e.getY();
            controller.sendMessage(coordinates);
        });
    }

}

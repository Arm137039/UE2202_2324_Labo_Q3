package client.view;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import client.controller.ClientController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.control.ProgressBar;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import javafx.scene.image.WritableImage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;


public class ClientView {
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

    public ClientView() {

    }

    @FXML
    public void initialize() {
        canvasList.clear();
        textFieldList.clear();
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

    public void showDrawerView() {
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


    public void showGuesserView() {
        stage = new Stage();
        BorderPane root = new BorderPane();

        // Container for Canvas + TextField pairs
        this.pairsContainer = new VBox(10);
        pairsContainer.setAlignment(Pos.CENTER);

        createNewDraw();

        // ScrollPane to allow scrolling if there are many pairs
        ScrollPane scrollPane = new ScrollPane(pairsContainer);
        scrollPane.setFitToWidth(true);

        root.setCenter(scrollPane);

        // Configuration de la ProgressBar pour qu'elle prenne toute la largeur
        ProgressBar timeBar = new ProgressBar(0);
        timeBar.setMaxWidth(Double.MAX_VALUE);
        timeBar.setPadding(new Insets(20));
        root.setBottom(timeBar);

        // Timeline pour mettre à jour la ProgressBar
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeBar.setProgress(timeBar.getProgress() + (double) 1 / 60);
        }));

        timeline.setCycleCount(60);
        timeline.play();

        Scene scene = new Scene(root, 800, 655);
        stage.setScene(scene);
        stage.setTitle("Devinez");
        stage.show();
    }
    private void createNewDraw(){
        Canvas canvas = new Canvas(200, 200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setLineWidth(1);
        gc.setStroke(Color.WHITE);
        canvasList.add(canvas);

        // Création et configuration du TextField correspondant
        TextField textField = new TextField();
        textField.setPromptText("Entrez votre mot :");
        textFieldList.add(textField); // Ajout du TextField à la liste

        int osef = textFieldList.size()-1;
        textField.setOnKeyReleased(e -> {
            System.out.println("Envoi de messages : " + osef + ", " + textField.getText());
            this.controller.sendMessage((osef*2)+1);
            this.controller.sendMessage(textField.getText());
        });

        HBox pair = new HBox(10, canvas, textField);
        HBox.setHgrow(textField, Priority.ALWAYS);
        pair.setPadding(new Insets(10));
        pair.setAlignment(Pos.CENTER_LEFT);
        this.pairsContainer.getChildren().add(pair);
    }
    public void updateCanvas(String coordinatesString, int index) {
        try {
            // Parse the coordinates from the input string
            String[] coordinatesArray = coordinatesString.trim().split(",");
            double x = Double.parseDouble(coordinatesArray[0]);
            double y = Double.parseDouble(coordinatesArray[1]);

            // Scale the coordinates from 500x500 to 200x200
            double scaledX = x * 200 / 500;
            double scaledY = y * 200 / 500;

            // Ensure the canvasList is large enough for the given index
            // If not, create new draw(s) until it is
            if (index >= canvasList.size()) {
                Platform.runLater(() -> createNewDraw());
            }

            // Update the canvas on the JavaFX thread
            Platform.runLater(() -> {
                try {
                    Canvas canvas = canvasList.get(index);
                    GraphicsContext gc = canvas.getGraphicsContext2D();
                    gc.beginPath();
                    gc.lineTo(scaledX, scaledY);
                    gc.stroke();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to update canvas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateTestField(int number){
        TextField textField = this.textFieldList.get(number);
        textField.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        textField.setDisable(true);
    }
}

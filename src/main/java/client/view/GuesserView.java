package client.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;


public class GuesserView extends ClientViewAbstract {
    private VBox pairsContainer;

    public GuesserView(){
        super();
    }
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @Override
    public void showGameView() {
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
    @Override
    public void createNewDraw(){
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
    @Override
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
    @Override
    public void updateTestField(int number){
        TextField textField = this.textFieldList.get(number);
        textField.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        textField.setDisable(true);
    }
}

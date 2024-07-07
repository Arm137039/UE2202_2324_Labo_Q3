package client.view;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import client.controller.ClientController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;
import javafx.scene.image.WritableImage;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;


public class ClientView {
    private Stage stage;
    private ClientController controller;
    private TextArea messageArea;
    private TextField inputField;
    private Canvas canvas;

    public ClientView(Stage stage) {
        this.stage = stage;
        this.canvas = new Canvas();
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
    public void showWaitView() {
        // Create a VBox layout with spacing and center alignment
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        // Create a label with the waiting message
        Label waitMessage = new Label("En attente d'un coéquipier ...");
        waitMessage.setFont(new Font("Arial", 25)); // Set font size and family for better visibility

        // Add the label to the layout
        layout.getChildren().add(waitMessage);

        // Create a new scene with the layout
        Scene waitScene = new Scene(layout, 600, 400);

        // Apply the scene to the stage
        Platform.runLater(() -> {
            stage.setScene(waitScene);
            stage.setTitle("Attente...");
            stage.show();
        });
    }


    public void showDrawerView() {
        stage.close();
        stage = new Stage();
        BorderPane root = new BorderPane();

        // Canvas setup
        canvas = new Canvas(500, 500);
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
        });

        canvas.setOnMouseReleased(e -> {
            try {
                // Capture du contenu du Canvas dans une BufferedImage
                WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                canvas.snapshot(null, writableImage);
                BufferedImage originalImage = SwingFXUtils.fromFXImage(writableImage, null);

                // Redimensionnement de l'image
                BufferedImage resizedImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
                java.awt.Graphics2D g2d = resizedImage.createGraphics();
                g2d.drawImage(originalImage, 0, 0, 200, 200, null);
                g2d.dispose();

                // Conversion de BufferedImage en tableau de bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(resizedImage, "png", baos);
                byte[] imageBytes = baos.toByteArray();

                // Encodage du tableau de bytes en Base64
                String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

                // Envoi de l'image encodée au serveur
                controller.sendMessage(encodedImage);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Layout setup
        root.setCenter(canvas); // Place canvas in the center of the BorderPane
        Scene scene = new Scene(root, 850, 720);
        stage.setScene(scene);
        stage.setTitle("Dessinez");
        stage.show();
    }


    public void showGuesserView() {
        stage = new Stage();
        BorderPane root = new BorderPane();

        // Configuration du Canvas
        canvas.setHeight(200);
        canvas.setWidth(200);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK); // Fond noir pour le canvas
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Remplissage du fond

        StackPane canvasContainer = new StackPane(canvas);
        canvasContainer.setPadding(new Insets(10));
        canvasContainer.setAlignment(Pos.TOP_LEFT); // Alignement du Canvas au centre du StackPane

        // Configuration du TextField aligné avec le milieu droit du Canvas
        TextField messageArea = new TextField();
        messageArea.setPromptText("Entrez votre mot");
        VBox messageAreaContainer = new VBox(messageArea);
        messageAreaContainer.setAlignment(canvasContainer.getAlignment());
        messageAreaContainer.setPadding(new Insets(100,20,0,0));

        TextArea chatArea = this.messageArea;
        chatArea.setEditable(false);
        chatArea.setPrefHeight(200);
        root.setRight(chatArea);

        // Ajout du Canvas et du TextField au BorderPane
        root.setLeft(canvasContainer); // Canvas à gauche
        root.setCenter(messageAreaContainer); // TextField au centre

        // Configuration de la ProgressBar pour qu'elle prenne toute la largeur
        ProgressBar timeBar = new ProgressBar(0);
        timeBar.setMaxWidth(Double.MAX_VALUE); // Permet à la ProgressBar de s'étendre horizontalement
        timeBar.setPadding(new Insets(20));
        root.setBottom(timeBar);

        // Timeline pour mettre à jour la ProgressBar
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeBar.setProgress(timeBar.getProgress() + 0.1);
        }));

        timeline.setCycleCount(10); // Répéter l'action 10 fois
        timeline.play();

        // Configuration de la scène et du stage
        Scene scene = new Scene(root, 800, 655); // Taille ajustée pour inclure le Canvas et le TextArea
        stage.setScene(scene);
        stage.setTitle("Devinez");
        stage.show();
    }
    public void updateCanvas(String base64Data) {
        try {
            // Décodez la chaîne Base64 en tableau de bytes
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            // Convertissez le tableau de bytes en BufferedImage
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(bis);
            bis.close();

            // Convertissez BufferedImage en WritableImage
            WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);

            // Utilisez GraphicsContext du Canvas pour dessiner l'image
            Platform.runLater(() -> {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()); // Efface le contenu précédent
                gc.drawImage(writableImage, 0, 0, canvas.getWidth(), canvas.getHeight()); // Dessine l'image redimensionnée
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

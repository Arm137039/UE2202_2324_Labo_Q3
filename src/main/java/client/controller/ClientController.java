package client.controller;

import client.model.ClientModel;
import client.view.ClientView;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;

import java.io.IOException; // Import IOException

public class ClientController {
    private final ClientModel model;
    private final ClientView view;
    private final String host;
    private final int port;

    public ClientController(ClientModel model, ClientView view, String host, int port) {
        this.model = model;
        this.view = view;
        this.host = host;
        this.port = port;

        view.setController(this);

        tryConnectToServer();
        new Thread(() -> { startReceivingMessages();}).start();
    }

    private void startReceivingMessages() {
        while(true){
            try {
                Object message = model.receiveMessage();
                view.appendMessage("Received something");

                if (message instanceof String) {
                    view.updateCanvas((String) message);
                } else if (message instanceof Canvas) {
                    //view.updateCanvas(message);
                    view.appendMessage("Received canvas");
                } else {
                    // Handle other types or ignore
                }
                interpretMessage((String) message);
            } catch (IOException | ClassNotFoundException e) {
                view.appendMessage("Failed to receive message: " + e.getMessage());
            }
        }
    }
    private void interpretMessage(String message){
        Platform.runLater(() -> {
            switch (message){
                case "wait":
                    this.view.showWaitView();
                    break;
                case "drawer":
                    this.view.showDrawerView();
                    break;
                case "guesser":
                    this.view.showGuesserView();
                    break;
                default:
                    view.appendMessage(message);
                    break;
            }
        });
    }
    public void tryConnectToServer() {
        try {
            model.connectToServer(this.host, this.port);
            view.appendMessage("Connected to server.");
        } catch (IOException e) {
            view.appendMessage("Failed to connect to server: " + e.getMessage());
        }
    }
    private void sendCanvas(Canvas canvas){
        model.sendMessage(canvas);
    }

    public void sendMessage(Object message) {
        model.sendMessage(message);
    }
}

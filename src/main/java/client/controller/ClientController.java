package client.controller;

import client.model.ClientModel;
import client.view.ClientView;
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
        view.show();
        tryConnectToServer();
        startReceivingMessages();
    }

    private void startReceivingMessages() {
        new Thread(() -> {
            while(true){
                try {
                    view.appendMessage((String) model.receiveMessage());
                } catch (IOException | ClassNotFoundException e) {
                    view.appendMessage("Failed to receive message: " + e.getMessage());
                }
            }

        }).start();
    }
    public void tryConnectToServer() {
        try {
            model.connectToServer(this.host, this.port);
            view.appendMessage("Connected to server.");
        } catch (IOException e) {
            view.appendMessage("Failed to connect to server: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        model.sendMessage(message);
    }
}

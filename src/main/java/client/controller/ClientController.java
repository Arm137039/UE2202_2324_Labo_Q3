package client.controller;

import client.model.ClientModel;
import client.view.ClientView;
import java.io.IOException; // Import IOException

public class ClientController {
    private final ClientModel model;
    private final ClientView view;

    public ClientController(ClientModel model, ClientView view) {
        this.model = model;
        this.view = view;

        view.setController(this);
        view.show();
    }

    public void connectToServer(String host, int port) {
        try {
            model.connectToServer(host, port);
            view.appendMessage("Connected to server.");
        } catch (IOException e) {
            view.appendMessage("Failed to connect to server: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        model.sendMessage(message);
    }
}

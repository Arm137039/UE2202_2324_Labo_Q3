package server;

import java.io.*;
import java.net.*;
import java.util.List;
import common.network.ObjectSocket;

public class ClientHandler implements Runnable {
    //private Socket clientSocket;
    private PictionaryServer server;
    private ObjectSocket objectSocket;
    private String role;

    public ClientHandler(ObjectSocket objectSocket, PictionaryServer server) {
        //this.clientSocket = socket;
        this.objectSocket = objectSocket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            assignRole();

            Object inputObject;
            while ((inputObject = objectSocket.read()) != null) {
                String inputLine = (String) inputObject; // Assuming the object read is a String
                System.out.println("Received: " + inputLine);
                if ("quit".equalsIgnoreCase(inputLine)) {
                    break;
                }
                // Handle drawing and guessing logic here
                // ...
            }

            closeConnection();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void assignRole() {
        try {
            List<ClientHandler> clients = server.getClients();
            if (clients.size() % 2 != 0) {
                role = "drawer";
                objectSocket.write("You are the drawer.");
                sendWordsToDrawer();
            } else {
                role = "guesser";
                objectSocket.write("You are the guesser.");
            }
        } catch (IOException e) {
            System.err.println("Error assigning role: " + e.getMessage());
        }
    }

    private void sendWordsToDrawer() {
        try {
            objectSocket.write("Words: ");
            for (int i = 0; i < 3; i++) {
                objectSocket.write(server.getRandomWord());
            }
        } catch (IOException e) {
            System.err.println("Error sending words to drawer: " + e.getMessage());
        }
    }

    private void closeConnection() {
        objectSocket.close();
        server.removeClient(this);
    }
}

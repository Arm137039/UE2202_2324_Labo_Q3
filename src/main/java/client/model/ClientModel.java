package client.model;

import common.network.ObjectSocket;
import java.io.IOException;
import java.net.Socket;

public class ClientModel {
    private Socket socket;
    private ObjectSocket objectSocket;

    public void connectToServer(String host, int port) throws IOException {
        socket = new Socket(host, port);
        objectSocket = new ObjectSocket(socket);
    }

    public void sendMessage(Object message) {
        if (objectSocket != null) {
            try {
                objectSocket.write(message);
                objectSocket.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Object receiveMessage() {
        try {
            // Assuming 'in' is your ObjectInputStream
            return objectSocket.read();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error receiving message: " + e.getMessage());
            // Handle stream reset or recovery here
            return null; // Or handle appropriately
        }
    }

    public void closeConnection() throws IOException {
        if (objectSocket != null) {
            objectSocket.close();
        }
    }
}

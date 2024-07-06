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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Object receiveMessage() throws IOException, ClassNotFoundException {
        if (objectSocket != null) {
            return objectSocket.read();
        }
        return null;
    }

    public void closeConnection() throws IOException {
        if (objectSocket != null) {
            objectSocket.close();
        }
    }
}

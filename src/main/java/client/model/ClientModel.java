package client.model;

import java.io.*;
import java.net.*;

public class ClientModel {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void connectToServer(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public String receiveMessage() throws IOException {
        if (in != null) {
            return in.readLine();
        }
        return null;
    }

    public void closeConnection() throws IOException {
        if (socket != null) {
            socket.close();
        }
    }
}

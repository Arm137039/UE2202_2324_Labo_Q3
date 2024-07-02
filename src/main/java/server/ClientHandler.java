package server;

import java.io.*;
import java.net.*;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PictionaryServer server;
    private PrintWriter out;
    private BufferedReader in;
    private String role;

    public ClientHandler(Socket socket, PictionaryServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            assignRole();

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (inputLine.equalsIgnoreCase("quit")) {
                    break;
                }
                // Handle drawing and guessing logic here
                // ...
            }

            closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void assignRole() {
        List<ClientHandler> clients = server.getClients();
        if (clients.size() == 1) {
            role = "drawer";
            out.println("You are the drawer.");
            sendWordsToDrawer();
        } else {
            role = "guesser";
            out.println("You are the guesser.");
        }
    }

    private void sendWordsToDrawer() {
        out.println("Words: ");
        for (int i = 0; i < 3; i++) {
            out.println(server.getRandomWord());
        }
    }

    private void closeConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
            server.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

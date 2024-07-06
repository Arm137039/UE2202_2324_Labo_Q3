package server;

import java.io.*;
import java.net.*;
import java.util.List;
import common.network.ObjectSocket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    //private Socket clientSocket;
    private PictionaryServer server;
    private ObjectSocket objectSocket;
    private List<String> words;
    private String role;
    private ClientHandler teammate;

    public ClientHandler(ObjectSocket objectSocket, PictionaryServer server) {
        //this.clientSocket = socket;
        this.objectSocket = objectSocket;
        this.server = server;
        this.words = new ArrayList<>();
        this.role = "";
    }

    @Override
    public void run() {
        try {

            assignRole();

            if(this.role == "drawer") {
                waitForNextClient();
                teammate = server.getClients().get(getClientPlace() + 1);
                sendWordsToDrawer();
            }
            else{
                teammate = server.getClients().get(getClientPlace() - 1);
            }


            Object inputObject;
            while ((inputObject = objectSocket.read()) != null) {
                String inputLine = (String) inputObject; // Assuming the object read is a String
                teammate.objectSocket.write("Received: " + inputLine);
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
                this.role = "drawer";
                objectSocket.write("You are the drawer.");
                generateRandomWord();
            } else {
                this.role = "guesser";
                objectSocket.write("You are the guesser.");
            }
        } catch (IOException e) {
            System.err.println("Error assigning role: " + e.getMessage());
        }
    }

    private void generateRandomWord() {
        for(int i = 0; i < 3; i++) {
            this.words.add(server.getRandomWord());
        }
    }

    private void sendWordsToDrawer() {
        try {
            objectSocket.write("Words: ");
            for (String word : words) {
                objectSocket.write(word);
            }
        } catch (IOException e) {
            System.err.println("Error sending words to drawer: " + e.getMessage());
        }
    }
    private void waitForNextClient(){
        int currentPlace = getClientPlace();
        while(server.getClients().size() == currentPlace + 1){
            synchronized (server){
                try{
                    server.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //faire une methode qui renvoie la place du ClientHandler, de la list server.clients
    private int getClientPlace(){
        List<ClientHandler> clients = server.getClients();
        for(int i = 0; i < clients.size(); i++){
            if(clients.get(i) == this){
                return i;
            }
        }
        return -1;
    }

    private void closeConnection() {
        objectSocket.close();
        server.removeClient(this);
    }
}

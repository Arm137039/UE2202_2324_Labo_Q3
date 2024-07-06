package server;

import common.network.ObjectSocket;
import common.network.ServerConstants;

import java.io.*;
import java.net.*;
import java.util.*;

public class PictionaryServer {

    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private List<String> words;

    public PictionaryServer() {
        clients = new ArrayList<>();
        words = new ArrayList<>();
        loadWords();
    }

    public static void main(String[] args) {
        try {
            PictionaryServer server = new PictionaryServer();
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(ServerConstants.PORT);
        System.out.println("Server started on port " + ServerConstants.PORT);

        while (true) {
            System.out.println("waiting a new client");
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected");

            ObjectSocket objectSocket = new ObjectSocket(clientSocket);
            ClientHandler clientHandler = new ClientHandler(objectSocket, this);
            synchronized (this){
                clients.add(clientHandler);
                notifyClientHandlerAdded();
            }
            new Thread(clientHandler).start();
        }
    }
    public synchronized void notifyClientHandlerAdded() {
        notifyAll(); // Notify all waiting threads that a new ClientHandler has been added
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized List<ClientHandler> getClients() {
        return clients;
    }


    private void loadWords() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/mots.txt")))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized String getRandomWord() {
        Random rand = new Random();
        return words.get(rand.nextInt(words.size()));
    }
}
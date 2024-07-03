package server;


import java.io.*;
import java.net.*;
import java.util.*;

public class PictionaryServer {
    private static final int PORT = 3001;
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private List<String> words;

    public PictionaryServer() {
        clients = new ArrayList<>();
        loadWords();
    }

    public static void main(String[] args) {
        PictionaryServer server = new PictionaryServer();
        server.start();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                System.out.println("hola");
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                System.out.println("New client connected");
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized List<ClientHandler> getClients() {
        return clients;
    }

    private void loadWords() {
        words = new ArrayList<>();
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
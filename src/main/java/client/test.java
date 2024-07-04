package client;

import common.network.ObjectSocket;
import java.net.Socket;

public class test {
    public static void main(String[] args) {
        int numberOfClients = 1; // Adjust the number of clients as needed
        for (int i = 0; i < numberOfClients; i++) {
            new Thread(new Client()).start();
        }
    }

    private static class Client implements Runnable {
        @Override
        public void run() {
            String hostname = "localhost";
            int port = 3001;

            try (Socket socket = new Socket(hostname, port)) {
                ObjectSocket objectSocket = new ObjectSocket(socket);

                while (true){
                    // Example of receiving a string object
                    String response = objectSocket.read();
                    System.out.println(response);
                }

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
}
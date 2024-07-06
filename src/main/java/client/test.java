package client;

import common.network.ObjectSocket;
import java.net.Socket;

public class test {
    public static void main(String[] args) {
        test Test = new test();
        Test.startClient();
    }

    private void startClient() {
        String hostname = "localhost";
        int port = 3001;

        try {
            Socket socket = new Socket(hostname, port);
            ObjectSocket objectSocket = new ObjectSocket(socket);

            while (true) {
                // Example of receiving a string object
                String response = objectSocket.read();
                System.out.println(response);
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
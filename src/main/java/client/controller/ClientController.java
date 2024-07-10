package client.controller;

import client.model.ClientModel;
import client.view.ClientView;
import javafx.application.Platform;

import java.io.IOException; // Import IOException
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ClientController {
    private final ClientModel model;
    private final ClientView view;
    private final String host;
    private final int port;
    private List<String> words;
    private int numberGoodWord;
    private int index = 0;
    @FXML
    private Label endMessage;
    @FXML
    private Label scoreMessage;

    public ClientController(ClientModel model, ClientView view, String host, int port) {
        this.model = model;
        this.view = view;
        this.host = host;
        this.port = port;

        this.words = new ArrayList<String>();
        this.words.add("sol");
        this.words.add("toit");
        this.words.add("boite");

        this.view.setController(this);

        this.view.showEndGame();
        //tryConnectToServer();
        //new Thread(() -> { startReceivingMessages();}).start();
    }

    private void startReceivingMessages() {
        while(true){
            Object message = model.receiveMessage();
            if (message instanceof String) {
                // It's safe to cast to String here
                if(!interpretMessage((String) message)){
                    view.updateCanvas((String) message, index);
                }
            } else if(message instanceof Integer){
                // Handle Integer messages separately
                int messageAsInt = (Integer) message; // No need to cast here
                if((messageAsInt % 2) == 0){
                    index = messageAsInt / 2;
                } else {
                    Object wordObject = model.receiveMessage();
                    if(wordObject instanceof String){
                        String word = (String) wordObject;
                        boolean goodWord = verifyWord(messageAsInt, word);
                        if (goodWord){
                            System.out.println("alo");
                            numberGoodWord += 1;
                            view.updateScore(numberGoodWord);
                            model.sendMessage("bon mot");
                            model.sendMessage(messageAsInt);
                            System.out.println("alo1");
                        }
                    }
                }
            }
        }
    }
    private boolean verifyWord(int index, String word){
        index = (index - 1)/2;
        return this.words.get(index).equals(word);
    }
    /*private boolean interpretMessage0(String message){
        final boolean[] result = {true};
        Platform.runLater(() -> {
            try {
                switch (message){
                    case "wait":
                        this.view.showWaitView();
                        break;
                    case "drawer":
                        this.view.showDrawerView();
                        break;
                    case "guesser":
                        this.view.showGuesserView();
                        break;
                    case "bon mot":
                        System.out.println("alo2");
                        int receivedMessage = (int) model.receiveMessage(); // This line throws IOException
                        System.out.println("alo3");
                        view.updateTestField(receivedMessage);
                        break;
                    default:
                        result[0] = false;
                        break;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace(); // Handle the IOException here
                result[0] = false; // Optionally, update result based on the exception
            }
        });
        return result[0];
    }*/
    private boolean interpretMessage(String message){
        final boolean[] result = {true};
        switch (message){
            case "wait":
                Platform.runLater(() -> this.view.showWaitView());
                break;
            case "drawer":
                Platform.runLater(() -> this.view.showDrawerView());
                break;
            case "guesser":
                Platform.runLater(() -> this.view.showGuesserView());
                break;
            case "bon mot":
                System.out.println("alo2");
                Object receivedMessage = model.receiveMessage();
                System.out.println("alo3");
                if (receivedMessage instanceof Integer) {
                    final int receivedInt = (Integer) receivedMessage;
                    Platform.runLater(() -> {
                        System.out.println("toto");
                        view.updateTestField((receivedInt-1)/2);
                    });
                }
                break;
            default:
                result[0] = false;
                break;
        }
        return result[0];
    }
    public void tryConnectToServer() {
        try {
            model.connectToServer(this.host, this.port);
        } catch (IOException e) {
            // Handle connection error
        }
    }
    public void setScoreMessage(int points) {
        this.view.updateScore(points);
    }
    public void sendMessage(Object message) {
        model.sendMessage(message);
    }
}

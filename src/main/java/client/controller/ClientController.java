package client.controller;

import client.model.ClientModel;
import client.view.ClientView;
import client.view.ClientViewAbstract;
import client.view.GuesserView;
import client.view.DrawerView;

import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
    private final ClientModel model;
    private ClientViewAbstract view;
    private ClientView view2;
    private final String host;
    private final int port;
    private List<String> words;
    private int numberGoodWord;
    private int index = 0;
    private Stage primaryStage;

    public ClientController(ClientModel model, String host, int port, Stage primaryStage) {
        this.model = model;
        this.host = host;
        this.port = port;
        this.primaryStage = primaryStage;

        this.words = new ArrayList<>();
        this.words.add("sol");
        this.words.add("toit");
        this.words.add("boite");

        tryConnectToServer();
        new Thread(this::startReceivingMessages).start();
    }

    private void startReceivingMessages() {
        while (true) {
            Object message = model.receiveMessage();
            if (message instanceof String) {
                interpretStringMessage((String) message);
            } else if (message instanceof Integer) {
                handleIntegerMessage((Integer) message);
            }
        }
    }

    private void handleIntegerMessage(Integer messageAsInt) {
        if ((messageAsInt % 2) == 0) {
            index = messageAsInt / 2;
        } else {
            Object wordObject = model.receiveMessage();
            if (wordObject instanceof String) {
                String word = (String) wordObject;
                boolean goodWord = verifyWord(messageAsInt, word);
                if (goodWord) {
                    numberGoodWord += 1;
                    Platform.runLater(() -> view.updateScore(numberGoodWord));
                    model.sendMessage("bon mot");
                    model.sendMessage(messageAsInt);
                }
            }
        }
    }

    private boolean verifyWord(int index, String word) {
        index = (index - 1) / 2;
        return this.words.get(index).equals(word);
    }

    private void interpretStringMessage(String message) {
        switch (message) {
            case "wait":
                Platform.runLater(this::showWaitView);
                break;
            case "drawer":
                Platform.runLater(this::showDrawerView);
                break;
            case "guesser":
                Platform.runLater(this::showGuesserView);
                break;
            case "bon mot":
                Object receivedMessage = model.receiveMessage();
                if (receivedMessage instanceof Integer) {
                    int receivedInt = (Integer) receivedMessage;
                    Platform.runLater(() -> view.updateTestField((receivedInt - 1) / 2));
                }
                break;
            default:
                interpretCanvas(message);
                break;
        }
    }
    private void interpretCanvas(String message){
        if(view instanceof GuesserView){
            Platform.runLater(() -> view.updateCanvas(message, index));
        }
    }

    private void tryConnectToServer() {
        try {
            model.connectToServer(this.host, this.port);
        } catch (IOException e) {
            Platform.runLater(() -> showConnectionErrorView());
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    tryConnectToServer();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    private void showWaitView() {
        if (!(view2 instanceof ClientView)) {
            view2 = new ClientView();
            view2.setController(this);
            view2.setStage(primaryStage);
        }
        view2.showWaitView();
    }

    private void showDrawerView() {
        if (!(view instanceof DrawerView)) {
            view = new DrawerView();
            view.setController(this);
            view.setStage(primaryStage);
        }
        view.showGameView();
    }

    private void showGuesserView() {
        if (!(view instanceof GuesserView)) {
            view = new GuesserView();
            view.setController(this);
            view.setStage(primaryStage);
        }
        view.showGameView();
    }

    private void showConnectionErrorView() {
        if (!(view2 instanceof ClientView)) {
            view2 = new ClientView();
            view2.setController(this);
            view2.setStage(primaryStage);
        }
        view2.showWaitView();
    }

    public void setScoreMessage(int points) {
        Platform.runLater(() -> view.updateScore(points));
    }

    public void sendMessage(Object message) {
        model.sendMessage(message);
    }
}

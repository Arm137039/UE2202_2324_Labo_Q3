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

        tryConnectToServer();
        new Thread(() -> { startReceivingMessages();}).start();
    }

    private void startReceivingMessages() {
        while(true){
            Object message = model.receiveMessage();
            if (message instanceof String) {
                if(!interpretStringMessage((String) message)){
                    view.updateCanvas((String) message, index);
                }
            } else if(message instanceof Integer){
                int messageAsInt = (int) message;
                if((messageAsInt % 2) == 0){
                    index = messageAsInt / 2;
                } else {
                    Object wordObject = model.receiveMessage();
                    if(wordObject instanceof String){
                        String word = (String) wordObject;
                        boolean goodWord = verifyWord(messageAsInt, word);
                        if (goodWord){
                            numberGoodWord += 1;
                            view.updateScore(numberGoodWord);
                            model.sendMessage("bon mot");
                            model.sendMessage(messageAsInt);
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
    private boolean interpretStringMessage(String message){
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
                //drawer renvois le numéro du bon mot
                //TODO: voir si il n'y a pas des conflit avec l'envois du canvas par le drawer
                Object receivedMessage = model.receiveMessage();
                if (receivedMessage instanceof Integer) {
                    final int receivedInt = (Integer) receivedMessage;
                    Platform.runLater(() -> this.view.updateTestField((receivedInt-1)/2));
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
            //TODO: creer une vue qui dit que la connection a échoué
            //      et qui tente de se reconnecter au bout de 2 sec
        }
    }
    public void setScoreMessage(int points) {
        this.view.updateScore(points);
    }
    public void sendMessage(Object message) {
        model.sendMessage(message);
    }
}

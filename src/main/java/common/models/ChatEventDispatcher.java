package common.models;

public interface ChatEventDispatcher {
    void onMessageReceived(Message message);
    void onChangePseudo(ChangePseudo changePseudo);
}

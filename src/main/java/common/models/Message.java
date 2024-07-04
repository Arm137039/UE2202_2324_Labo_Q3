package common.models;

public class Message extends ChatEvent {

    private final String sender;
    private final String content;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    @Override
    public boolean isMessage() {
        return true;
    }

    @Override
    public void dispatchOn(ChatEventDispatcher dispatcher) {
        dispatcher.onMessageReceived(this);
    }
}

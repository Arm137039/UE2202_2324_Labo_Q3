package common.models;

import java.io.Serializable;

public abstract class ChatEvent implements Serializable {

    public boolean isMessage() {
        return false;
    }

    public boolean isChangePseudo() {
        return false;
    }

    public abstract void dispatchOn(ChatEventDispatcher dispatcher);
}

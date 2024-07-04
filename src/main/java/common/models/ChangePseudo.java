package common.models;

public class ChangePseudo extends ChatEvent {
    public final String newPseudo;
    public final String oldPseudo;

    public ChangePseudo(String oldPseudo, String newPseudo) {
        this.newPseudo = newPseudo;
        this.oldPseudo = oldPseudo;
    }

    @Override
    public boolean isChangePseudo() {
        return true;
    }

    @Override
    public void dispatchOn(ChatEventDispatcher dispatcher) {
        dispatcher.onChangePseudo(this);
    }

}

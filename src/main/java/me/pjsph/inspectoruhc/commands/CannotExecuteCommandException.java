package me.pjsph.inspectoruhc.commands;


public class CannotExecuteCommandException extends Exception {

    public enum Reason {

        NOT_ALLOWED,
        ONLY_AS_A_PLAYER,
        BAD_USE,
        NEED_DOC,
        UNKNOW

    }

    private Reason reason;
    private AbstractCommand origin;

    public CannotExecuteCommandException(Reason reason, AbstractCommand origin) {
        this.reason = reason;
        this.origin = origin;
    }

    public CannotExecuteCommandException(Reason reason) {
        this(reason, null);
    }

    public Reason getReason() {
        return reason;
    }

    public AbstractCommand getOrigin() {
        return origin;
    }

}

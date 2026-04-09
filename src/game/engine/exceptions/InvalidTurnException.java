package game.engine.exceptions;

public class InvalidTurnException extends GameActionException {
    private static final String MSG = "Action done on wrong turn";

    public InvalidTurnException() {
        super(MSG);
    }

    public InvalidTurnException(String message) {
        super(message);
    }
    public static String getMsg() {
        return this.MSG;
    }
}
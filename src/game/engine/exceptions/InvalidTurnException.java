package game.engine.exceptions;

@SuppressWarnings("serial")
public class InvalidTurnException extends GameActionException{
	private static final String MSG = "Action done on wrong turn";

	public InvalidTurnException() {
		super(MSG);
	}

<<<<<<< HEAD
    public InvalidTurnException(String message) {
        super(message);
    }
    public static String getMsg() {
        return this.MSG;
    }
}
=======
	public InvalidTurnException(String message){
		super(message);
	}

}
>>>>>>> 896bd770a997025971b37608592eeabc44f3c9ec

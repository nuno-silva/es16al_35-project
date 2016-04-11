package pt.tecnico.mydrive.exception;

public class IsNotCdAbleException extends MydriveException {
    private String _msg;
    public IsNotCdAbleException() {
        _msg = "File isn't cdAble";
    }

    public IsNotCdAbleException( String msg ) {
        _msg = msg;
    }

    @Override
    public String getMessage() {
        return _msg;
    }
}

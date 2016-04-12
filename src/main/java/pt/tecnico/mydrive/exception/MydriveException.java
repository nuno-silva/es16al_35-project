package pt.tecnico.mydrive.exception;

public abstract class MydriveException extends RuntimeException {
    public MydriveException() {
    }

    public MydriveException(String mesg) {
        super(mesg);
    }
}

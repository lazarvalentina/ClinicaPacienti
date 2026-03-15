package main.java.Exceptions;

public class RepositoryException extends Exception {
    public RepositoryException(Throwable cause) {
        super(cause);
    }

    public RepositoryException(String message) {
        super(message);

    }
}

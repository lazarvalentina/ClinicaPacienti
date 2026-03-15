package main.java.Exceptions;

public class DuplicateIDException extends RepositoryException {
    public DuplicateIDException(String msg) {
        super(msg);
    }
}

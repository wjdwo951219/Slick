package toy.slick.exception;

import jakarta.servlet.ServletException;

public class Bucket4jException extends ServletException {
    public Bucket4jException(String message) {
        super(message);
    }
}

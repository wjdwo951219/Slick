package toy.slick.exception;

import jakarta.servlet.ServletException;

public class ApiKeyException extends ServletException {
    public ApiKeyException(String msg) {
        super(msg);
    }
}

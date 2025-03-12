package exception;

import javax.servlet.ServletException;
public class InternalServerErrorException extends ServletException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
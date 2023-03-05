package hexlet.code.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends RuntimeException {

    private final HttpStatus status;

    public InvalidRequestException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public static InvalidRequestException invalidRequest(String message) {
        return new InvalidRequestException(message, HttpStatus.BAD_REQUEST);
    }
}

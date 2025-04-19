package pe.com.lacunza.technix.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class IllegalStateAlertException extends RuntimeException {
    public IllegalStateAlertException(String message) {
        super(message);
    }
}

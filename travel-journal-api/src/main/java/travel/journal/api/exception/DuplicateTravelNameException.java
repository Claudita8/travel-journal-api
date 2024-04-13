package travel.journal.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateTravelNameException extends RuntimeException {
    public DuplicateTravelNameException(String message){
        super(message);
    }
}

package tp.farming_springboot.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;

public class RestNullPointerException extends NullPointerException {

    @Getter
    private HttpHeaders headers;

    @Getter
    private HttpStatus httpStatus;

    private String message;
    private Object obj;
    private StatusEnum statusEnum;

    @Getter
    private Message msg;


    public RestNullPointerException(HttpHeaders headers, String message, HttpStatus httpStatus, Object obj, StatusEnum statusEnum) {
        this.headers = headers;
        this.message = message;
        this.httpStatus = httpStatus;
        this.obj = obj;
        this.statusEnum = statusEnum;
        this.msg = new Message(statusEnum, message, obj);
    }

    public RestNullPointerException(HttpHeaders headers, String message, HttpStatus httpStatus, StatusEnum statusEnum) {
        this.headers = headers;
        this.message = message;
        this.httpStatus = httpStatus;
        this.obj = null;
        this.statusEnum = statusEnum;
        this.msg = new Message(statusEnum, message, this.obj);
    }
}

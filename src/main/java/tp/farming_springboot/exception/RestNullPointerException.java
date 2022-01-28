package tp.farming_springboot.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;

public class RestNullPointerException extends NullPointerException {

    public RestNullPointerException(String msg){
        super(msg);
    }

}

package tp.farming_springboot.exception;

import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;



public class UserNotAuthorizedException extends Exception{
    public UserNotAuthorizedException(String msg){
        super(msg);
    }

}

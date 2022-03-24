package tp.farming_springboot.domain.exception;

public class UserExistsException extends Exception{
    public UserExistsException(String msg){
        super(msg);
    }
}


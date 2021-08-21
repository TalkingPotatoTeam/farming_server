package tp.farming_springboot.exception;

public class UserExistsException extends Exception{
    public UserExistsException(String msg){
        super(msg);
    }
}


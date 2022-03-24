package tp.farming_springboot.domain.exception;


public class UserNotAuthorizedException extends Exception{
    public UserNotAuthorizedException(String msg){
        super(msg);
    }

}

package tp.farming_springboot.domain.exception;

public class UserAlreadyLikeProductException extends Exception{

    public UserAlreadyLikeProductException(String msg) {
        super(msg);
    }
}

package tp.farming_springboot.exception;

public class UserAlreadyLikeProductException extends Exception{

    public UserAlreadyLikeProductException(String msg) {
        super(msg);
    }
}

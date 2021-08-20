package tp.farming_springboot.exception;

public class UserNotLikeProductException extends Exception{
    public UserNotLikeProductException(String msg) {
        super(msg);
    }
}

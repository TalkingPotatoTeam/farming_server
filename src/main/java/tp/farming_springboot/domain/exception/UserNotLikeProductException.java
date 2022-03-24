package tp.farming_springboot.domain.exception;

public class UserNotLikeProductException extends Exception{
    public UserNotLikeProductException(String msg) {
        super(msg);
    }
}

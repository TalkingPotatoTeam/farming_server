package tp.farming_springboot.domain.exception;

public class RestNullPointerException extends NullPointerException {

    public RestNullPointerException(String msg){
        super(msg);
    }

}

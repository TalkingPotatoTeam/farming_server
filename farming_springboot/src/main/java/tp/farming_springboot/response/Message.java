package tp.farming_springboot.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Data
public class Message {

    @Getter
    @Setter
    private HttpStatus status;


    @Setter
    private String message;

    @Setter
    private Object data;

    public Message() {
        this.status = HttpStatus.BAD_REQUEST;
        this.data = null;
        this.message = null;
    }
}

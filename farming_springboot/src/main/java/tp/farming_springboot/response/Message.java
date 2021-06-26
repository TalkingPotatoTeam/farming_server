package tp.farming_springboot.response;

import java.time.ZonedDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class Message {

    private ZonedDateTime timestamp;

    @Setter
    private int statusCode;

    @Getter
    @Setter
    private StatusEnum status;
    @Setter
    private String message;

    @Setter
    private Object data;


    public Message() {
        this.statusCode = StatusEnum.BAD_REQUEST.statusCode;
        this.status = StatusEnum.BAD_REQUEST;
        this.data = null;
        this.message = null;
        this.timestamp = ZonedDateTime.now();
    }

    public Message(StatusEnum status, String message, Object data) {
        this.status = status;
        this.statusCode = status.statusCode;
        this.message = message;
        this.data = data;
        this.timestamp = ZonedDateTime.now();
    }

    public Message(StatusEnum status, String message) {
        this.status = status;
        this.statusCode = status.statusCode;
        this.message = message;
        this.data = null;
        this.timestamp = ZonedDateTime.now();
    }
}

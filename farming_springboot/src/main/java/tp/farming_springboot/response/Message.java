package tp.farming_springboot.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Message {

    @Getter
    @Setter
    private StatusEnum status;
    @Setter
    private String message;

    @Setter
    private Object data;

    public Message() {
        this.status = StatusEnum.BAD_REQUEST;
        this.data = null;
        this.message = null;
    }

    public Message(StatusEnum status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public Message(StatusEnum status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }
}

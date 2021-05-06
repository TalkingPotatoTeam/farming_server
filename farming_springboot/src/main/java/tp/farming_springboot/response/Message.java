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
}

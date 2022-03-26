package tp.farming_springboot.api;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ApiResponse<T> {

    private LocalDateTime timestamp;
    private int statusCode;

    @JsonIgnore
    private ResultCode status;
    private String message;
    private T data;


    public ApiResponse(ResultCode status, String message, T data) {
        this.status = status;
        this.statusCode = status.getCode();
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponse(ResultCode status, String message) {
        this.status = status;
        this.statusCode = status.getCode();
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(
                ResultCode.OK,
                ResultCode.OK.getMessage()
        );
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                ResultCode.OK,
                ResultCode.OK.getMessage(),
                data
        );
    }

    public static <T> ApiResponse<T> failure(ResultCode resultCode, String message) {
        return new ApiResponse<>(
                resultCode,
                message
        );
    }
 }

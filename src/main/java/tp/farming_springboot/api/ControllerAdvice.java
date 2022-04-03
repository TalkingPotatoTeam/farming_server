package tp.farming_springboot.api;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import tp.farming_springboot.domain.exception.*;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler({
            RestNullPointerException.class,
            IllegalArgumentException.class,
            UserNotLikeProductException.class,
            UserAlreadyLikeProductException.class,
            HttpRequestMethodNotSupportedException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            AddressRemoveException.class,
            UserExistsException.class,
            VerificationException.class,
            MethodArgumentNotValidException.class,
            NullPointerException.class
    })
    public ApiResponse<?> handle(Exception e) {
        return ApiResponse.failure(ResultCode.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UserNotAuthorizedException.class)
    public ApiResponse<?> handle(UserNotAuthorizedException e) {
        return ApiResponse.failure(ResultCode.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(PhotoFileException.class)
    public ApiResponse<?> handle(PhotoFileException e) {
        return ApiResponse.failure(ResultCode.INTERNAL_SERVER_ERROR, e.getMessage());
    }

}

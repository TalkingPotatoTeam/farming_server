package tp.farming_springboot.controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.exception.*;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;

import java.nio.charset.Charset;


@RestControllerAdvice
public class ControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RestNullPointerException.class)
    public ResponseEntity<Message> handler(RestNullPointerException e) {
        Message message = new Message(StatusEnum.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UserNotAutorizedException.class)
    public ResponseEntity<Message> handle(UserNotAutorizedException e) {
        Message message = new Message(StatusEnum.UNAUTHORIZED, e.getMessage());
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(PhotoFileException.class)
    public ResponseEntity<Message> handle(PhotoFileException e) {
        Message message = new Message(StatusEnum.INTERNAL_SERVER_ERROR, e.getMessage());
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Message> handle(HttpRequestMethodNotSupportedException e) {
        Message message = new Message(StatusEnum.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Message> handle(MissingServletRequestParameterException e) {
        Message message = new Message(StatusEnum.PARAMETER_LACKED, e.getMessage());
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Message> handle(MissingServletRequestPartException e) {
        Message message = new Message(StatusEnum.PARAMETER_LACKED, e.getMessage());
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AddressRemoveException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Message> handle(AddressRemoveException e) {
        Message message = new Message(StatusEnum.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Message> handle(UserExistsException e) {
        Message message = new Message(StatusEnum.BAD_REQUEST, e.getMessage());
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.BAD_REQUEST);
    }

    public HttpHeaders HttpHeaderSetting(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return headers;
    }


}

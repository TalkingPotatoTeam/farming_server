package tp.farming_springboot.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.UserRepository;
import tp.farming_springboot.exception.RestNullPointerException;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;

import java.nio.charset.Charset;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Message> handler(RestNullPointerException e) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Message message = new Message();
        message.setMessage(e.getMessage());
        message.setStatus(StatusEnum.BAD_REQUEST);

        return new ResponseEntity<>(message, headers, HttpStatus.BAD_REQUEST);
    }



    public User findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RestNullPointerException("Can't find User {id:" + id + "}"));
        return user;
    }

    public User findUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone).orElseThrow(() -> new RestNullPointerException("Can't find User {phone:" + phone + "}"));
        return user;
    }




}

package tp.farming_springboot.controller;

import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.domain.user.dto.TokenDto;
import tp.farming_springboot.domain.user.dto.UserAuthenDto;
import tp.farming_springboot.domain.user.dto.UserCreateDto;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.UserRepository;
import tp.farming_springboot.domain.user.service.AuthenticateService;
import tp.farming_springboot.domain.user.service.OtpService;
import tp.farming_springboot.domain.user.service.SmsService;
import tp.farming_springboot.domain.user.service.UserService;
import tp.farming_springboot.exception.UserExistsException;
import tp.farming_springboot.exception.VerificationException;
import tp.farming_springboot.response.StatusEnum;
import tp.farming_springboot.response.Message;
import java.nio.charset.Charset;
import java.util.Optional;

@CrossOrigin
@RestController
@EnableAutoConfiguration
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthenticateController {
    private final OtpService otpService;
    private final SmsService smsService;
    private final AuthenticateService authenticateService;
    private final UserService userService;
    private final UserRepository userRepository;

    public HttpHeaders HttpHeaderSetting(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return headers;
    }

    //renew tokens for free
    @GetMapping("/tokens")
    public ResponseEntity<?> temp(@RequestBody UserAuthenDto logger){
        TokenDto tokenDto = authenticateService.getNewTokens(logger.getPhone());
        Message message = new Message(StatusEnum.OK,"Generating token success.", tokenDto);
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    //renew tokens
    @GetMapping("/gen-tokens")
    public ResponseEntity<?> sendTokens(Authentication authentication){
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        TokenDto tokenDto = authenticateService.getNewTokens(user.get().getPhone());
        Message message = new Message(StatusEnum.OK,"Generating token success.", tokenDto);
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    //send otp number to user
    @PostMapping("/request-otp")
    @ResponseStatus(HttpStatus.OK)
    private ResponseEntity<Message> sendMsg(@RequestBody UserAuthenDto user){
        try {
            String phone = user.getPhone();
            int otp = otpService.generateOTP(phone);
            String result = smsService.sendSMS(String.valueOf(otp), phone);
            Message message = new Message(StatusEnum.OK,"User Created", result );
            return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
        }
        catch(CoolsmsException e){
            Message message = new tp.farming_springboot.response.Message(StatusEnum.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.BAD_REQUEST);
        }
    }

    //verify number and redirect to signup or login
    @PostMapping("/validate")
    public ResponseEntity<Message> validateOtp(@RequestBody UserCreateDto newUser) throws VerificationException, UserExistsException {
        String phone = newUser.getPhone();

        //validate
        authenticateService.verifyOtp(newUser.getOtp(), otpService.getOtp(phone));
        String result = "Authentication was Successful.";

        //if user does not exist create new user
        if(!userService.checkUserExists(phone)) {
            userService.create(phone);
        }

        //return tokens
        TokenDto tokenDto = authenticateService.getNewTokens(phone);
        Message message = new Message(StatusEnum.OK, result, tokenDto);
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

}

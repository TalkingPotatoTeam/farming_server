package tp.farming_springboot.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.config.JwtUtils;
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
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.*;
@CrossOrigin
@RestController
@EnableAutoConfiguration
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthenticateController {

    private final OtpService otpService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final SmsService smsService;
    private final AuthenticateService authenticateService;
    private final UserService userService;

    public HttpHeaders HttpHeaderSetting(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return headers;
    }

    @GetMapping("/tokens") //사용자 번호만 받고 access 토큰 + refresh 토큰 발급
    public ResponseEntity<?> authenticate(@RequestBody UserCreateDto logger){
        tp.farming_springboot.response.Message message = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        try{
            if (userRepository.existsByPhone(logger.getPhone())) {
                Optional<User> user = userRepository.findByPhone(logger.getPhone());
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.get().getPhone(), user.get().getPhone()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String access = jwtUtils.generateJwtToken(authentication);
                String refresh = jwtUtils.generateJwtRefreshToken(authentication);

                List<JSONObject> entities = new ArrayList<JSONObject>();
                JSONObject entity = new JSONObject();
                entity.put("access", access);
                entity.put("refresh", refresh);
                entities.add(entity);
                message = new tp.farming_springboot.response.Message(StatusEnum.OK, "Update access and refresh token.",entities);
                return new ResponseEntity<>(message, headers, HttpStatus.OK);
            }
            else{
                message = new tp.farming_springboot.response.Message(StatusEnum.BAD_REQUEST, "Phone number does not exist in db.");
                return new ResponseEntity<>(message, headers, HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e){
            System.out.println(e);
            message = new tp.farming_springboot.response.Message(StatusEnum.BAD_REQUEST, "알수없는 오류.");
            return new ResponseEntity<>(message, headers, HttpStatus.BAD_REQUEST);
        }
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
        int userOtp = newUser.getOtp();
        int serverOtp = otpService.getOtp(phone);

        //validate
        authenticateService.verifyOtp(userOtp, serverOtp);
        String result = "Authentication was Successful.";
        //if user does not exist create new user
        if(!userService.checkUserExists(phone)) {
            userService.create(phone);
        }

        //return tokens
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        entity.put("access", authenticateService.accessToken(phone));
        entity.put("refresh", authenticateService.refreshToken(phone));
        entities.add(entity);
        Message message = new Message(StatusEnum.OK, result, entities);
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    //만료된 토큰 + 리프레시 토큰 받고 새로운 access 토큰 발급해줌
    @GetMapping(value = "/acces-token")
    public ResponseEntity<?> newAccessToken(HttpServletRequest request) {
        // From the HttpRequest get the claims
        Claims claims = (Claims) request.getAttribute("claims");
        System.out.println(claims);
        Map<String, Object> expectedMap = getMapFromIoJsonwebtokenClaims(claims);
        String token = jwtUtils.doGenerateAccessToken(expectedMap, expectedMap.get("sub").toString());
        return ResponseEntity.ok(token);
    }

    private Map<String, Object> getMapFromIoJsonwebtokenClaims( Claims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

}

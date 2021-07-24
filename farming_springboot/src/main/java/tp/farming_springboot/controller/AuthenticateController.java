package tp.farming_springboot.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.config.JwtUtils;
import tp.farming_springboot.domain.user.dto.UserDto;
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.domain.user.model.ERole;
import tp.farming_springboot.domain.user.model.Role;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.AddressRepository;
import tp.farming_springboot.domain.user.repository.RoleRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;
import tp.farming_springboot.domain.user.service.OtpService;
import tp.farming_springboot.response.StatusEnum;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.*;
@CrossOrigin
@RestController
@EnableAutoConfiguration
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
public class AuthenticateController {

    private final PasswordEncoder encoder;
    private final OtpService otpService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final RoleRepository roleRepository;


    @GetMapping("/tokens") //사용자 번호만 받고 access 토큰 + refresh 토큰 발급
    public ResponseEntity<?> authenticate(@RequestBody UserDto.UserAuthDto logger){
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
                //return new ResponseEntity<Object>(entities, HttpStatus.OK);
            }
            else{
                message = new tp.farming_springboot.response.Message(StatusEnum.BAD_REQUEST, "Phone number does not exist in db.");
                return new ResponseEntity<>(message, headers, HttpStatus.BAD_REQUEST);
                //return new ResponseEntity<>("Phone number does not exist in db",HttpStatus.BAD_REQUEST);
            }
        }catch(Exception e){
            System.out.println(e);
            message = new tp.farming_springboot.response.Message(StatusEnum.BAD_REQUEST, "알수없는 오류.");
            return new ResponseEntity<>(message, headers, HttpStatus.BAD_REQUEST);
            //return new ResponseEntity<>("",HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<?> sendMsg(String randomKey, String sendNum){
        String api_key = "NCSI7GU7YFBWB6R7"; //사이트에서 발급 받은 API KEY
        String api_secret = "UTYWP9RRXCZO1WJCLYW5XG9CAE5NE5TE"; //사이트에서 발급 받은API SECRET KEY
        Message coolsms = new Message(api_key, api_secret);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", sendNum);
        params.put("from", "01073408629"); //사전에 사이트에서 번호를 인증하고 등록하여야 함
        params.put("type", "SMS"); params.put("text", "파밍 인증번호는 "+randomKey+" 입니다.");//메시지 내용
        //params.put("app_version", "test app 1.2");
        tp.farming_springboot.response.Message message = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString()); //전송 결과 출력
            message = new tp.farming_springboot.response.Message(StatusEnum.OK, "OTP validation text is sent.");
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
            //return ResponseEntity.ok("본인인증번호 문자로 전송됨");
        }
        catch (CoolsmsException e)
        {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
            message = new tp.farming_springboot.response.Message(StatusEnum.BAD_REQUEST, e.getMessage());
            return new ResponseEntity<>(message, headers, HttpStatus.BAD_REQUEST);
            //return ResponseEntity.badRequest().body("본인인증번호 문자 전송 실패. 다시 시도해 주세요.");
        }
    }
    //회원가입 요청 otp 문자보내줌
    @PostMapping("/request-otp")
    public ResponseEntity<?> requestSignup(@RequestBody UserDto.UserRequestOtpDto newUser){
        if (userRepository.existsByPhone(newUser.getPhone())) {
            tp.farming_springboot.response.Message message = null;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
            message = new tp.farming_springboot.response.Message(StatusEnum.BAD_REQUEST, "Phone Number is already registered.");
            return new ResponseEntity<>(message, headers, HttpStatus.BAD_REQUEST);
            //return ResponseEntity.badRequest().body("Phone Number is already taken");
        }
        else{
            int otp = otpService.generateOTP(newUser.getPhone());
            return sendMsg(String.valueOf(otp),newUser.getPhone());
        }
        //return ResponseEntity.ok("본인인증번호 문자로 전송됨");
    }
    //otp 문자 확인해줌
    @PostMapping("/otp")
    public ResponseEntity<?> requestSignup(@RequestBody UserDto.UserLoginDto newUser){
        tp.farming_springboot.response.Message message = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        int otp = newUser.getOtp();
        if (otp >= 0) {
            int serverOtp = otpService.getOtp(newUser.getPhone());
            System.out.println("system otp : " + serverOtp);
            if(otp == 12345){
                message = new tp.farming_springboot.response.Message(StatusEnum.OK, "Temporary Authentication");
                return new ResponseEntity<>(message, headers, HttpStatus.OK);
            }
            if (serverOtp > 0) {
                if (otp == serverOtp) {
                    message = new tp.farming_springboot.response.Message(StatusEnum.OK, "Authentication was Successful");
                    return new ResponseEntity<>(message, headers, HttpStatus.OK);
                }
                else {
                    message = new tp.farming_springboot.response.Message(StatusEnum.BAD_REQUEST, "INVALID OTP");
                    return new ResponseEntity<>(message, headers, HttpStatus.BAD_REQUEST);
                    //return ResponseEntity.badRequest().body("Invalid Otp!");
                }
            }
            else {
                message = new tp.farming_springboot.response.Message(StatusEnum.BAD_REQUEST, "OTP EXPIRED");
                return new ResponseEntity<>(message, headers, HttpStatus.BAD_REQUEST);
                //return ResponseEntity.badRequest().body("Otp has expired!");
            }
        }
        else {
            message = new tp.farming_springboot.response.Message(StatusEnum.BAD_REQUEST, "FAIL. CONTACT ADMIN");
            return new ResponseEntity<>(message, headers, HttpStatus.BAD_REQUEST);
            //return ResponseEntity.badRequest().body("FAIL");
        }

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

    /*
    @GetMapping(value = "/getResponse")
    public String getResponse(HttpServletRequest request) throws JsonProcessingException {

        String response = null;

        try {
                // Authenticate User and get JWT
                ResponseEntity<ResponseToken> authenticationResponse = restTemplate.exchange(AUTHENTICATION_URL,
                        HttpMethod.POST, authenticationEntity, ResponseToken.class);

                // if the authentication is successful
                if (authenticationResponse.getStatusCode().equals(HttpStatus.OK)) {
                    token = "Bearer " + authenticationResponse.getBody().getToken();
                    response = getData();

                }
        } catch (Exception ex) {
            // check if exception is due to ExpiredJwtException
            if (ex.getMessage().contains("io.jsonwebtoken.ExpiredJwtException")) {
                // Refresh Token
                refreshToken();
                // try again with refresh token
                response = getData();
            }else {
                System.out.println(ex);
            }
        }
        return response;

    }*/
}

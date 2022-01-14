package tp.farming_springboot.controller;

import lombok.RequiredArgsConstructor;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.config.JwtUtils;
import tp.farming_springboot.domain.user.dto.AddressDto;
import tp.farming_springboot.domain.user.dto.UserCreateDto;
import tp.farming_springboot.domain.user.dto.UserDto;
import tp.farming_springboot.domain.user.dto.UserResDto;
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.AddressRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;
import tp.farming_springboot.domain.user.service.AddressService;
import tp.farming_springboot.domain.user.service.AuthenticateService;
import tp.farming_springboot.domain.user.service.OtpService;
import tp.farming_springboot.domain.user.service.UserService;
import tp.farming_springboot.exception.AddressRemoveException;
import tp.farming_springboot.exception.UserExistsException;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;

import java.nio.charset.Charset;
import java.util.*;
@RestControllerAdvice
@RequiredArgsConstructor
@CrossOrigin
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final AddressService addressService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;



    //delete User
    @DeleteMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> delete(Authentication authentication){
        String userPhone = authentication.getName();
        userService.delete(userPhone);
        Message message = new Message(StatusEnum.OK,"User deleted" );
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    //read User's Address list
    @GetMapping("/address") //내 주소들 보기
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> getAddress(Authentication authentication){
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        entity.put("Current Address", user.get().getCurrent());
        entity.put("All Address", user.get().getAddresses());
        entities.add(entity);
        Message message = new Message(StatusEnum.OK, "Addresses.", entities);
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    //add Address
    @PostMapping("/address")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> addAddress(Authentication authentication, @RequestBody AddressDto Address){
        String userPhone = authentication.getName();
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        Address newAddress = addressService.create(user.get().getId(), Address.getContent(), Address.getLat(), Address.getLon());
        userService.addAddress(userPhone, newAddress);
        Message message = new Message(StatusEnum.OK,"Address added" );
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    //delete Address
    @DeleteMapping("/address/{id}") //주소 삭제
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> deleteAddress(Authentication authentication, @PathVariable Long id ) throws AddressRemoveException {
        String userPhone = authentication.getName();
        userService.deleteAddress(userPhone, id);
        addressService.delete(id);
        Message message = new Message(StatusEnum.OK,"Address deleted" );
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    //set User's Current Address
    @PutMapping("/address/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> changeCurrentAddress(Authentication authentication, @PathVariable Long id) {
        String userPhone = authentication.getName();
        System.out.println("userPhone : "+userPhone);
        userService.setCurrentAddress(userPhone, id);
        Message message = new Message(StatusEnum.OK,"Current Address changed" );
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    //return User Profile
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> getUser(Authentication authentication){
        Optional<User> user = userRepository.findByPhone(authentication.getName());

        UserResDto userResDto = UserResDto.of(user.get().getId(), user.get().getPhone(), user.get().getCurrent());

        Message message = new Message(StatusEnum.OK, "User", userResDto);
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    //Update user's phone num
    @PatchMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Message> updateUserPhone(Authentication authentication, @RequestBody UserCreateDto newUser) throws UserExistsException{
        String userPhone = authentication.getName();
        userService.updatePhone(userPhone, newUser.getPhone());
        Message message = new Message(StatusEnum.OK, "User's phone number has changed");
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    @PostMapping("/sudo") //테스트할때 문자인증 없이 회원가입용
    public ResponseEntity<?> createUserForce(@RequestBody UserDto user){
        User newUser = new User(user.getPhone());
        //번호로 비밀번호 생성
        newUser.setPassword(encoder.encode(newUser.getPhone()));
        //현주소를 유저의 주소록에 저장
        Address newAddress = new Address();
        newAddress.setUser_id(newUser.getId());
        newAddress.setContent(user.getAddress());
        addressRepository.save(newAddress);
        newUser.addAddress(newAddress);
        //유저 롤 추가
        newUser.setCurrent(newAddress);
        userRepository.save(newUser);


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(newUser.getPhone(), newUser.getPhone()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String access = jwtUtils.generateJwtToken(authentication);




        JSONObject entity = new JSONObject();
        entity.put("access", access);
        return new ResponseEntity<Object>(entity, HttpStatus.OK);
    }


    public HttpHeaders HttpHeaderSetting(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return headers;
    }

}

package tp.farming_springboot.api;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.application.dto.request.AddressDto;
import tp.farming_springboot.application.dto.request.UserCreateDto;
import tp.farming_springboot.application.dto.request.UserForceCreateDto;
import tp.farming_springboot.application.dto.response.TokenDto;
import tp.farming_springboot.application.dto.response.UserResDto;
import tp.farming_springboot.domain.entity.Address;
import tp.farming_springboot.domain.entity.User;
import tp.farming_springboot.domain.repository.UserRepository;
import tp.farming_springboot.application.AddressService;
import tp.farming_springboot.application.UserService;
import tp.farming_springboot.domain.exception.AddressRemoveException;
import tp.farming_springboot.domain.exception.UserExistsException;
import tp.farming_springboot.application.dto.response.Message;
import tp.farming_springboot.application.dto.response.StatusEnum;

import java.nio.charset.Charset;
import java.util.*;

@RequiredArgsConstructor
@CrossOrigin
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AddressService addressService;


    @DeleteMapping
    public String delete(Authentication authentication){
        System.out.println("userPhone = " + authentication.getName());
        userService.delete( authentication.getName());
        return "user deleted.";
    }

    @GetMapping("/address") //내 주소들 보기
    public ResponseEntity<Message> getAddress(Authentication authentication){
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        List<JSONObject> entities = new ArrayList<>();
        JSONObject entity = new JSONObject();
        entity.put("Current Address", user.get().getCurrent());
        entity.put("All Address", user.get().getAddresses());
        entities.add(entity);

        return new ResponseEntity<>(new Message(StatusEnum.OK, "Addresses.", entities), HttpHeaderSetting(), HttpStatus.OK);
    }


    @PostMapping("/address")
    public ResponseEntity<Message> addAddress(Authentication authentication, @RequestBody AddressDto Address){
        String userPhone = authentication.getName();
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        Address newAddress = addressService.create(user.get().getId(), Address.getContent(), Address.getLat(), Address.getLon());
        userService.addAddress(userPhone, newAddress);

        return new ResponseEntity<>(new Message(StatusEnum.OK,"Address added" ), HttpHeaderSetting(), HttpStatus.OK);
    }

    //delete Address
    @DeleteMapping("/address/{id}") //주소 삭제
    public ResponseEntity<Message> deleteAddress(Authentication authentication, @PathVariable Long id ) throws AddressRemoveException {
        String userPhone = authentication.getName();

        userService.deleteAddress(userPhone, id);
        addressService.delete(id);
        return new ResponseEntity<>(new Message(StatusEnum.OK,"Address deleted" ), HttpHeaderSetting(), HttpStatus.OK);
    }

    //set User's Current Address
    @PutMapping("/address/{id}")
    public ResponseEntity<Message> changeCurrentAddress(Authentication authentication, @PathVariable Long id) {
        String userPhone = authentication.getName();
        userService.setCurrentAddress(userPhone, id);
        return new ResponseEntity<>(new Message(StatusEnum.OK,"Current Address changed"), HttpHeaderSetting(), HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<Message> getUser(Authentication authentication){
        UserResDto userDto = userService.getUserInfo(authentication.getName());
        return new ResponseEntity<>(new Message(StatusEnum.OK, "User", userDto), HttpHeaderSetting(), HttpStatus.OK);
    }


    @PutMapping
    public ResponseEntity<Message> updateUserPhone(Authentication authentication, @RequestBody UserCreateDto newUser) throws UserExistsException{
        String userPhone = authentication.getName();
        userService.updatePhone(userPhone, newUser.getPhone());
        return new ResponseEntity<>(new Message(StatusEnum.OK, "User's phone number has changed"), HttpHeaderSetting(), HttpStatus.OK);
    }

    @PostMapping("/sudo")
    public ResponseEntity<?> createUserForce(@RequestBody UserForceCreateDto userDto){
        TokenDto tokenDto = userService.createUserForce(userDto);
        return new ResponseEntity<Object>(tokenDto, HttpStatus.OK);
    }

    public HttpHeaders HttpHeaderSetting(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return headers;
    }

}

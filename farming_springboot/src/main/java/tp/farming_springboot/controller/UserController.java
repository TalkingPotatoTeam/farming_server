package tp.farming_springboot.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
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
import java.util.*;
@CrossOrigin
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    OtpService otpService;

    @GetMapping("/init")
    public void init() {
        Role roleUser = new Role();
        Role roleAdmin = new Role();
        roleUser.setName(ERole.ROLE_USER);
        roleRepository.save(roleUser);
        roleAdmin.setName(ERole.ROLE_ADMIN);
        roleRepository.save(roleAdmin);
    }

    @PutMapping("/address/{id}")//대표주소 변경
    public ResponseEntity<?> changeCurrentAddress(Authentication authentication, @PathVariable Long id) {
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        Optional<Address> toChange = addressRepository.findById(id);
        user.get().setCurrent(toChange.get());
        userRepository.save(user.get());
        return ResponseEntity.ok("대표 주소 변경 성공");
    }

    @GetMapping("/address") //내 주소들 보기
    public ResponseEntity<?> getAddress(Authentication authentication){
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        entity.put("Current Address", user.get().getCurrent());
        entity.put("All Address", user.get().getAddresses());
        entities.add(entity);
        return new ResponseEntity<Object>(entities, HttpStatus.OK);
    }

    @DeleteMapping("/address/{id}") //주소 삭제
    public ResponseEntity<?>  deleteAddress(Authentication authentication, @PathVariable Long id ){
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        Optional<Address> toDelete = addressRepository.findById(id);
        if (user.get().getCurrent().getId() == id){
            return ResponseEntity.badRequest().body("대표 주소는 삭제 불가능");
        }
        user.get().deleteAddress(toDelete.get());
        addressRepository.deleteById(id);
        return ResponseEntity.ok("주소 삭제됨!");
    }

    @PostMapping("/address") //주소 추가
    public ResponseEntity<?>  addAddress(Authentication authentication, @RequestBody UserDto.UserNewAddressDto User ){
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        Address newAddress = new Address();
        newAddress.setUser_id(user.get().getId());
        newAddress.setContent(User.getAddress());
        addressRepository.save(newAddress);
        user.get().addAddress(newAddress);
        userRepository.save(user.get());
        return ResponseEntity.ok("주소 추가됨!");
    }

    @PatchMapping("") //데이터 수정
    public User updateUser(@PathVariable String id, @RequestBody User newUser ){
        Long userID = Long.parseLong(id);
        Optional<User> user = userRepository.findById(userID);
        user.get().setId(newUser.getId());
        user.get().setPhone(newUser.getPhone());
        userRepository.save(user.get());
        return user.get();
    }


    @DeleteMapping("") //탈퇴
    public ResponseEntity<?> deleteUser(Authentication authentication){
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        userRepository.deleteById(user.get().getId());
        return ResponseEntity.ok("회원탈퇴 성공!");
    }

    @GetMapping("") //내 정보 겟
    public ResponseEntity<?> getUser(Authentication authentication){
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        entity.put("Id", user.get().getId().toString());
        entity.put("Phone", user.get().getPhone());
        entity.put("Current Address", user.get().getCurrent());
        entity.put("All Addresses", user.get().getAddresses());
        entities.add(entity);
        return new ResponseEntity<Object>(entities, HttpStatus.OK);
    }
    //회원가입 otp 확인하고 유저 만듬
    @PostMapping("")
    public ResponseEntity<?> registerUser(@RequestBody UserDto.UserRegisterDto newUser) {
        int otp = newUser.getOtp();
        if (otp >= 0) {
            int serverOtp = otpService.getOtp(newUser.getPhone());
            System.out.println("system otp : " + serverOtp);
            if (serverOtp > 0) {
                if (otp == serverOtp) {
                    otpService.clearOTP(newUser.getPhone());
                    createUser(newUser);
                    Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(newUser.getPhone(), newUser.getPhone()));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    String access = jwtUtils.generateJwtToken(authentication);
                    JSONObject entity = new JSONObject();
                    entity.put("access", access);
                    return new ResponseEntity<Object>(entity, HttpStatus.OK);
                }
                else {
                    return ResponseEntity.badRequest().body("Invalid Otp!");
                }
            }
            else {
                return ResponseEntity.badRequest().body("Otp has expired!");
            }
        }
        else {
            return ResponseEntity.badRequest().body("FAIL");
        }
    }
    @PostMapping("/sudo") //테스트할때 문자인증 없이 회원가입용
    public ResponseEntity<?> createUser(@RequestBody UserDto.UserRegisterDto user){
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
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        newUser.addRole(userRole);
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

}

package tp.farming_springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.config.JwtUtils;
import tp.farming_springboot.config.UserDetailsImpl;
import tp.farming_springboot.domain.user.dto.UserDto;
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.domain.user.model.ERole;
import tp.farming_springboot.domain.user.model.Role;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.AddressRepository;
import tp.farming_springboot.domain.user.repository.RoleRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;
@CrossOrigin
@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/api/test/user/")
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


    @GetMapping("/") // 데이터 조회
    public String hello(){
        return "Main Page for Testing Users";
    }

    @GetMapping("/all")
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id){
        Long userID = Long.parseLong(id);
        Optional<User> user = userRepository.findById((userID));
        return user.get();
    }

    @GetMapping("/init")
    public void init (){
        Role roleUser = new Role();
        Role roleAdmin = new Role();
        roleUser.setName(ERole.ROLE_USER);
        roleRepository.save(roleUser);
        roleAdmin.setName(ERole.ROLE_ADMIN);
        roleRepository.save(roleAdmin);
    }

    @GetMapping("/phone/{phone}")
    public Long getUserId (@PathVariable String phone){
        Optional<User> user = userRepository.findByPhone(phone);
        return user.get().getId();
    }

    @PostMapping("/phone/")
    public Long getUserIdByRequest (@RequestBody User usr){
        Optional<User> user = userRepository.findByPhone(usr.getPhone());
        //return phone.();
        return user.get().getId();
    }

    @PostMapping("/update/{id}") //데이터 수정
    public User updateUser(@PathVariable String id, @RequestBody User newUser ){
        Long userID = Long.parseLong(id);
        Optional<User> user = userRepository.findById(userID);
        user.get().setId(newUser.getId());
        user.get().setPhone(newUser.getPhone());
        userRepository.save(user.get());
        return user.get();
    }

    @PostMapping("/newAddress/{id}") //주소만 추가
    public User updateUser(@PathVariable String id, @RequestBody String newAddressString ){
        Long userID = Long.parseLong(id);
        Optional<User> user = userRepository.findById(userID);
        Address newAddress = new Address();
        newAddress.setUser_id(user.get().getId());
        newAddress.setContent(newAddressString);
        addressRepository.save(newAddress);
        user.get().addAddress(newAddress);
        userRepository.save(user.get());
        return user.get();
    }

    private User createUser(@RequestBody UserDto.UserRegisterDto user){
        User newUser = new User(user.getPhone(),user.getAddress());
        //번호로 비밀번호 생성
        newUser.setPassword(encoder.encode(newUser.getPhone()));
        //현주소를 유저의 주소록에 저장
        Address newAddress = new Address();
        newAddress.setUser_id(newUser.getId());
        newAddress.setContent(newUser.getAddress());
        addressRepository.save(newAddress);
        newUser.addAddress(newAddress);
        //유저 롤 추가
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        newUser.addRole(userRole);
        userRepository.save(newUser);
        return newUser;
    }

    @DeleteMapping("/delete/{id}") //데이터 삭제
    public String deleteUser(@PathVariable String id){
        Long userID = Long.parseLong(id);
        userRepository.deleteById(userID);
        return "Deleted user";
    }

    @PostMapping("/signin")
    public String authenticateUser(@RequestBody UserDto.UserLoginDto logger) {
        if (userRepository.existsByPhone(logger.getPhone())) {
            Optional<User> user = userRepository.findByPhone(logger.getPhone());
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(user.get().getPhone(), user.get().getPhone()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                return "Logged in! token : " + jwt;
            } catch (Exception ex) {
                if (ex.getMessage().contains("io.jsonwebtoken.ExpiredJwtException")) {
                    System.out.println("dkdkdkddk");
                    // Refresh Token
                    //refreshToken();
                    // try again with refresh token
                    //response = getData();
                } else {
                    System.out.println("aaaaaaaaaa");
                    System.out.println(ex);
                }
            }
        }
        else {
            return "Phone number does not exist in db";
        }
        return null;
    }

    @PostMapping("/signup")
    public String registerUser(@RequestBody UserDto.UserRegisterDto newUser) {
        if (userRepository.existsByPhone(newUser.getPhone())) {
            return "Phone number is already taken";
        }
        createUser(newUser);
        return "User registered!";
    }
}

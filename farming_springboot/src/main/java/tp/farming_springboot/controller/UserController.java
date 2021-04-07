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
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.domain.user.model.ERole;
import tp.farming_springboot.domain.user.model.Role;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.AddressRepository;
import tp.farming_springboot.domain.user.repository.RoleRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

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
    @GetMapping("/{phone}")
    public Long getUserId (@PathVariable String phone){
        Optional<User> user = userRepository.findByPhone(phone);
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

    @PutMapping("/create") //데이터 삽입
    public User createUser(@RequestBody User user){

        //현주소를 유저의 주소록에 저장
        Address newAddress = new Address();
        newAddress.setUser_id(user.getId());
        newAddress.setContent(user.getAddress());
        addressRepository.save(newAddress);
        user.addAddress(newAddress);


        //유저 롤 추가
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.addRole(userRole);
        User newUser = userRepository.save(user);
        return newUser;
    }

    @DeleteMapping("/delete/{id}") //데이터 삭제
    public String deleteUser(@PathVariable String id){
        Long userID = Long.parseLong(id);
        userRepository.deleteById(userID);
        return "Deleted user";
    }

    @PostMapping("/signin")
    public String authenticateUser( @RequestBody User user) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getPhone(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        /*
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());*/

        return "Logged in! token : "+jwt;

    }

    @PostMapping("/signup")
    public String registerUser(@RequestBody User newuser) {
    //public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByPhone(newuser.getPhone())) {
            return "Phone number is already taken";
                   // .badRequest()
                    //.body(new MessageResponse("Error: Phone number is already taken!"));
        }
        newuser.setPassword(encoder.encode(newuser.getPassword()));
        createUser(newuser);

        return "User registered!";
    }

}

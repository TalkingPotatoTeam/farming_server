package tp.farming_springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.domain.user.model.ERole;
import tp.farming_springboot.domain.user.model.Role;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.AddressRepository;
import tp.farming_springboot.domain.user.repository.RoleRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;

import java.util.*;

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

    @PutMapping("/create") //데이터 삽입 //핸드폰번호가 이미 있는거 또 생성할려고하면 오류 뜸
    public User createUser(@RequestBody User user){
        User newUser = userRepository.save(user);
        //현주소를 유저의 주소록에 저장
        Address newAddress = new Address();
        newAddress.setUser_id(user.getId());
        newAddress.setContent(user.getAddress());
        addressRepository.save(newAddress);
        user.addAddress(newAddress);

        /*
        try{
        Role ru = new Role();
        ru.setName(ERole.ROLE_USER);
        roleRepository.save(ru);}catch(Exception e){}*/

        //유저 롤 추가
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.addRole(userRole);

        return newUser;
    }

    @DeleteMapping("/delete/{id}") //데이터 삭제
    public String deleteUser(@PathVariable String id){
        Long userID = Long.parseLong(id);
        userRepository.deleteById(userID);
        return "Deleted user";
    }


}

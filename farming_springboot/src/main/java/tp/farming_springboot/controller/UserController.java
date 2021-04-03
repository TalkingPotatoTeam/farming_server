package tp.farming_springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@EnableAutoConfiguration
@RequestMapping(value = "/")
public class UserController {
    @Autowired
    UserRepository userRepository;


    //연습용 매핑해서 바로 확인용
    @GetMapping("/") // 데이터 조회
    public String hello(){
        return "Main Page";
    }

    @GetMapping("/post")
    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    @GetMapping("/post/{id}")
    public User getUser(@PathVariable String id){
        Long userID = Long.parseLong(id);
        Optional<User> user = userRepository.findById((userID));
        return user.get();
    }

    @PostMapping("/post/{id}") //데이터 수정
    public User updateUser(@PathVariable String id, @RequestBody User newUser ){
        Long userID = Long.parseLong(id);
        Optional<User> user = userRepository.findById(userID);
        user.get().setId(newUser.getId());
        user.get().setPhone(newUser.getPhone());
        userRepository.save(user.get());
        return user.get();
    }

    @PutMapping("/post") //데이터 삽입
    public User createUser(@RequestBody User user){
        User newUser = userRepository.save(user);
        return newUser;
    }

    @DeleteMapping("/post/{id}") //데이터 삭제
    public String deleteUser(@PathVariable String id){
        Long userID = Long.parseLong(id);
        userRepository.deleteById(userID);
        return "Deleted user";
    }


}

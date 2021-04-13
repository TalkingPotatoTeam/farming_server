package tp.farming_springboot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.UserRepository;

import java.security.Principal;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
//@RequestMapping("/api/")
public class LoginTestController {
    @Autowired
    UserRepository userRepository;
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user1")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String currentUserName(Principal principal) {
        System.out.println("a");
        Optional<User> user = userRepository.findByPhone(principal.getName());
        System.out.println("b");
        return user.get().getId().toString() + user.get().getPhone() + user.get().getAddress();
    }

    @GetMapping("/user2")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String currentUsersName(Authentication authentication) {
        Optional<User> user = userRepository.findByPhone(authentication.getName());
        return user.get().getId().toString() + user.get().getPhone() + user.get().getAddress();
    }


    @GetMapping("/authenticate")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
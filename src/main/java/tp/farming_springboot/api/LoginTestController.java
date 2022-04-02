package tp.farming_springboot.api;


import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import tp.farming_springboot.domain.entity.User;
import tp.farming_springboot.domain.repository.UserRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@ApiIgnore
public class LoginTestController {
    private final UserRepository userRepository;

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
        return user.get().getId().toString() + user.get().getPhone() + user.get().getCurrent();
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> currentUsersName(Authentication authentication) {
        Optional<User> user = userRepository.findByPhone(authentication.getName());

        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        entity.put("Id", user.get().getId().toString());
        entity.put("Phone", user.get().getPhone());
        entity.put("Address", user.get().getCurrent());
        entities.add(entity);
        return new ResponseEntity<Object>(entities, HttpStatus.OK);
    }


    @GetMapping("/authenticate")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }




}
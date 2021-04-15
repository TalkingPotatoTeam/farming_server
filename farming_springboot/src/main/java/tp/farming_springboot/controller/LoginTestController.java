package tp.farming_springboot.controller;


import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
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
import java.util.HashMap;
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


    @GetMapping("/sendSMS")
    public void testSend() {
        String api_key = "NCSVJBMVYB7TOOQV"; //사이트에서 발급 받은 API KEY
        String api_secret = "DYGDIZQPLVPHNEXMXQAH2YJFXZOL36NG"; //사이트에서 발급 받은API SECRET KEY
        Message coolsms = new Message(api_key, api_secret);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", "01073408629");
        params.put("from", "01073408629"); //사전에 사이트에서 번호를 인증하고 등록하여야 함
        params.put("type", "SMS"); params.put("text", "Test Message123");//메시지 내용
        params.put("app_version", "test app 1.2");
        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString()); //전송 결과 출력
            }
        catch (CoolsmsException e)
            {
                System.out.println(e.getMessage());
                System.out.println(e.getCode());
            }

    }

}
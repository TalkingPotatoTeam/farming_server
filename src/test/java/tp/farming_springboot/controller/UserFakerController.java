package tp.farming_springboot.controller;


import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tp.farming_springboot.domain.user.dto.UserForceCreateDto;
import tp.farming_springboot.domain.user.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserFakerController {

    @Autowired
    private UserService userService;




    @Test
    public void faker() {
        Faker faker = new Faker();
        for(int i=0; i<100000; i++) {
            String phoneNum = faker.phoneNumber().cellPhone();
            String address = faker.address().fullAddress();
            UserForceCreateDto userForceCreateDto = new UserForceCreateDto(phoneNum, address);
            userService.createUserForce(userForceCreateDto);
        }
    }
    

}

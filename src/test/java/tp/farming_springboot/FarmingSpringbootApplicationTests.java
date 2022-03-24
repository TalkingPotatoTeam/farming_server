package tp.farming_springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tp.farming_springboot.domain.dao.User;
import tp.farming_springboot.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class FarmingSpringbootApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
    }
    @Test
    @Transactional
    public void test1() {
        String phone = "123-456";
        User user = new User(phone);


        System.out.println("레포지토리 save를 호출했습니다.");


      //  User user1 = userRepository.findByPhoneElseThrow(phone);
        System.out.println("레포지토리 조회를 호출했습니다.");
        userRepository.save(user);
        System.out.println("레포지토리 save 호출했습니다.");
    }

    @Test
    public void test2() {

        List<String> stringList = new ArrayList<>();
        stringList.add("hi12313131");
        stringList.add("rritia");
        stringList.add("testcode");

        System.out.println("stringList = " + stringList);
        Collections.sort(stringList, (a, b) -> a.length() - b.length());
        System.out.println("stringList = " + stringList);

    }

}

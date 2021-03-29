package tp.farming_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tp.farming_springboot.controller.UserController;
import tp.farming_springboot.model.User;

@SpringBootApplication
public class FarmingSpringbootApplication {

    public static void main(String[] args) {

        SpringApplication.run(FarmingSpringbootApplication.class, args);

    }

}

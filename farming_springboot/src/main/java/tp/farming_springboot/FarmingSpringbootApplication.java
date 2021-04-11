package tp.farming_springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tp.farming_springboot.controller.UserController;
import tp.farming_springboot.domain.user.model.ERole;
import tp.farming_springboot.domain.user.model.Role;
import tp.farming_springboot.domain.user.repository.RoleRepository;

@SpringBootApplication
public class FarmingSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmingSpringbootApplication.class, args);
    }

}

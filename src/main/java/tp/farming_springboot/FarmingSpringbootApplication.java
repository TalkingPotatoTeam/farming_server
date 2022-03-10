package tp.farming_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FarmingSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmingSpringbootApplication.class, args);
    }

}
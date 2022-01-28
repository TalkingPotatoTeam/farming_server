package tp.farming_springboot.controller;


import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;


public class UserFakerController {

    

    @Test
    public void faker() {
        Faker faker = new Faker();

        System.out.println("faker.name().fullName() = " + faker.name().fullName());
    }
}

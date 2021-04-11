package tp.farming_springboot.domain.user.dto;

import lombok.Setter;
import lombok.Getter;

public class UserDto {
    @Getter
    public static class UserRegisterDto{
        private String phone;
        private String address;
    }
    @Getter
    public static class UserLoginDto{
        private String phone;
    }
}

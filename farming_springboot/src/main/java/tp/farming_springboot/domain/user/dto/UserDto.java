package tp.farming_springboot.domain.user.dto;

import lombok.Setter;
import lombok.Getter;

public class UserDto {
    @Getter
    public static class UserRegisterDto{
        private String phone;
        private String address;
        private int otp;
    }
    @Getter
    public static class UserRequestOtpDto{
        private String phone;
    }
    @Getter
    public static class UserAuthDto{
        private String phone;
    }
}

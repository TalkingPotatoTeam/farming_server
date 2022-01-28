package tp.farming_springboot.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserCreateDto {
    private String phone;
    private int otp;
}

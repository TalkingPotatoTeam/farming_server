package tp.farming_springboot.domain.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.domain.user.model.User;


@Data
@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private Address currentAddress;

    public static UserResponseDto from(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.id = user.getId();
        userResponseDto.currentAddress = user.getCurrent();

        return userResponseDto;
    }
}

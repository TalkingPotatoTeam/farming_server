package tp.farming_springboot.domain.user.dto;

import lombok.*;
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.domain.user.model.User;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeUserResDto {
    private Long id;
    private Address currentAddress;

    public static LikeUserResDto from(User user) {
        LikeUserResDto userResponseDto = new LikeUserResDto();
        userResponseDto.id = user.getId();
        userResponseDto.currentAddress = user.getCurrent();

        return userResponseDto;
    }
}

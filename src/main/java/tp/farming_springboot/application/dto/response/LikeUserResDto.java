package tp.farming_springboot.application.dto.response;

import lombok.*;
import tp.farming_springboot.domain.dao.Address;
import tp.farming_springboot.domain.dao.User;


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

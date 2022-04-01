package tp.farming_springboot.application.dto.response;

import lombok.*;
import tp.farming_springboot.domain.entity.Address;
import tp.farming_springboot.domain.entity.User;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeUserResDto {
    private Long id;

    public static LikeUserResDto from(User user) {
        return new LikeUserResDto(user.getId());
    }
}

package tp.farming_springboot.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResDto {
    private Long id;
    private String phone;
    private String currentAddress;

    public static UserResDto of(Long id, String phone, String currentAddress) {
        return new UserResDto(id, phone, currentAddress);
    }
}

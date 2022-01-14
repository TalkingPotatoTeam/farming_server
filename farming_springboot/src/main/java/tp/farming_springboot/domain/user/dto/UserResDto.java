package tp.farming_springboot.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tp.farming_springboot.domain.user.model.Address;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResDto {
    private Long id;
    private String phone;
    private Address currentAddress;

    public static UserResDto of(Long id, String phone, Address currentAddress) {
        return new UserResDto(id, phone, currentAddress);
    }
}

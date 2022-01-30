package tp.farming_springboot.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import tp.farming_springboot.domain.user.model.Address;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserForceCreateDto {
    private String phone;
    private String address;
}

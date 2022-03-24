package tp.farming_springboot.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserForceCreateDto {
    private String phone;
    private String address;
}

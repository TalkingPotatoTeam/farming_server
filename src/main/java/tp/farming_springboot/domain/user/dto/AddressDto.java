package tp.farming_springboot.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddressDto {
    private String content;
    private Double lat;
    private Double lon;
}

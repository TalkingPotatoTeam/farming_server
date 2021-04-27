package tp.farming_springboot.domain.product.dto;


import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@AllArgsConstructor
@Getter
@Setter
public class ProductCreateDto {
    private String title;
    private String content;
    private String price;
    private String quantity;
    private String address;
    private boolean certified;
}

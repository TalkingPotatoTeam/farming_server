package tp.farming_springboot.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tp.farming_springboot.domain.product.model.Category;
import tp.farming_springboot.domain.product.model.PhotoFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@Getter
@Setter
public class ProductCreateDto {
    @NotBlank(message="Product title can't be blank.")
    private String title;
    @NotBlank(message="Product content can't be blank.")
    private String content;
    @NotBlank(message="Product price can't be blank.")
    private String price;

    private String quantity;

    private String address;

    private boolean certified;

    private List<PhotoFile> photoFile;

    private PhotoFile receipt;

    @NotBlank(message="Product category can't be blank.")
    private String category;

    private LocalDateTime buyProductDate;

    private String freshness;

}


package tp.farming_springboot.domain.product.dto;

import lombok.*;
import tp.farming_springboot.domain.product.model.Category;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.user.model.User;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductCreateDto {

    @NotBlank(message="Product title can't be blank.")
    private String title;
    @NotBlank(message="Product content can't be blank.")
    private String content;
    @NotBlank(message="Product price can't be blank.")
    private String price;

    private boolean certified;
    private List<PhotoFile> photoFile;
    private PhotoFile receipt;
    @NotBlank(message="Product category can't be blank.")
    private String categoryName;
    private Category category;
    private String buyProductDate;
    private String freshness;

}


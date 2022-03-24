package tp.farming_springboot.application.dto.request;

import lombok.*;
import tp.farming_springboot.domain.dao.Category;
import tp.farming_springboot.domain.dao.PhotoFile;

import javax.validation.constraints.NotBlank;
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


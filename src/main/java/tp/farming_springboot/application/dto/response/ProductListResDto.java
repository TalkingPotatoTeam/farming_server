package tp.farming_springboot.application.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import tp.farming_springboot.domain.entity.PhotoFile;
import tp.farming_springboot.domain.entity.Product;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@NoArgsConstructor

/*
 * 홈 화면 게시물 리스트를 위한 DTO입니다.
 */
public class ProductListResDto {
    private Long id;
    private String title;
    private String price;
    private boolean certified;
    private PhotoFileDto photoFile; // 대표 사진 1개
    private LocalDateTime createdAt;

    public static ProductListResDto from(Product product) {
        ProductListResDto productListResDto = new ProductListResDto();
        productListResDto.id = product.getId();
        productListResDto.title = product.getTitle();
        productListResDto.price = product.getPrice();
        productListResDto.certified = product.isCertified();

        Optional<PhotoFile> thumbnail = product.getPhotoFile().stream().findFirst();

        if(thumbnail.isPresent()) {
            productListResDto.photoFile = PhotoFileDto.of(thumbnail.get().getOrigFilename(), thumbnail.get().getUrl());
        }

        productListResDto.createdAt = product.getCreatedAt();
        return productListResDto;
    }

}

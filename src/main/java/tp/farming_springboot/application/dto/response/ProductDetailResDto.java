package tp.farming_springboot.application.dto.response;

import lombok.*;
import tp.farming_springboot.domain.entity.Freshness;
import tp.farming_springboot.domain.entity.Product;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor

/*
 * 게시물 상세 화면을 위한 DTO입니다.
 */
public class ProductDetailResDto {
    private Long id;
    private String title;
    private String content;
    private Long price;
    private Long userId;
    private String address;
    private boolean certified;
    private List<PhotoFileDto> photoFile;
    private PhotoFileDto receipt;
    private String category;
    private Date buyProductDate;
    private int freshnessId;
    private int productStatusId;

    public static ProductDetailResDto from(Product product) {
        ProductDetailResDto productResponseDto = new ProductDetailResDto();
        productResponseDto.id = product.getId();
        productResponseDto.title = product.getTitle();
        productResponseDto.content = product.getContent();
        productResponseDto.price = product.getPrice();

        // 유저 필수
        productResponseDto.userId = product.getUser().getId();
        productResponseDto.address = product.getAddress();
        productResponseDto.certified = product.isCertified();

        // 카테고리는 필수
        productResponseDto.category = product.getCategory().getName();

        productResponseDto.buyProductDate = product.getBuyProductDate();

        // 필수는 아니지만 stream으로 NPE 안전
        productResponseDto.photoFile = product.getPhotoFile().stream().map(
                f -> PhotoFileDto.of(f.getOrigFilename(), f.getUrl())
        ).collect(Collectors.toList());

        // 영수증 필수 X
        if(product.getReceipt() != null)
            productResponseDto.receipt = PhotoFileDto.of(product.getReceipt().getOrigFilename(), product.getReceipt().getUrl());

        // 신선도 필수 X
        if(product.getFreshness() != null)
            productResponseDto.freshnessId = product.getFreshness().getId();

        productResponseDto.productStatusId = product.getProductStatus().getId();

        return productResponseDto;
    }


}

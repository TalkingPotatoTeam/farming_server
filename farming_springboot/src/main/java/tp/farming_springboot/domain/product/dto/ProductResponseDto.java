package tp.farming_springboot.domain.product.dto;

import lombok.*;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.model.Product;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String title;
    private String content;
    private String price;
    private Long userId;
    private String quantity;
    private String address;
    private boolean certified;
    private List<PhotoFile> photoFile;
    private PhotoFile receipt;
    private String category;
    private Date buyProductDate;
    private String freshness;
    private String productStatus;

    public static ProductResponseDto from(Product product) {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.id = product.getId();
        productResponseDto.title = product.getTitle();
        productResponseDto.content = product.getContent();
        productResponseDto.price = product.getPrice();
        productResponseDto.userId = product.getUser().getId();
        productResponseDto.quantity = product.getQuantity();
        productResponseDto.address = product.getAddress();
        productResponseDto.certified = product.isCertified();
        //productResponseDto.photoFile = product.getPhotoFile();
        productResponseDto.receipt = product.getReceipt();
        productResponseDto.category = product.getCategory().getName();
        productResponseDto.buyProductDate = product.getBuyProductDate();
        productResponseDto.freshness = product.getFreshness();
        productResponseDto.productStatus = product.getProductStatus();

        return productResponseDto;
    }


}

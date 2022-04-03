package tp.farming_springboot.domain.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum ProductStatus {
    판매중(0, "판매중"),
    예약중(1, "예약중"),
    판매완료(2, "판매완료");

    int id;
    String content;

    ProductStatus(int id, String content) {
        this.id = id;
        this.content = content;
    }
    public static ProductStatus fromId(int id) {
        Optional<ProductStatus> productStatus = Arrays.stream(ProductStatus.values())
                .filter(f -> f.getId() == id)
                .findAny();

        return productStatus.isPresent() ? productStatus.get() : ProductStatus.판매중;

    }
}

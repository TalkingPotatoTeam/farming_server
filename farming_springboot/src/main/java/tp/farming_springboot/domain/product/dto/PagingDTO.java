package tp.farming_springboot.domain.product.dto;

import lombok.Data;
import lombok.Getter;
import tp.farming_springboot.domain.user.model.User;

import java.time.LocalDateTime;
@Data
public class PagingDTO {
    private Long id;
    private String title;
    private Long createdBy;
    private LocalDateTime createdDate;
    private boolean certified;
    private String price;


    public PagingDTO(Long id, Long createdBy, String title ,LocalDateTime createdDate, boolean certified, String price) {
        this.id = id;
        this.createdBy = createdBy;
        this.title = title;
        this.createdDate = createdDate;
        this.certified = certified;
        this.price = price;
    }
}

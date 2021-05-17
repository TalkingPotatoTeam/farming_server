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

    public PagingDTO(Long id, String title, Long createdBy, LocalDateTime createdDate){
        this.id = id;
        this.title = title;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }
}

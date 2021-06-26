package tp.farming_springboot.domain.review.model;

import lombok.Getter;
import lombok.Setter;
import tp.farming_springboot.domain.product.model.Product;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class ReviewChoice {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String reviewContent;


    // 부정적인 리뷰인지, 아닌지 체크 용도
    @Getter
    @Setter
    private String tag;

}
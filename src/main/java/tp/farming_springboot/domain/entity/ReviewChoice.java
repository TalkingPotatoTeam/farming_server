package tp.farming_springboot.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
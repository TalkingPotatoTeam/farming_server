package tp.farming_springboot.domain.review.model;


import lombok.Getter;
import lombok.Setter;
import tp.farming_springboot.domain.user.model.User;

import javax.persistence.*;

@Entity
public class Review {

    public Review(Long id, User user, String reviewContent, int tag) {
        this.id = id;
        this.user = user;
        this.reviewContent = reviewContent;
        this.tag = tag;
    }

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name ="user_id")
    // JoinColumn => 참조하는 (객체 이름_필드이름)
    @Getter
    @Setter
    private User user;

    @Getter
    @Setter
    String reviewContent;


    // 부정적인 리뷰인지, 아닌지 체크 용도
    @Getter
    @Setter
    int tag;

}

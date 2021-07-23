package tp.farming_springboot.domain.review.model;


import lombok.Getter;
import lombok.Setter;
import tp.farming_springboot.domain.user.model.User;

import javax.persistence.*;

@Entity
public class Review {

    public Review(User reviewer, User reviewee, ReviewChoice reviewContent) {
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.reviewContent = reviewContent;
    }

    public Review(){

    }

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name ="reviewer_id")
    // JoinColumn => 참조하는 (객체 이름_필드이름)
    @Getter
    @Setter
    private User reviewer;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name ="reviewee_id")
    // JoinColumn => 참조하는 (객체 이름_필드이름)
    @Getter
    @Setter
    private User reviewee;


    @Getter
    @Setter
    @ManyToOne
    private ReviewChoice reviewContent;



}

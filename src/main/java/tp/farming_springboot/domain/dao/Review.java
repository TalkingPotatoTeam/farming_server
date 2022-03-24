package tp.farming_springboot.domain.dao;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Review {
    @Builder
    public Review(Long questionId, User reviewer, User reviewee, ReviewChoice reviewContent) {
        this.questionId = questionId;
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

    @Getter @Setter
    private Long questionId;


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

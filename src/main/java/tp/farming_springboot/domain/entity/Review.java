package tp.farming_springboot.domain.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tp.farming_springboot.domain.ReviewAnswer;

import javax.persistence.*;

@Entity
public class Review {
    @Builder
    public Review(Long questionId, User reviewer, User reviewee, ReviewAnswer reviewAnswer) {
        this.questionId = questionId;
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.reviewAnswer = reviewAnswer;
    }

    public Review(){

    }

    public static Review of (Long questionId, User reviewer, User reviewee, ReviewAnswer reviewAnswer){
        return new Review(questionId, reviewer, reviewee, reviewAnswer);
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
    @Enumerated(EnumType.STRING)
    private ReviewAnswer reviewAnswer;

}

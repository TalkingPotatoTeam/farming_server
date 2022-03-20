package tp.farming_springboot.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tp.farming_springboot.domain.ReviewAnswer;
import tp.farming_springboot.domain.entity.Review;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private Long id;
    private Long questionId;
    private Long reviewerId;
    private Long revieweeId;
    private ReviewAnswer reviewAnswer;

    public static ReviewDto toEntity(Review review){
        ReviewDto v = new ReviewDto();
        v.id = review.getId();
        v.questionId = review.getQuestionId();
        v.reviewerId = review.getReviewer().getId();
        v.revieweeId = review.getReviewee().getId();
        v.reviewAnswer = review.getReviewAnswer();

        return v;
    }

}
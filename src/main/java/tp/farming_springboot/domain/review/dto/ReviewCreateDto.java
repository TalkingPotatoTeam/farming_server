package tp.farming_springboot.domain.review.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.review.model.Review;
import tp.farming_springboot.domain.review.model.ReviewChoice;
import tp.farming_springboot.domain.user.model.User;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateDto {
    //1 : 게시물 2: 전반적 매너 3: 시간약속
    @NotBlank(message="Review question Id can't be blank.")
    private Long questionId;
    @NotBlank(message="Review answer content can't be blank.")
    private String reviewContent;

    private ReviewChoice reviewChoice;
    private User reviewee;
    private User reviewer;

    public Review toEntity() {
        Review build = Review.builder()
                .questionId(this.questionId)
                .reviewee(this.reviewee)
                .reviewer(this.reviewer)
                .reviewContent(this.reviewChoice)
                .build();
        return build;
    }
}

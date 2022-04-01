package tp.farming_springboot.application.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tp.farming_springboot.domain.entity.Review;
import tp.farming_springboot.domain.entity.ReviewChoice;
import tp.farming_springboot.domain.entity.User;

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

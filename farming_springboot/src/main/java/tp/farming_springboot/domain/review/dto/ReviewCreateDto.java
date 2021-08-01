package tp.farming_springboot.domain.review.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.review.model.Review;
import tp.farming_springboot.domain.review.model.ReviewChoice;
import tp.farming_springboot.domain.user.model.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCreateDto {
    private String reviewContent;

    private ReviewChoice reviewChoice;
    private User reviewee;
    private User reviewer;

    public Review toEntity() {
        Review build = Review.builder()
                .reviewee(this.reviewee)
                .reviewer(this.reviewer)
                .reviewContent(this.reviewChoice)
                .build();
        return build;
    }
}

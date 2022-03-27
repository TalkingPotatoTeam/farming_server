package tp.farming_springboot.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tp.farming_springboot.application.dto.request.ReviewCreateRequestDto;
import tp.farming_springboot.application.dto.response.ReviewDto;
import tp.farming_springboot.domain.entity.Review;
import tp.farming_springboot.domain.entity.User;
import tp.farming_springboot.domain.exception.UserNotAuthorizedException;
import tp.farming_springboot.domain.repository.ReviewRepository;
import tp.farming_springboot.domain.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public ReviewDto create(Authentication authentication, ReviewCreateRequestDto reviewCreateRequestDto) throws UserNotAuthorizedException {
        String userPhone = authentication.getName();
        Long revieweeId = reviewCreateRequestDto.getRevieweeId();

        User reviewer = userRepository.findByPhoneElseThrow(userPhone);
        User reviewee = userRepository.findByIdElseThrow(revieweeId);

        if(reviewer.getId() == reviewee.getId()) {
            throw new UserNotAuthorizedException("Can't review your self.");
        }

        Review review = Review.of(
                reviewCreateRequestDto.getQuestionId(),
                reviewer,
                reviewee,
                reviewCreateRequestDto.getReviewAnswer()
        );

        return ReviewDto.toEntity(this.reviewRepository.save(review));
    }
}

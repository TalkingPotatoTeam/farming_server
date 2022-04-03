package tp.farming_springboot.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tp.farming_springboot.application.dto.request.ReviewCreateReqDto;
import tp.farming_springboot.application.dto.response.ReviewResDto;
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

    public ReviewResDto create(Authentication authentication, ReviewCreateReqDto reviewCreateReqDto) throws UserNotAuthorizedException {
        String userPhone = authentication.getName();
        Long revieweeId = reviewCreateReqDto.getRevieweeId();

        User reviewer = userRepository.findByPhoneElseThrow(userPhone);
        User reviewee = userRepository.findByIdElseThrow(revieweeId);

        if(reviewer.getId() == reviewee.getId()) {
            throw new UserNotAuthorizedException("Can't review your self.");
        }

        Review review = Review.of(
                reviewCreateReqDto.getQuestionId(),
                reviewer,
                reviewee,
                reviewCreateReqDto.getReviewAnswer()
        );

        return ReviewResDto.toEntity(this.reviewRepository.save(review));
    }
}

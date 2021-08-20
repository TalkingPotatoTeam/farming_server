package tp.farming_springboot.domain.review.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tp.farming_springboot.domain.review.dto.ReviewCreateDto;
import tp.farming_springboot.domain.review.model.Review;
import tp.farming_springboot.domain.review.model.ReviewChoice;
import tp.farming_springboot.domain.review.repository.ReviewChoiceRepository;
import tp.farming_springboot.domain.review.repository.ReviewRepository;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.service.UserService;
import tp.farming_springboot.exception.RestNullPointerException;
import tp.farming_springboot.exception.UserNotAuthorizedException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private final ReviewChoiceRepository reviewChoiceRepository;

    public void create(String userPhone, Long revieweeId, ReviewCreateDto reviewDto) throws UserNotAuthorizedException {
        User reviewer = userService.findUserByPhone(userPhone);
        User reviewee = userService.findUserById(revieweeId);

        if(reviewer.getId() == reviewee.getId()) {
            throw new UserNotAuthorizedException("Can't review your self.");
        }
        System.out.println("reviewDto.getReviewContent() = " + reviewDto.getReviewContent());

        String reviewContentKeyString = "";
        Long questionId = reviewDto.getQuestionId();
        String reviewContent = reviewDto.getReviewContent();

        if(questionId == 1) { // 게시글 관련
            if(reviewContent.equals("네, 일치했어요."))
                reviewContentKeyString = "게시물 일치";
            else if(reviewContent.equals("아니요, 일치하지 않았어요."))
                reviewContentKeyString = "게시물 불일치";
            else if(reviewContent.equals("그냥 그래요."))
                reviewContentKeyString = "게시물 보통";
        } else if(questionId == 2) { //전반적인 매너
            if(reviewContent.equals("거래 매너가 좋아요."))
                reviewContentKeyString = "매너 좋음";
            else if(reviewContent.equals("그냥 그래요."))
                reviewContentKeyString = "매너 보통";
            else if(reviewContent.equals("거래 매너가 아쉬워요."))
                reviewContentKeyString = "매너 별로";

        } else if(questionId == 3) { // 시간약속
            if(reviewContent.equals("시간약속을 잘 지켰어요."))
                reviewContentKeyString = "시간약속 지킴";
            else if(reviewContent.equals("시간약속을 지키지 않았어요."))
                reviewContentKeyString = "시간약속 안지킴";
            else if(reviewContent.equals("그냥 그래요."))
                reviewContentKeyString = "시간약속 보통";
        }

        System.out.println("reviewContentKeyString = " + reviewContentKeyString);
        ReviewChoice reviewChoice = reviewChoiceRepository.findByReviewContent(reviewContentKeyString)
                .orElseThrow(()-> new RestNullPointerException("Can`t find review content."));


        reviewDto.setReviewChoice(reviewChoice);
        reviewDto.setReviewee(reviewee);
        reviewDto.setReviewer(reviewer);

        Review review = reviewDto.toEntity();
        reviewRepository.save(review);
    }
}

package tp.farming_springboot.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.domain.review.dto.ReviewCreateDto;
import tp.farming_springboot.domain.review.model.Review;
import tp.farming_springboot.domain.review.model.ReviewChoice;
import tp.farming_springboot.domain.review.repository.ReviewChoiceRepository;
import tp.farming_springboot.domain.review.repository.ReviewRepository;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.UserRepository;
import tp.farming_springboot.exception.RestNullPointerException;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping(value="/review")
@AllArgsConstructor
public class ReviewController {
    private final UserRepository userRepository;
    private final ReviewChoiceRepository reviewChoiceRepository;
    private final ReviewRepository reviewRepository;

    @ExceptionHandler
    public ResponseEntity<Message> handler(RestNullPointerException e) {
        Message message = e.getMsg();
        HttpHeaders headers = e.getHeaders();
        HttpStatus httpStatus = e.getHttpStatus();

        return new ResponseEntity<>(message, headers, httpStatus);
    }

    @PostMapping("/{revieweeId}")
    @ResponseBody
    public ResponseEntity<Message> create(Principal principal,
                                          @RequestPart ReviewCreateDto reviewDto,
                                          @PathVariable Long revieweeId){
        Message message = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Optional<User> reviewer = userRepository.findByPhone(principal.getName());
        reviewer.orElseThrow(() -> new RestNullPointerException(headers, "User Token invalid.", HttpStatus.UNAUTHORIZED, null, StatusEnum.UNAUTHORIZED));


        Optional<User> reviewee = userRepository.findById(revieweeId);
        reviewer.orElseThrow(() -> new RestNullPointerException(headers, "User Token invalid.", HttpStatus.UNAUTHORIZED, null, StatusEnum.UNAUTHORIZED));


        Review review = new Review();
        review.setReviewer(reviewer.get());
        review.setReviewee(reviewee.get());

        System.out.println(reviewDto.getReviewContent());
        if(reviewDto.getReviewContent().equals("네, 일치했어요.")) {
            Optional<ReviewChoice> reviewChoice = reviewChoiceRepository.findByReviewContent("게시물 일치");
            review.setReviewContent(reviewChoice.get());
        }
        else if(reviewDto.getReviewContent().equals("아니요, 일치하지 않았어요.")) {
            Optional<ReviewChoice> reviewChoice = reviewChoiceRepository.findByReviewContent("게시물 불일치");
            review.setReviewContent(reviewChoice.get());
        }
        else if(reviewDto.getReviewContent().equals("거래 매너가 좋아요.")) {
            Optional<ReviewChoice> reviewChoice = reviewChoiceRepository.findByReviewContent("매너 좋음");
            review.setReviewContent(reviewChoice.get());
        }
        else if(reviewDto.getReviewContent().equals("그냥 그래요.")) {
            Optional<ReviewChoice> reviewChoice = reviewChoiceRepository.findByReviewContent("매너 보통");
            review.setReviewContent(reviewChoice.get());
        }
        else if(reviewDto.getReviewContent().equals("거래 매너가 별로에요.")) {
            Optional<ReviewChoice> reviewChoice = reviewChoiceRepository.findByReviewContent("매너 별로");
            review.setReviewContent(reviewChoice.get());
        }
        else if(reviewDto.getReviewContent().equals("시간약속을 잘 지켰어요.")) {
            Optional<ReviewChoice> reviewChoice = reviewChoiceRepository.findByReviewContent("시간약속 지킴");
            review.setReviewContent(reviewChoice.get());
        }
        else if(reviewDto.getReviewContent().equals("시간약속을 지키지 않았어요.")) {
            Optional<ReviewChoice> reviewChoice = reviewChoiceRepository.findByReviewContent("시간약속 안지킴");
            review.setReviewContent(reviewChoice.get());
        }
        Review rev = reviewRepository.save(review);
        message = new Message(StatusEnum.OK, "Posting review is Success.", rev);

        return new ResponseEntity<>(message,headers, HttpStatus.OK);
    }
}

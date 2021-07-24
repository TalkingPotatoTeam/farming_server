package tp.farming_springboot.controller;


import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.domain.review.dto.ReviewCreateDto;
import tp.farming_springboot.domain.review.model.Review;
import tp.farming_springboot.domain.review.model.ReviewChoice;
import tp.farming_springboot.domain.review.repository.ReviewChoiceRepository;
import tp.farming_springboot.domain.review.repository.ReviewRepository;
import tp.farming_springboot.domain.review.service.ReviewService;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.UserRepository;
import tp.farming_springboot.domain.user.service.UserService;
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

    private final ReviewService reviewService;


    @PostMapping("/{revieweeId}")
    public String create(Authentication authentication,
                                          @RequestBody ReviewCreateDto reviewDto,
                                          @PathVariable Long revieweeId){

        System.out.println("ReviewController.create");
        String userName = authentication.getName();
        reviewService.create(userName, revieweeId, reviewDto);
        return "Review uploaded.";
    }
}

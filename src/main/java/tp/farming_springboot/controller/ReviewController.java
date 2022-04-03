package tp.farming_springboot.controller;


import lombok.AllArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.domain.review.dto.ReviewCreateRequestDto;
import tp.farming_springboot.domain.review.dto.ReviewDTO;
import tp.farming_springboot.domain.review.service.ReviewService;
import tp.farming_springboot.exception.UserNotAuthorizedException;


@RestController
@RequestMapping(value="/review")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewDTO create(Authentication authentication, @RequestBody ReviewCreateRequestDto reviewCreateRequestDto) throws UserNotAuthorizedException {
        return this.reviewService.create(authentication, reviewCreateRequestDto);
    }
}

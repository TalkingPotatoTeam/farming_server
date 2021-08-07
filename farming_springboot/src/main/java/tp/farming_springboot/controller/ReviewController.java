package tp.farming_springboot.controller;


import lombok.AllArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.domain.review.dto.ReviewCreateDto;
import tp.farming_springboot.domain.review.service.ReviewService;


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
        String userPhone = authentication.getName();
        reviewService.create(userPhone, revieweeId, reviewDto);
        return "Review uploaded.";
    }
}

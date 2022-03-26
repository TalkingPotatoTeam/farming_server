package tp.farming_springboot.api;


import lombok.AllArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.application.dto.request.ReviewCreateDto;
import tp.farming_springboot.application.ReviewService;
import tp.farming_springboot.domain.exception.UserNotAuthorizedException;


@RestController
@RequestMapping(value="/review")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{revieweeId}")
    public String create(Authentication authentication,
                                          @RequestBody ReviewCreateDto reviewDto,
                                          @PathVariable Long revieweeId) throws UserNotAuthorizedException {

        System.out.println("ReviewController.create");
        String userPhone = authentication.getName();
        reviewService.create(userPhone, revieweeId, reviewDto);
        return "Review uploaded.";
    }
}

package tp.farming_springboot.api;


import lombok.AllArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.application.ReviewService;
import tp.farming_springboot.application.dto.request.ReviewCreateRequestDto;
import tp.farming_springboot.domain.exception.UserNotAuthorizedException;


@RestController
@RequestMapping(value="/review")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<?> create(Authentication authentication, @RequestBody ReviewCreateRequestDto reviewCreateRequestDto) throws UserNotAuthorizedException {
        return ApiResponse.success(this.reviewService.create(authentication, reviewCreateRequestDto));
    }
}

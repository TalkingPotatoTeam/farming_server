package tp.farming_springboot.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.application.dto.response.ProductDetailResDto;
import tp.farming_springboot.application.LikeService;
import tp.farming_springboot.application.dto.response.LikeUserResDto;
import tp.farming_springboot.domain.exception.UserAlreadyLikeProductException;
import tp.farming_springboot.domain.exception.UserNotLikeProductException;

import java.nio.charset.Charset;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{productId}")
    public ApiResponse<?> create(Authentication authentication,  @PathVariable Long productId) throws UserAlreadyLikeProductException {
        likeService.create(authentication.getName(), productId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{productId}")
    public ApiResponse<?> delete(Authentication authentication, @PathVariable Long productId) throws UserNotLikeProductException {
        likeService.delete(authentication.getName(), productId);
        return ApiResponse.success();
    }

    @GetMapping("/{productId}")
    public ApiResponse<?> getLikeUserList(@PathVariable Long productId) {
        return ApiResponse.success(likeService.getLikeUserSet(productId));
    }

    @GetMapping("/product/{userId}")
    public ApiResponse<?> getLikelistByUser(@PathVariable Long userId) {
        return ApiResponse.success(likeService.getLikelistByUser(userId));
    }

}

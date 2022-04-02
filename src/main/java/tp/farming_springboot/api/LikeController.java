package tp.farming_springboot.api;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.application.LikeService;
import tp.farming_springboot.domain.exception.UserAlreadyLikeProductException;
import tp.farming_springboot.domain.exception.UserNotLikeProductException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{productId}")
    @ApiOperation(value = "게시물 좋아요 API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse<?> create(Authentication authentication,  @PathVariable Long productId) throws UserAlreadyLikeProductException {
        likeService.create(authentication.getName(), productId);
        return ApiResponse.success();
    }

    @DeleteMapping("/{productId}")
    @ApiOperation(value = "게시물 좋아요 삭제 API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse<?> delete(Authentication authentication, @PathVariable Long productId) throws UserNotLikeProductException {
        likeService.delete(authentication.getName(), productId);
        return ApiResponse.success();
    }

    @GetMapping("/{productId}")
    @ApiOperation(value = "게시물 좋아요 누른 유저 리스트 조회 API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse<?> getLikeUserList(@PathVariable Long productId) {
        return ApiResponse.success(likeService.getLikeUserSet(productId));
    }

    @GetMapping("/product")
    @ApiOperation(value = "로그인 유저가 좋아요 누른 게시물 리스트 조회 API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse<?> getLikeProductByUser(Authentication authentication) {
        return ApiResponse.success(likeService.getLikeProductByUser(authentication.getName()));
    }

}

package tp.farming_springboot.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.domain.product.dto.ProductResponseDto;
import tp.farming_springboot.domain.product.service.LikeService;
import tp.farming_springboot.domain.user.dto.UserResponseDto;
import tp.farming_springboot.exception.UserAlreadyLikeProductException;
import tp.farming_springboot.exception.UserNotLikeProductException;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;
import java.nio.charset.Charset;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {
    private final LikeService likeService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{productId}")
    public String create(Authentication authentication,  @PathVariable Long productId) throws UserAlreadyLikeProductException {
        likeService.create(authentication.getName(), productId);
        return "Like is Added to product {" + productId + "}.";
    }


    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{productId}")
    public String delete(Authentication authentication,  @PathVariable Long productId) throws UserNotLikeProductException {
        likeService.delete(authentication.getName(), productId);
        return "Like is deleted to product {" + productId + "}.";
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{productId}")
    public ResponseEntity<Message> getLikeUserList(@PathVariable Long productId) {
        Set<UserResponseDto> userSet = likeService.getLikeUserSet(productId);
        Message message = new Message(StatusEnum.OK, "", userSet);
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/product/{userId}")
    public ResponseEntity<Message> getLikelistByUser(@PathVariable Long userId) {
        Set<ProductResponseDto> productSet = likeService.getLikelistByUser(userId);
        Message message = new Message(StatusEnum.OK, "", productSet);
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }



    public HttpHeaders HttpHeaderSetting(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return headers;
    }
}

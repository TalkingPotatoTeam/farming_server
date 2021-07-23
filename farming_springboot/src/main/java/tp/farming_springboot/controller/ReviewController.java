package tp.farming_springboot.controller;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.dto.ProductCreateDto;
import tp.farming_springboot.domain.review.dto.ReviewCreateDto;
import tp.farming_springboot.domain.review.repository.ReviewRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;
import tp.farming_springboot.response.Message;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value="/review")
@AllArgsConstructor
public class ReviewController {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @PostMapping
    public ResponseEntity<Message> create(Authentication authentication, @RequestPart ReviewCreateDto reviewDto){
        Message message = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));


        return new ResponseEntity<>(message,headers, HttpStatus.OK);
    }
}

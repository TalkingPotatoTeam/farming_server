package tp.farming_springboot.controller;


import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.dto.ProductCreateDto;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.product.repository.CategoryRepository;
import tp.farming_springboot.domain.product.service.ProductService;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.product.repository.ProductRepository;
import tp.farming_springboot.domain.user.service.UserService;
import tp.farming_springboot.exception.PhotoFileException;
import tp.farming_springboot.exception.UserNotAuthorizedException;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;

import javax.validation.Valid;
import java.nio.charset.Charset;
import java.util.List;

@RestControllerAdvice
@RestController
@RequestMapping(value="/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository prodRepo;
    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final UserService userService;


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String create(
            Authentication authentication, @Valid @RequestPart ProductCreateDto prodDto,
            @RequestPart (value="PhotoFile", required=false) List<MultipartFile> files,
            @RequestPart(value = "ReceiptFile", required = false) MultipartFile receiptFile
            ) throws PhotoFileException {

        String userPhone = authentication.getName();
        productService.create(userPhone, prodDto, files, receiptFile);
        return "Product item uploaded.";
    }

    // prodRepo의 findall return type => *Iterable*
    @GetMapping
    public ResponseEntity<Message> list(Authentication authentication) {
        Iterable<Product> prodList = prodRepo.findAll();
        Message message= new Message(StatusEnum.OK, "finding all of product is success.", prodList);
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findByProductId(Authentication authentication, @PathVariable Long id) {
        Product prod = productService.findById(id);
        Message message = new Message(StatusEnum.OK, "Finding with product id is Success.", prod);
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Message> findByUserId(Authentication authentication, @PathVariable Long id) {
        //id 검증 용도
        User user  = userService.findUserById(id);

        //Null이어도 에러 처리가 없어야 함.
        Iterable<Product> prodList = prodRepo.findByUserId(id);
        Message message = new Message(StatusEnum.OK,"Finding with user id is Success.", prodList );
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);

    }

    @GetMapping("/current-login-user")
    public ResponseEntity<Message> findByLoggedUserId(Authentication authentication) {
        User user = userService.findUserByPhone(authentication.getName());

        //Null이어도 에러 처리가 없어야 함.
        Iterable<Product> prodList = prodRepo.findByUserId(user.getId());
        Message message = new Message(StatusEnum.OK, "Finding by current-user is success.", prodList);

        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }


    // 게시물 id로 수정하기
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String update(
            Authentication authentication,
            @PathVariable Long id,
            @RequestPart ProductCreateDto prodDto,
            @RequestPart(value="PhotoFile", required = false) List<MultipartFile> files,
            @RequestPart(value="ReceiptFile", required = false) MultipartFile ReceiptFile
            ) throws UserNotAuthorizedException, PhotoFileException {

        String userPhone = authentication.getName();
        productService.update(prodDto, userPhone, id, ReceiptFile, files);

        return "Updating product success.";
    }


    //자동으로 사진도 삭제.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String delete(Authentication authentication, @PathVariable Long id) throws UserNotAuthorizedException, PhotoFileException {
        productService.delete(authentication.getName(), id);
        return "Deleting product success.";
    }

    @GetMapping("/categories")
    public ResponseEntity<Message> showCategories(){
        Message message = new Message(StatusEnum.OK, "",categoryRepository.getCategories());
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    public HttpHeaders HttpHeaderSetting(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return headers;
    }


}

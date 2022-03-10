package tp.farming_springboot.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.dto.*;
import tp.farming_springboot.domain.product.repository.CategoryRepository;
import tp.farming_springboot.domain.product.service.ProductService;
import tp.farming_springboot.exception.PhotoFileException;
import tp.farming_springboot.exception.UserNotAuthorizedException;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;

@RestControllerAdvice
@RestController
@RequestMapping(value="/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/home")
    public List<ProductListResDto> home(
            @PageableDefault(size=3, sort="id",direction= Sort.Direction.DESC) Pageable pageRequest){
        List<ProductListResDto> productResponseDto = productService.findProductByPagination(pageRequest);
        return productResponseDto;
    }

    //서치 미완성
    //1. 필터 적용해서 키워드로 검색(필터: 카테고리, 인증 푸드, 거리)
    //2. 필터 없이 키워드로 검색

    // 카테고리 눌렀을 때 해당 카테고리 다 나오게
    @GetMapping("/search")
    public List<ProductListResDto> searchByKeywordWithFilter(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size=3, sort="id",direction= Sort.Direction.DESC) Pageable pageRequest,
            @RequestBody(required = false) ProductFilterDto productFilterDto) {

        if(productFilterDto == null)
            productFilterDto = ProductFilterDto.getDefaultInstance();

        if(keyword == null)
            keyword = "";

        List<ProductListResDto> productResponseDto = productService.searchByKeywordAndFilter(keyword, productFilterDto ,pageRequest);
        return productResponseDto;
    }

    @GetMapping("/search/category")
    public List<ProductListResDto> searchByCategory(
            @RequestParam String category,
            @PageableDefault(size=3, sort="id",direction= Sort.Direction.DESC) Pageable pageRequest) {

        List<ProductListResDto> productResponseDtos = productService.searchByCategory(category, pageRequest);
        return productResponseDtos;
    }


    @PostMapping
    public String create(
            Authentication authentication, @RequestParam("prodDto") String prodStr,
            @RequestParam(value="PhotoFile", required=false) List<MultipartFile> files,
            @RequestParam(value = "ReceiptFile", required = false) MultipartFile receiptFile
    ) throws PhotoFileException, ParseException, IOException, NoSuchAlgorithmException {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductCreateDto prodDto = objectMapper.readValue(prodStr, ProductCreateDto.class);

        productService.create(authentication.getName(), prodDto, files, receiptFile);
        return "Product item uploaded.";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findByProductId(@PathVariable Long id) {
        ProductDetailResDto prod = productService.findById(id);
        Message message = new Message(StatusEnum.OK, "Finding with product id is Success.", prod);
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Message> findByUserId(@PathVariable Long id) {
        List<ProductDetailResDto> prodList = productService.findByUserId(id);
        Message message = new Message(StatusEnum.OK,"Finding with user id is Success.", prodList );
        return new ResponseEntity<>(message, HttpHeaderSetting(), HttpStatus.OK);
    }


    // 게시물 id로 수정하기
    @PutMapping("/{id}")
    public String update(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam("prodDto") String prodStr,
            @RequestParam(value="PhotoFile", required=false) List<MultipartFile> files,
            @RequestParam(value = "ReceiptFile", required = false) MultipartFile receiptFile
            ) throws UserNotAuthorizedException, PhotoFileException, ParseException, IOException, NoSuchAlgorithmException {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductCreateDto prodDto = objectMapper.readValue(prodStr, ProductCreateDto.class);
        productService.update(prodDto, authentication.getName(), id, receiptFile, files);

        return "Updating product success.";
    }


    //자동으로 사진도 삭제.
    @DeleteMapping("/{id}")
    public String delete(Authentication authentication, @PathVariable Long id) throws UserNotAuthorizedException {
        productService.delete(authentication.getName(), id);
        return "Deleting product success.";
    }

    @PutMapping(value="/status/{productId}")
    public String changeStatusOfProduct(Authentication authentication, @PathVariable Long productId,
                                        @RequestBody ProductStatusDto productStatus) throws UserNotAuthorizedException {

        productService.changeStatusOfProduct(authentication.getName(), productId, productStatus);
        return "Updating Status Of Product is Success.";
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

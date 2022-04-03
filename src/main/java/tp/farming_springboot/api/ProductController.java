package tp.farming_springboot.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.api.annotation.ApiPageable;
import tp.farming_springboot.application.dto.request.ProductCreateDto;
import tp.farming_springboot.application.dto.request.ProductFilterDto;
import tp.farming_springboot.application.dto.request.ProductStatusDto;
import tp.farming_springboot.domain.repository.CategoryRepository;
import tp.farming_springboot.application.ProductService;
import tp.farming_springboot.domain.exception.PhotoFileException;
import tp.farming_springboot.domain.exception.UserNotAuthorizedException;

import java.io.IOException;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/home")
    @ApiOperation(value = "메인 홈 게시물 조회 API", authorizations = {@Authorization(value = "jwt")})
    @ApiPageable
    public ApiResponse<?> home(@PageableDefault(size=3, sort="id",direction= Sort.Direction.DESC) Pageable pageRequest){
        return ApiResponse.success(productService.findProductByPagination(pageRequest));
    }

    @GetMapping("/search")
    @ApiOperation(value = "게시물 검색 API", authorizations = {@Authorization(value = "jwt")})
    @ApiPageable
    public ApiResponse<?> searchByKeywordWithFilter(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @PageableDefault(size=3, sort="id", direction= Sort.Direction.DESC) Pageable pageRequest,
            @RequestBody(required = false) ProductFilterDto productFilterDto) {

        return ApiResponse.success(productService.searchByKeywordAndFilter(keyword, productFilterDto ,pageRequest));
    }

    @GetMapping("/search/category")
    @ApiOperation(value = "카테고리로 게시물 검색 API", authorizations = {@Authorization(value = "jwt")})
    @ApiPageable
    public ApiResponse<?> searchByCategory(
            @RequestParam String category,
            @PageableDefault(size=3, sort="id",direction= Sort.Direction.DESC) Pageable pageRequest) {

        return ApiResponse.success(productService.searchByCategory(category, pageRequest));
    }


    @PostMapping("/receipt/{productId}")
    @ApiOperation(value = "구매 인증 (영수증 등록) API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse registerReceipt(@PathVariable Long productId,
                                       @RequestParam(value = "file") MultipartFile file) throws IOException, NoSuchAlgorithmException {

        productService.registerReceipt(productId, file);
        return ApiResponse.success();
    }


    @PostMapping
    @ApiOperation(value = "게시물 생성 API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse create(Authentication authentication,
                              @RequestParam("prodDto") String prodStr,
                              @RequestParam(value="PhotoFile", required=false) List<MultipartFile> files,
                              @RequestParam(value = "ReceiptFile", required = false) MultipartFile receiptFile) throws PhotoFileException, ParseException, IOException, NoSuchAlgorithmException {


        ProductCreateDto prodDto = objectMapper.readValue(prodStr, ProductCreateDto.class);
        productService.create(authentication.getName(), prodDto, files, receiptFile);
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "게시물 ID로 조회 API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse<?> findByProductId(@PathVariable Long id) {
        return ApiResponse.success(productService.findById(id));
    }

    @GetMapping("/user/{id}")
    @ApiOperation(value = "유저 ID로 게시물 조회 API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse<?> findByUserId(@PathVariable Long id) {
        return ApiResponse.success(productService.findByUserId(id));
    }


    // 게시물 id로 수정하기
    @PutMapping("/{id}")
    @ApiOperation(value = "게시물 ID로 수정 API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse<?> update(Authentication authentication,
                                 @PathVariable Long id,
                                 @RequestParam("prodDto") String prodStr,
                                 @RequestParam(value="PhotoFile", required=false) List<MultipartFile> files,
                                 @RequestParam(value = "ReceiptFile", required = false) MultipartFile receiptFile) throws UserNotAuthorizedException, PhotoFileException, ParseException, IOException, NoSuchAlgorithmException {

        ProductCreateDto prodDto = objectMapper.readValue(prodStr, ProductCreateDto.class);
        productService.update(prodDto, authentication.getName(), id, receiptFile, files);

        return ApiResponse.success();
    }


    //자동으로 사진도 삭제.
    @DeleteMapping("/{id}")
    @ApiOperation(value = "게시물 삭제 API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse<?> delete(Authentication authentication, @PathVariable Long id) throws UserNotAuthorizedException {
        productService.delete(authentication.getName(), id);
        return ApiResponse.success();
    }

    @PutMapping(value="/status/{productId}")
    @ApiOperation(value = "게시물 판매 상태 변경 API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse<?> changeStatusOfProduct(Authentication authentication, @PathVariable Long productId,
                                                @RequestBody ProductStatusDto productStatus) throws UserNotAuthorizedException {

        productService.changeStatusOfProduct(authentication.getName(), productId, productStatus);
        return ApiResponse.success();
    }

    @GetMapping("/categories")
    @ApiOperation(value = "전체 게시물 카테고리 목록 조회 API", authorizations = {@Authorization(value = "jwt")})
    public ApiResponse showCategories(){
        return ApiResponse.success(categoryRepository.getCategories());
    }


}

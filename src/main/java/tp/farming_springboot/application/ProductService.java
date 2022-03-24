package tp.farming_springboot.application;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.application.dto.request.ProductCreateDto;
import tp.farming_springboot.application.dto.request.ProductFilterDto;
import tp.farming_springboot.application.dto.request.ProductStatusDto;
import tp.farming_springboot.application.dto.response.ProductDetailResDto;
import tp.farming_springboot.application.dto.response.ProductListResDto;
import tp.farming_springboot.domain.dao.Category;
import tp.farming_springboot.domain.dao.PhotoFile;
import tp.farming_springboot.domain.dao.Product;
import tp.farming_springboot.domain.repository.CategoryRepository;
import tp.farming_springboot.domain.repository.ProductRepository;
import tp.farming_springboot.infra.S3UploaderService;
import tp.farming_springboot.domain.dao.User;
import tp.farming_springboot.domain.repository.UserRepository;
import tp.farming_springboot.domain.exception.PhotoFileException;
import tp.farming_springboot.domain.exception.UserNotAuthorizedException;


import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final FileService fileService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final S3UploaderService s3UploaderService;

    /*
     * Pagination이 적용된 게시물 조회 로직입니다.
     */
    @Transactional(readOnly = true)
    public List<ProductListResDto> findProductByPagination(Pageable pageRequest) {
        Page<Product> productList = productRepository.findAll(pageRequest);

        List<ProductListResDto> productResponseDtos = productList.stream().map(product -> ProductListResDto.from(product)).collect(Collectors.toList());

        return productResponseDtos;
    }

    @Transactional(readOnly = true)
    public List<ProductListResDto> searchByKeywordAndFilter(String keyword, ProductFilterDto productFilterDto, Pageable pageRequest){
        List<Category> categoryList = categoryRepository.findByNameIn(productFilterDto.getCategoryNameList());
        Page<Product> productList = productRepository.findByKeywordInCategoryList(keyword, categoryList, pageRequest);
        return productList.stream().map(product -> ProductListResDto.from(product)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductListResDto> searchByCategory(String categoryName, Pageable pageRequest) {
        Category category = categoryRepository.findByNameOrElseThrow(categoryName);
        Page<Product> productList = productRepository.findByCategory(category, pageRequest);

        List<ProductListResDto> productResponseDtos = productList.stream().map(
                product -> ProductListResDto.from(product)
        ).collect(Collectors.toList());

        return productResponseDtos;
    }


    /*
     * Pagination이 적용되지 않은 게시물 조회 로직입니다.
     */
    @Transactional(readOnly = true)
    public List<ProductDetailResDto> findByUserId(Long userId) {
        List<Product> productList = productRepository.findByUserId(userId);

        return productList.stream().map(
                product -> ProductDetailResDto.from(product)
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDetailResDto findById(Long id) {
        Product product = productRepository.findByIdOrElseThrow(id);
        return ProductDetailResDto.from(product);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void create(String userPhone, ProductCreateDto prodDto, List<MultipartFile> photoFiles, MultipartFile receiptFile) throws PhotoFileException, ParseException, IOException, NoSuchAlgorithmException {
        User user = userRepository.findByPhoneElseThrow(userPhone);
        Category category = categoryRepository.findByNameOrElseThrow(prodDto.getCategoryName());

        Product product = Product.of(
                user,
                prodDto.getTitle(),
                prodDto.getContent(),
                prodDto.getPrice(),
                user.getCurrent().getContent(),
                prodDto.isCertified(),
                category,
                prodDto.getReceipt(),
                new SimpleDateFormat("yyyy.MM.dd").parse(prodDto.getBuyProductDate()),
                prodDto.getFreshness()
        );

        if(receiptFile != null){
            PhotoFile receipt = fileService.photoFileCreate(receiptFile);
            product.addReceiptAndCertified(receipt);
        }

        if(photoFiles != null) {
            fileService.photoFileListCreate(photoFiles, product);
        }

        productRepository.save(product);
    }


    @Transactional(rollbackFor = {Exception.class})
    public void delete(String userPhone, Long id) throws UserNotAuthorizedException {
        User user = userRepository.findByPhoneElseThrow(userPhone);
        Product product = productRepository.findByIdOrElseThrow(id);

        if(!isUserAuthor(user, product)) {
            throw new UserNotAuthorizedException("Current user and product author is not same.");
        }else {
            product.getPhotoFile().forEach(f -> s3UploaderService.deleteS3(f.getHashFilename()));

            if(product.getReceipt() != null)
                s3UploaderService.deleteS3(product.getReceipt().getHashFilename());

            //fileService.clearFileFromProduct(product);

            productRepository.deleteById(id);
        }
    }


    @Transactional(rollbackFor = {Exception.class})
    public void update(ProductCreateDto prodDto,
                       String userPhone, Long id,
                       MultipartFile ReceiptFile,
                       List<MultipartFile> photoFiles) throws UserNotAuthorizedException, PhotoFileException, ParseException, IOException, NoSuchAlgorithmException {

        User user = userRepository.findByPhoneElseThrow(userPhone);
        Product prod = productRepository.findByIdOrElseThrow(id);

        if(!isUserAuthor(user, prod))
            throw new UserNotAuthorizedException("Current user and product author is not same.");
        else {
            fileService.clearFileFromProduct(prod);

            if (ReceiptFile != null) {
                PhotoFile receipt = fileService.photoFileCreate(ReceiptFile);
                prod.addReceiptAndCertified(receipt);
            }

            if (photoFiles != null) {
                List<PhotoFile> photoFileList = fileService.photoFileListCreate(photoFiles, prod);
                prodDto.setPhotoFile(photoFileList);
            }

            Category category = categoryRepository.findByNameOrElseThrow(prodDto.getCategoryName());
            prod.update(
                    prodDto.getTitle(),
                    prodDto.getContent(),
                    prodDto.getPrice(),
                    prodDto.isCertified(),
                    category,
                    prodDto.getReceipt(),
                    prodDto.getPhotoFile(),
                    new SimpleDateFormat("yyyy.MM.dd").parse(prodDto.getBuyProductDate()),
                    prodDto.getFreshness()
            );

            productRepository.save(prod);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void changeStatusOfProduct(String userPhone, Long productId, ProductStatusDto productStatus) throws UserNotAuthorizedException {
        User user = userRepository.findByPhoneElseThrow(userPhone);
        Product product = productRepository.findByIdOrElseThrow(productId);

        if(!isUserAuthor(user, product)) {
            throw new UserNotAuthorizedException("Current user and product author is not same.");
        } else {
            List<String> productStatusList = new ArrayList<>();
            productStatusList.add("판매중");
            productStatusList.add("예약중");
            productStatusList.add("판매완료");

            if(!productStatusList.contains(productStatus.getProductStatus())) {
                System.out.println(" = " + productStatus);
                throw new IllegalArgumentException("Product Status is not existed.");
            } else {
                product.setProductStatus(productStatus.getProductStatus());
                productRepository.save(product);
            }
        }
    }

    public boolean isUserAuthor(User user, Product product) {
        if(user.getId() == product.getUser().getId())
            return true;
        else
            return false;
    }


}

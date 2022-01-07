package tp.farming_springboot.domain.product.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.dto.ProductCreateDto;
import tp.farming_springboot.domain.product.dto.ProductFilterDto;
import tp.farming_springboot.domain.product.dto.ProductResponseDto;
import tp.farming_springboot.domain.product.dto.ProductStatusDto;
import tp.farming_springboot.domain.product.model.Category;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.product.repository.CategoryRepository;
import tp.farming_springboot.domain.product.repository.FileRepository;
import tp.farming_springboot.domain.product.repository.ProductRepository;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.service.UserService;
import tp.farming_springboot.exception.PhotoFileException;
import tp.farming_springboot.exception.UserNotAuthorizedException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;


    public List<ProductResponseDto> findProductByPagination(Pageable pageRequest) {
        Page<Product> productList = productRepository.findAll(pageRequest);

        List<ProductResponseDto> productResponseDtos = productList.stream().map(
                product -> ProductResponseDto.from(product)
        ).collect(Collectors.toList());

        return productResponseDtos;
    }

    public List<ProductResponseDto> searchByKeywordAndFilter(String keyword, ProductFilterDto productFilterDto, Pageable pageRequest){
        List<Category> categoryList = categoryRepository.findByNameIn(productFilterDto.getCategoryNameList());
        Page<Product> productList = productRepository.findByKeywordInCategoryList(keyword, categoryList, pageRequest);
        return productList.stream().map(product -> ProductResponseDto.from(product)).collect(Collectors.toList());
    }

    public List<ProductResponseDto> searchByCategory(String categoryName, Pageable pageRequest) {
        Category category = categoryRepository.findByNameOrElseThrow(categoryName);
        Page<Product> productList = productRepository.findByCategory(category, pageRequest);

        List<ProductResponseDto> productResponseDtos = productList.stream().map(
                product -> ProductResponseDto.from(product)
        ).collect(Collectors.toList());

        return productResponseDtos;
    }

    public List<ProductResponseDto> findByUserId(Long userId) {
        List<Product> productList = productRepository.findByUserId(userId);

        return productList.stream().map(
                product -> ProductResponseDto.from(product)
        ).collect(Collectors.toList());
    }

    public ProductResponseDto findById(Long id) {
        Product product = productRepository.findByIdOrElseThrow(id);
        return ProductResponseDto.from(product);
    }

    @Transactional(rollbackOn = {Exception.class})
    public void create(String userPhone, ProductCreateDto prodDto, List<MultipartFile> photoFiles, MultipartFile receiptFile) throws PhotoFileException, ParseException, IOException {
        User user = userService.findUserByPhone(userPhone);
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
            product.addReceiptFile(receipt);
        }

        if(photoFiles != null) {
            fileService.photoFileListCreate(photoFiles, product);
        }

        productRepository.save(product);
    }


    @Transactional(rollbackOn = {Exception.class})
    public void delete(String userPhone, Long id) throws UserNotAuthorizedException {
        User user = userService.findUserByPhone(userPhone);
        Product product = productRepository.findByIdOrElseThrow(id);

        if(!isUserAuthor(user, product)) {
            throw new UserNotAuthorizedException("Current user and product author is not same.");
        }else {
            productRepository.deleteById(id);
        }
    }


    @Transactional(rollbackOn = {Exception.class})
    public void update(ProductCreateDto prodDto,
                       String userPhone, Long id,
                       MultipartFile ReceiptFile,
                       List<MultipartFile> photoFiles) throws UserNotAuthorizedException, PhotoFileException, ParseException, IOException {

        User user = userService.findUserByPhone(userPhone);
        Product prod = productRepository.findByIdOrElseThrow(id);

        if(!isUserAuthor(user, prod))
            throw new UserNotAuthorizedException("Current user and product author is not same.");
        else {
            clearFileFromProduct(prod);

            if (ReceiptFile != null) {
                PhotoFile receipt = fileService.photoFileCreate(ReceiptFile);
                prod.addReceiptFile(receipt);
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

    private void clearFileFromProduct(Product product) {
        if (product.getPhotoFile().size() > 0)
            fileRepository.deleteRelatedProductId(product.getId());


        if (product.getReceipt() != null) {
            fileRepository.deleteById(product.getReceipt().getId());
        }

    }


    public void changeStatusOfProduct(String userPhone, Long productId, ProductStatusDto productStatus) throws UserNotAuthorizedException {
        User user = userService.findUserByPhone(userPhone);
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

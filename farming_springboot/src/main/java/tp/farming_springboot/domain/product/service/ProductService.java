package tp.farming_springboot.domain.product.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.dto.ProductCreateDto;
import tp.farming_springboot.domain.product.dto.ProductResponseDto;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.product.repository.CategoryRepository;
import tp.farming_springboot.domain.product.repository.FileRepository;
import tp.farming_springboot.domain.product.repository.ProductRepository;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.service.UserService;
import tp.farming_springboot.exception.PhotoFileException;
import tp.farming_springboot.exception.RestNullPointerException;
import tp.farming_springboot.exception.UserNotAuthorizedException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;


    public ProductResponseDto findById(Long id) {
        Product product = productRepository.findByIdOrElseThrow(id);
        return ProductResponseDto.from(product);
    }

    public void create(String userPhone, ProductCreateDto prodDto, List<MultipartFile> photoFiles, MultipartFile receiptFile) throws PhotoFileException {
        User user = userService.findUserByPhone(userPhone);
        List<PhotoFile> photoFileList = new ArrayList<PhotoFile>();

        if(receiptFile != null){
            PhotoFile receipt = fileService.photoFileCreate(receiptFile);
            prodDto.setReceipt(receipt);
            prodDto.setCertified(true);
        } else {
            prodDto.setCertified(false);
        }

        if(photoFiles != null) {
            photoFileList = fileService.photoFileListCreate(photoFiles);
        }
        prodDto.setUser(user);
        prodDto.setPhotoFile(photoFileList);
        prodDto.setCategory(categoryRepository.findByNameOrElseThrow(prodDto.getCategoryName()));

        Product product = Product.of(
                prodDto.getUser(),
                prodDto.getTitle(),
                prodDto.getContent(),
                prodDto.getPrice(),
                prodDto.getUser().getCurrent().getContent(),
                prodDto.isCertified(),
                prodDto.getQuantity(),
                prodDto.getCategory(),
                prodDto.getReceipt(),
                prodDto.getPhotoFile(),
                prodDto.getBuyProductDate(),
                prodDto.getFreshness()
        );

        productRepository.save(product);
    }



    @Transactional
    public void delete(String userPhone, Long id) throws UserNotAuthorizedException, PhotoFileException {
        User user = userService.findUserByPhone(userPhone);
        Product product = productRepository.findByIdOrElseThrow(id);

        if(!isUserAuthor(user, product)) {
            throw new UserNotAuthorizedException("Current user and product author is not same.");
        }else {
            if(product.getPhotoFile().size() != 0) {
                fileService.deleteFiles(product.getPhotoFile());
            }
            if(product.getReceipt() != null) {
                List<PhotoFile> tempReceiptList = new ArrayList<PhotoFile>();
                tempReceiptList.add(product.getReceipt());
                fileService.deleteFiles(tempReceiptList);
            }
            productRepository.deleteById(id);
        }
    }


    @Transactional
    public void update(ProductCreateDto prodDto,
                       String userPhone, Long id,
                       MultipartFile ReceiptFile,
                       List<MultipartFile> photoFiles) throws UserNotAuthorizedException, PhotoFileException {

        User user = userService.findUserByPhone(userPhone);
        Product prod = productRepository.findByIdOrElseThrow(id);

        if(!isUserAuthor(user, prod))
            throw new UserNotAuthorizedException("Current user and product author is not same.");
        else {
            // 사진 파일 삭제
            if (prod.getPhotoFile().size() > 0) {
                fileService.deleteFiles(prod.getPhotoFile());
                fileRepository.deleteRelatedProductId(prod.getId());
            }

            // 영수증 파일 삭제
            if (prod.getReceipt() != null) {
                List<PhotoFile> tempReceiptList = new ArrayList<PhotoFile>();
                tempReceiptList.add(prod.getReceipt());

                // deleteFiles 메소드에서 인자를 리스트 형태만 받기 때문에, 재활용을 위한 리스트화
                fileService.deleteFiles(tempReceiptList);

                //orphan removal 설정으로 참조하지 않으면 필드를 자동으로 삭제해줌 => repo를 통한 delete 과정 없어도됨
                prodDto.setReceipt(null);
            }

            if (ReceiptFile != null) {
                PhotoFile receiptPhoto = fileService.photoFileCreate(ReceiptFile);
                prodDto.setReceipt(receiptPhoto);
                prodDto.setCertified(true);

            } else {
                prodDto.setCertified(false);
            }

            if (photoFiles != null) {
                List<PhotoFile> photoFileList = fileService.photoFileListCreate(photoFiles);
                prodDto.setPhotoFile(photoFileList);
            }

            prodDto.setCategory(categoryRepository.findByNameOrElseThrow(prodDto.getCategoryName()));
            prod.update(
                    prodDto.getTitle(),
                    prodDto.getContent(),
                    prodDto.getPrice(),
                    prodDto.isCertified(),
                    prodDto.getQuantity(),
                    prodDto.getCategory(),
                    prodDto.getReceipt(),
                    prodDto.getPhotoFile(),
                    prodDto.getBuyProductDate(),
                    prodDto.getFreshness()
            );

            productRepository.save(prod);
        }
    }

    public boolean isUserAuthor(User user, Product product) {
        if(user.getId() == product.getUser().getId())
            return true;
        else
            return false;
    }


}

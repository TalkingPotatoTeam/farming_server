package tp.farming_springboot.controller;


import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.dto.PhotoFileDto;
import tp.farming_springboot.domain.product.dto.ProductCreateDto;
import tp.farming_springboot.domain.product.model.Category;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.product.repository.CategoryRepository;
import tp.farming_springboot.domain.product.repository.FileRepository;
import tp.farming_springboot.domain.product.service.FileService;
import tp.farming_springboot.domain.product.util.MD5Generator;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.product.repository.ProductRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;
import tp.farming_springboot.domain.user.service.UserService;
import tp.farming_springboot.exception.RestNullPointerException;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;

import java.nio.file.Files;
import java.nio.file.Path;

import java.io.File;
import java.nio.charset.Charset;

import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@RestController
@RequestMapping(value="/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository prodRepo;
    private final FileRepository photoFileRepo;
    private final FileService fileService;
    private final CategoryRepository categoryRepository;
    private final UserService userService;


    @ExceptionHandler
    public ResponseEntity<Message> handler(RestNullPointerException e) {
        Message message = e.getMsg();
        HttpHeaders headers = e.getHeaders();
        HttpStatus httpStatus = e.getHttpStatus();

        return new ResponseEntity<>(message, headers, httpStatus);
    }

    public HttpHeaders HttpHeaderSetting(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        return headers;
    }

    @PostMapping
    @ResponseBody
    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Message> create(
            Authentication authentication, @RequestPart ProductCreateDto prodDto,
            @RequestPart (value="PhotoFile", required=false) List<MultipartFile> files,
            @RequestPart(value = "ReceiptFile", required = false) MultipartFile receiptFile
            ) {
        User user = userService.findUserByPhone(authentication.getName());
        HttpHeaders headers = HttpHeaderSetting();
        Message message = new Message();

        if(receiptFile != null){
            PhotoFile receipt = fileService.photoFileCreate(receiptFile);
            if(receipt == null) {
                System.out.println("null pointer");
            }
            prodDto.setReceipt(receipt);
            prodDto.setCertified(true);
        } else {
            prodDto.setCertified(false);
        }

        List<PhotoFile> photoFileList = new ArrayList<PhotoFile>();
        if(files != null) {
            photoFileList = fileService.photoFileListCreate(files);
            if(photoFileList == null) {
                message = new Message(StatusEnum.INTERNAL_SERVER_ERROR, "File reading error.");
                return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        prodDto.setPhotoFile(photoFileList);
        prodDto.setAddress(user.getCurrent().getContent());
        Product prod = prodRepo.save(new Product(user, prodDto, categoryRepository));

        message = new Message(StatusEnum.OK, "Product item uploaded.", prod);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    // prodRepo의 findall return type => *Iterable*
    @GetMapping
    public ResponseEntity<Message> list(Authentication authentication) {
        Message message = null;
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Iterable<Product> prodList = prodRepo.findAll();

        message = new Message(StatusEnum.OK, "finding all of product is success.", prodList);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findByProductId(Authentication authentication, @PathVariable Long id) {
        Message message = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json",Charset.forName("UTF-8")));

        Optional<Product> prod = prodRepo.findById(id);
        prod.orElseThrow(()->  new RestNullPointerException(headers, "Finding with id product failed.", HttpStatus.NOT_FOUND, StatusEnum.NOT_FOUND));

        message = new Message(StatusEnum.OK, "Finding with product id is Success.", prod);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);

    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Message> findByUserId(Authentication authentication, @PathVariable Long id) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json",Charset.forName("UTF-8")));

        User product_author  = userService.findUserById(id);

        Iterable<Product> prod = prodRepo.findByUserId(id);
        message.setMessage("Finding with user id is Success.");
        message.setStatus(StatusEnum.OK);
        message.setData(prod);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);

    }

    @GetMapping("/current-login-user")
    public ResponseEntity<Message> findByLoggedUserId(Authentication authentication) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json",Charset.forName("UTF-8")));
        User current_user = userService.findUserByPhone(authentication.getName());

        Iterable<Product> prodList = prodRepo.findByUserId(current_user.getId());
        message.setStatus(StatusEnum.OK);
        message.setMessage("Finding by current-user is success.");
        message.setData(prodList);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);

    }

    private void deleteFiles(List<PhotoFile> photoList) {
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        if(photoList == null || photoList.size() == 0){
            return;
        }

        for (PhotoFile pf : photoList) {
            try {
                Path filePath = Paths.get(pf.getFilePath());
                Files.deleteIfExists(filePath);
            }
            catch(Exception e){
                System.out.println("error");
            }
        }

    }

    // 게시물 id로 수정하기
    @PutMapping("/{id}")
    public ResponseEntity<Message> update(
            Authentication authentication,
            @PathVariable Long id,
            @RequestPart ProductCreateDto prodDto,
            @RequestPart(value="PhotoFile", required = false) List<MultipartFile> files,
            @RequestPart(value="ReceiptFile", required = false) MultipartFile ReceiptFile
            ) {

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        User user = userService.findUserByPhone(authentication.getName());

        Optional<Product> prod = prodRepo.findById(id);
        prod.orElseThrow(()-> new RestNullPointerException(headers, "Can't Find Product by Id", HttpStatus.NOT_FOUND, StatusEnum.NOT_FOUND));

        if(prod.get().getPhotoFile().size() > 0) {
            deleteFiles(prod.get().getPhotoFile());
        }

        if(prod.get().getReceipt() != null) {
            List<PhotoFile> tempReceiptList = new ArrayList<PhotoFile>();
            tempReceiptList.add(prod.get().getReceipt());
            deleteFiles(tempReceiptList);
        }
        prod.get().setReceipt(null);
        photoFileRepo.deleteRelatedProductId(id);

        try {
            List<PhotoFile> photoFileList = null;
            PhotoFile receiptPhoto = null;
            if(prod.get().getUser().getId() != user.getId()) {
                message.setStatus(StatusEnum.UNAUTHORIZED);
                message.setMessage("You can't add photo, you must author of this product.");
                return new ResponseEntity<>(message, headers, HttpStatus.UNAUTHORIZED);
            }
            if(ReceiptFile != null) {
                String origFilename = ReceiptFile.getOriginalFilename();
                String filename = new MD5Generator(origFilename + LocalDateTime.now()).toString(); // file name 암호화
                String savePath = System.getProperty("user.dir") + "/receipt_photo_files";

                if (!new File(savePath).exists()) {
                    try {
                        new File(savePath).mkdir();
                    } catch (Exception e) {
                        message.setStatus(StatusEnum.INTERNAL_SERVER_ERROR);
                        message.setMessage("Making File-dir failed.");
                        return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                String filePath = savePath + "/" + filename;
                ReceiptFile.transferTo(new File(filePath));

                PhotoFileDto fileDto = new PhotoFileDto(origFilename, filename, filePath);
                receiptPhoto = fileService.saveFile(fileDto);
                prod.get().setCertified(true);
            }
            else {
                prod.get().setCertified(false);
            }
            if(files != null) {
                for (MultipartFile file : files) {
                    String origFilename = file.getOriginalFilename();
                    String filename = new MD5Generator(origFilename + LocalDateTime.now()).toString(); // file name 암호화
                    String savePath = System.getProperty("user.dir") + "/photo_files";

                    if (!new File(savePath).exists()) {
                        try {
                            new File(savePath).mkdir();
                        } catch (Exception e) {
                            message.setStatus(StatusEnum.INTERNAL_SERVER_ERROR);
                            message.setMessage("Making File-dir failed.");
                            return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                    String filePath = savePath + "/" + filename;
                    file.transferTo(new File(filePath));

                    PhotoFileDto fileDto = new PhotoFileDto(origFilename, filename, filePath);

                    if (photoFileList == null) {
                        photoFileList = new ArrayList<PhotoFile>();
                    }
                   // photoFileList.add(fileService.saveFile(fileDto));
                }
                if (prodDto.getTitle() != prod.get().getTitle())
                    prod.get().setTitle(prodDto.getTitle());

                if (prodDto.getContent() != prod.get().getContent())
                    prod.get().setContent(prodDto.getContent());

                if (prodDto.getPrice() != prod.get().getPrice())
                    prod.get().setPrice(prodDto.getPrice());

                //주소를 수정해도될까?
                if (prodDto.getAddress() != prod.get().getAddress())
                    prod.get().setAddress(prodDto.getAddress());

                if (prodDto.getQuantity() != prod.get().getQuantity())
                    prod.get().setQuantity(prodDto.getQuantity());

                prod.get().setReceipt(receiptPhoto);
                prod.get().addPhoto(photoFileList);
                prodRepo.save(prod.get());
            }

            message.setStatus(StatusEnum.OK);
            message.setData(prod);
            message.setMessage("Product updated.");
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        } catch(Exception e) {
            message.setStatus(StatusEnum.INTERNAL_SERVER_ERROR);
            message.setMessage("Photo uploading failed.");
            return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
    //자동으로 사진도 삭제.
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> delete(Authentication authentication, @PathVariable Long id) {
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        User user = userService.findUserByPhone(authentication.getName());

        Optional<Product> prod = prodRepo.findById(id);
        prod.orElseThrow(()-> new RestNullPointerException(headers, "Can't Find Product by Id", HttpStatus.NOT_FOUND, StatusEnum.NOT_FOUND));


        if(prod.get().getUser().getId() == user.getId()) {
            if(prod.get().getPhotoFile().size() != 0) {
                deleteFiles(prod.get().getPhotoFile());
            }
            if(prod.get().getReceipt() != null) {
                List<PhotoFile> tempReceiptList = new ArrayList<PhotoFile>();
                tempReceiptList.add(prod.get().getReceipt());
                deleteFiles(tempReceiptList);
            }
            prodRepo.deleteById(id);
            message.setStatus(StatusEnum.OK);
            message.setMessage("Product deleted.");
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        else {
            message.setStatus(StatusEnum.UNAUTHORIZED);
            message.setMessage("Can't delete Product, You must author of this product");
            return new ResponseEntity<>(message, headers, HttpStatus.UNAUTHORIZED);
        }


    }

    @GetMapping("/categories")
    public ResponseEntity<Message> showCategories(){
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        Message message = new Message(StatusEnum.OK, "",categoryRepository.getCategories());
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }


}

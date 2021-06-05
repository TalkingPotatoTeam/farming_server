package tp.farming_springboot.controller;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tp.farming_springboot.domain.product.dto.PhotoFileDto;
import tp.farming_springboot.domain.product.dto.ProductCreateDto;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.product.repository.FileRepository;
import tp.farming_springboot.domain.product.service.FileService;
import tp.farming_springboot.domain.product.util.MD5Generator;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.product.repository.ProductRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;
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

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

@RestController
@RequestMapping(value="/product")
public class ProductController {
    private final ProductRepository prodRepo;
    private final UserRepository userRepo;
    private final FileRepository photoFileRepo;
    private final FileService fileService;


    @Autowired
    public ProductController(ProductRepository prodRepo, UserRepository userRepository, FileRepository photoFileRepository, FileService fileService) {
        this.prodRepo = prodRepo;
        this.userRepo = userRepository;
        this.photoFileRepo = photoFileRepository;
        this.fileService = fileService;
    }

    @ExceptionHandler
    public ResponseEntity<Message> handler(RestNullPointerException e) {
        Message message = e.getMsg();
        HttpHeaders headers = e.getHeaders();
        HttpStatus httpStatus = e.getHttpStatus();

        return new ResponseEntity<>(message, headers, httpStatus);
    }

    @PostMapping
    @ResponseBody
    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Message> create(
            Principal principal, @RequestPart ProductCreateDto prodDto,
            @RequestPart (value="PhotoFile", required=false) List<MultipartFile> files,
            @RequestPart(value = "ReceiptFile", required = false) MultipartFile receiptFile
            ) {

        Optional<User> user = null;
        List<PhotoFile> photoFileList = null; // isEmpty()로 확인할 수 있게 수정
        PhotoFile receipt = null;
        Message message = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        try {
            user = userRepo.findByPhone(principal.getName());
            user.orElseThrow(() -> new RestNullPointerException(headers, "User Token invalid.", HttpStatus.UNAUTHORIZED, null, StatusEnum.UNAUTHORIZED));

            if(receiptFile != null){
                String origFilename = receiptFile.getOriginalFilename();
                String filename = new MD5Generator(origFilename + LocalDateTime.now()).toString(); // file name 암호화
                String savePath = System.getProperty("user.dir") + "/receipt_photo_files";

                if (!new File(savePath).exists()) {
                    try {
                        new File(savePath).mkdir();
                    } catch (Exception e) {
                        message = new Message(StatusEnum.INTERNAL_SERVER_ERROR, "Making File-dir failed.");
                        return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                String filePath = savePath + "/" + filename;
                receiptFile.transferTo(new File(filePath));

                PhotoFileDto fileDto = new PhotoFileDto();
                fileDto.setOrigFilename(origFilename);
                fileDto.setFilename(filename);
                fileDto.setFilePath(filePath);

                receipt = fileService.saveFile(fileDto);
                prodDto.setReceipt(receipt);
                prodDto.setCertified(true);
            } else {
                prodDto.setCertified(false);
            }


            if(files == null) {
                photoFileList = new ArrayList<PhotoFile>();

            } else {
                for (MultipartFile file : files) {

                    String origFilename = file.getOriginalFilename();
                    String filename = new MD5Generator(origFilename + LocalDateTime.now()).toString(); // file name 암호화
                    String savePath = System.getProperty("user.dir") + "/photo_files";

                    if (!new File(savePath).exists()) {
                        try {
                            new File(savePath).mkdir();
                        } catch (Exception e) {
                            message = new Message(StatusEnum.INTERNAL_SERVER_ERROR, "Making File-dir failed.");
                            return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                        }
                    }
                    String filePath = savePath + "/" + filename;
                    file.transferTo(new File(filePath));

                    PhotoFileDto fileDto = new PhotoFileDto();
                    fileDto.setOrigFilename(origFilename);
                    fileDto.setFilename(filename);
                    fileDto.setFilePath(filePath);

                    if (photoFileList == null)
                        photoFileList = new ArrayList<PhotoFile>();

                    photoFileList.add(fileService.saveFile(fileDto));

                }
            }
            prodDto.setPhotoFile(photoFileList);

        } catch(Exception e) {
            message = new Message(StatusEnum.INTERNAL_SERVER_ERROR, "File reading error.");
            return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        prodDto.setAddress(user.get().getCurrent().getContent());
        Product prod = prodRepo.save(new Product(user, prodDto));

        message = new Message(StatusEnum.OK, "Product item uploaded.", prod);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    // prodRepo의 findall return type => *Iterable*
    @GetMapping
    public ResponseEntity<Message> list(Principal principal) {
        Message message = null;
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        Optional<User> user = userRepo.findByPhone(principal.getName());
        user.orElseThrow(() -> new RestNullPointerException(headers, "User Token invalid.", HttpStatus.UNAUTHORIZED, StatusEnum.UNAUTHORIZED));

        Iterable<Product> prodList = prodRepo.findAll();

        message = new Message(StatusEnum.OK, "finding all of product is success.", prodList);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findByProductId(Principal principal, @PathVariable Long id) {
        Message message = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json",Charset.forName("UTF-8")));
        Optional<User> user = null;

        user = userRepo.findByPhone(principal.getName());
        user.orElseThrow(() -> new RestNullPointerException(headers, "User Token invalid.", HttpStatus.UNAUTHORIZED, StatusEnum.UNAUTHORIZED));

        Optional<Product> prod = prodRepo.findById(id);
        prod.orElseThrow(()->  new RestNullPointerException(headers, "Finding with id product failed.", HttpStatus.NOT_FOUND, StatusEnum.NOT_FOUND));

        message = new Message(StatusEnum.OK, "Finding with product id is Success.", prod);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);

    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Message> findByUserId(Principal principal, @PathVariable Long id) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json",Charset.forName("UTF-8")));
        // 게시글이 없는 경우 빈 데이터.

        Optional<User> current_user = userRepo.findByPhone(principal.getName());
        current_user.orElseThrow(() ->  new RestNullPointerException(headers, "Current User Token invalid.", HttpStatus.UNAUTHORIZED, StatusEnum.UNAUTHORIZED));
        Optional<User> product_author  = userRepo.findById(id);
        product_author.orElseThrow(()-> new RestNullPointerException(headers, "Finding by author id failed.", HttpStatus.NOT_FOUND, StatusEnum.NOT_FOUND));

        Iterable<Product> prod = prodRepo.findByUserId(id);
        message.setMessage("Finding with user id is Success.");
        message.setStatus(StatusEnum.OK);
        message.setData(prod);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);

    }

    @GetMapping("/current-login-user")
    public ResponseEntity<Message> findByLoggedUserId(Principal principal) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json",Charset.forName("UTF-8")));

        Optional<User> current_user = userRepo.findByPhone(principal.getName());
        current_user.orElseThrow(() -> new RestNullPointerException(headers, "Current User Token invalid.", HttpStatus.UNAUTHORIZED, StatusEnum.UNAUTHORIZED));

        Iterable<Product> prodList = prodRepo.findByUserId(current_user.get().getId());
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
            Principal principal,
            @PathVariable Long id,
            @RequestPart ProductCreateDto prodDto,
            @RequestPart(value="PhotoFile", required = false) List<MultipartFile> files,
            @RequestPart(value="ReceiptFile", required = false) MultipartFile ReceiptFile
            ) {

        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Optional<User> user = userRepo.findByPhone(principal.getName());
        user.orElseThrow(()-> new RestNullPointerException(headers, "User Token invalid.", HttpStatus.UNAUTHORIZED, StatusEnum.UNAUTHORIZED));

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
            if(prod.get().getUser().getId() != user.get().getId()) {
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
                    photoFileList.add(fileService.saveFile(fileDto));
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
    public ResponseEntity<Message> delete(Principal principal, @PathVariable Long id) {
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Optional<User> user = userRepo.findByPhone(principal.getName());
        user.orElseThrow(()-> new RestNullPointerException(headers, "User Token invalid.", HttpStatus.UNAUTHORIZED, StatusEnum.UNAUTHORIZED));

        Optional<Product> prod = prodRepo.findById(id);
        prod.orElseThrow(()-> new RestNullPointerException(headers, "Can't Find Product by Id", HttpStatus.NOT_FOUND, StatusEnum.NOT_FOUND));


        if(prod.get().getUser().getId() == user.get().getId()) {
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

}

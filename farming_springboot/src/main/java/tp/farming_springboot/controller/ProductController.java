package tp.farming_springboot.controller;


import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import tp.farming_springboot.exception.RestNullPointerException;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;

import java.io.File;
import java.nio.charset.Charset;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value="/product")
public class ProductController {
    @Autowired
    private ProductRepository prodRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private FileRepository photoFileRepo;
    @Autowired
    private FileService fileService;
    @Autowired
    private CategoryRepository categoryRepository;
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
    public ResponseEntity<Message> create(Principal principal, @RequestPart ProductCreateDto prodDto, @RequestPart (value="PhotoFile", required=false) List<MultipartFile> files) {

        Optional<User> user = null;
        List<PhotoFile> photoFileList = null;
        Message message = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        try {
            user = userRepo.findByPhone(principal.getName());
            user.orElseThrow(() -> new RestNullPointerException(headers, "User Token invalid.", HttpStatus.UNAUTHORIZED, null, StatusEnum.UNAUTHORIZED));

            for(MultipartFile file : files) {
                String origFilename = file.getOriginalFilename();
                String filename = new MD5Generator(origFilename).toString(); // file name 암호화
                String savePath = System.getProperty("user.dir") + "/photo_files";

                if (!new File(savePath).exists()) {
                    try {
                        new File(savePath).mkdir();
                    }
                    catch(Exception e){
                        message = new Message(StatusEnum.INTERNAL_SERVER_ERROR, "Making File-dir failed.");
                        return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                String filePath = savePath + "/" + filename;
                file.transferTo(new File(filePath));

                PhotoFileDto fileDto = new PhotoFileDto(origFilename, filename, filePath);

                if(photoFileList == null)
                    photoFileList = new ArrayList<PhotoFile>();

                photoFileList.add(fileService.saveFile(fileDto));
            }
            prodDto.setPhotoFile(photoFileList);

        } catch(Exception e) {
            message = new Message(StatusEnum.INTERNAL_SERVER_ERROR, "File reading error.");
            return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        prodDto.setAddress(user.get().getCurrent().getContent());
        Product prod = prodRepo.save(new Product(user, prodDto, categoryRepository));

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

    @PostMapping("/{id}/photo")
    public ResponseEntity<Message> addPhotoToProduct(Principal principal, @PathVariable Long id, @RequestPart(value="PhotoFile") List<MultipartFile> files) {
        Message message =new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));


        Optional<Product> prod = null;

        Optional<User> user = userRepo.findByPhone(principal.getName());
        user.orElseThrow(()-> new RestNullPointerException(headers, "User Token invalid.", HttpStatus.UNAUTHORIZED, StatusEnum.UNAUTHORIZED));

        try {
            List<PhotoFile> photoFileList = null;

            prod = prodRepo.findById(id);
            prod.orElseThrow(()-> new RestNullPointerException(headers, "Can't Find Product by Id", HttpStatus.NOT_FOUND, StatusEnum.NOT_FOUND));

            if(prod.get().getUser().getId() != user.get().getId()) {
                message.setStatus(StatusEnum.UNAUTHORIZED);
                message.setMessage("You can't add photo, you must author of this product.");
                return new ResponseEntity<>(message, headers, HttpStatus.UNAUTHORIZED);
            }

            for(MultipartFile file : files) {

                String origFilename = file.getOriginalFilename();
                String filename = new MD5Generator(origFilename).toString(); // file name 암호화
                String savePath = System.getProperty("user.dir") + "/photo_files";

                if (!new File(savePath).exists()) {
                    try{
                        new File(savePath).mkdir();
                    }
                    catch(Exception e){
                        message.setStatus(StatusEnum.INTERNAL_SERVER_ERROR);
                        message.setMessage("Making File-dir failed.");
                        return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                String filePath = savePath + "/" + filename;
                file.transferTo(new File(filePath));

                PhotoFileDto fileDto = new PhotoFileDto(origFilename, filename, filePath);

                if(photoFileList == null) {
                    photoFileList = new ArrayList<PhotoFile>();
                }
                photoFileList.add(fileService.saveFile(fileDto));
            }

            prod.get().addPhoto(photoFileList);
            prodRepo.save(prod.get());

            message.setStatus(StatusEnum.OK);
            message.setMessage("Photo uploading success.");
            message.setData(photoFileList);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);

        } catch(Exception e) {
            message.setStatus(StatusEnum.INTERNAL_SERVER_ERROR);
            message.setMessage("Photo uploading failed.");
            return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/{product_id}/photo/{photo_id}")
    public ResponseEntity<Message> deletePhotoToProduct(Principal principal, @PathVariable Long product_id, @PathVariable Long photo_id) {
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Optional<User> user = userRepo.findByPhone(principal.getName());
        user.orElseThrow(()-> new RestNullPointerException(headers, "User Token invalid.", HttpStatus.UNAUTHORIZED, StatusEnum.UNAUTHORIZED));

        Optional<Product> prod = prodRepo.findById(product_id);
        prod.orElseThrow(()-> new RestNullPointerException(headers, "Can't Find Product by Id", HttpStatus.NOT_FOUND, StatusEnum.NOT_FOUND));

        Optional<PhotoFile> photoFile = photoFileRepo.findById(photo_id);
        photoFile.orElseThrow(()-> new RestNullPointerException(headers, "Can't Find Photo by Id", HttpStatus.NOT_FOUND, StatusEnum.NOT_FOUND));

        // 게시글이 없을 때 등등 추가적인 예외처리 필요함.
        if(prod.get().getUser().getId() == user.get().getId()) {
            if(prod.get().getPhotoFile().contains(photoFile.get())) {
                prod.get().deletePhoto(photoFile.get());
                prodRepo.save(prod.get());
                photoFileRepo.delete(photoFile.get());
                message.setStatus(StatusEnum.OK);
                message.setMessage("Photo deleted");
                message.setData(prod);
                return new ResponseEntity<>(message, headers, HttpStatus.OK);
            }
            else {
                message.setStatus(StatusEnum.UNMATCH);
                message.setMessage("This Photo isn't included in this Product. Enter Another Product id or Photo id.");
                return new ResponseEntity<>(message, headers, HttpStatus.NOT_FOUND);
            }
        }
        else {
            message.setStatus(StatusEnum.UNAUTHORIZED);
            message.setMessage("Can't delete Photo, You must author of this product");
            return new ResponseEntity<>(message, headers, HttpStatus.UNAUTHORIZED);
        }

    }

    // 게시물 id로 수정하기
    @PutMapping("/{id}")
    public ResponseEntity<Message> update(Principal principal, @PathVariable Long id, @RequestPart ProductCreateDto prodDto) {
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Optional<User> user = userRepo.findByPhone(principal.getName());
        user.orElseThrow(()-> new RestNullPointerException(headers, "User Token invalid.", HttpStatus.UNAUTHORIZED, StatusEnum.UNAUTHORIZED));


        Optional<Product> prod = prodRepo.findById(id);
        prod.orElseThrow(()-> new RestNullPointerException(headers, "Can't Find Product by Id", HttpStatus.NOT_FOUND, StatusEnum.NOT_FOUND));

        if(prod.get().getUser().getId() == user.get().getId()) {

            if (prodDto.getTitle() != null)
                prod.get().setTitle(prodDto.getTitle());

            if (prodDto.getContent() != null)
                prod.get().setContent(prodDto.getContent());

            if (prodDto.getPrice() != null)
                prod.get().setPrice(prodDto.getPrice());

            if (prodDto.getAddress() != null)
                prod.get().setAddress(prodDto.getAddress());

            if (prodDto.getQuantity() != null)
                prod.get().setQuantity(prodDto.getQuantity());

            prodRepo.save(prod.get());
            message.setStatus(StatusEnum.OK);
            message.setData(prod);
            message.setMessage("Product updated.");
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        else {
            message.setStatus(StatusEnum.UNAUTHORIZED);
            message.setMessage("Can't edit Product, You must author of this product");
            return new ResponseEntity<>(message, headers, HttpStatus.UNAUTHORIZED);
        }

    }
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

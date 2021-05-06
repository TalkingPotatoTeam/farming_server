package tp.farming_springboot.controller;


import org.apache.ibatis.executor.ExecutorException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
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
    public ProductController(ProductRepository prodRepo, UserRepository userRepository, FileRepository photoFileRepository, FileService fileService) {
        this.prodRepo = prodRepo;
        this.userRepo = userRepository;
        this.photoFileRepo = photoFileRepository;
        this.fileService = fileService;
    }

    @PostMapping
    @ResponseBody
    //@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Message> create(Principal principal, @RequestPart ProductCreateDto prodDto, @RequestPart (value="PhotoFile", required=false) List<MultipartFile> files) {

        Optional<User> user = null;
        List<PhotoFile> photoFileList = null;
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        try {
            try {
                user = userRepo.findByPhone(principal.getName());
                user.orElseThrow(() -> new Exception("User Token invalid."));
            }
            catch(Exception e) {
                message.setStatus(StatusEnum.UNAUTHORIZED);
                message.setMessage(e.getMessage());
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

                PhotoFileDto fileDto = new PhotoFileDto();
                fileDto.setOrigFilename(origFilename);
                fileDto.setFilename(filename);
                fileDto.setFilePath(filePath);

                if(photoFileList == null)
                    photoFileList = new ArrayList<PhotoFile>();

                photoFileList.add(fileService.saveFile(fileDto));
            }
            prodDto.setPhotoFile(photoFileList);

        } catch(Exception e) {
            message.setStatus(StatusEnum.INTERNAL_SERVER_ERROR);
            message.setMessage("File reading error");
            return new ResponseEntity<>(message, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        prodDto.setAddress(user.get().getCurrent().getContent());
        Product prod = prodRepo.save(new Product(user, prodDto));
        message.setStatus(StatusEnum.OK);
        message.setMessage("Product item uploaded");
        message.setData(prod);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    // prodRepo의 findall return type => *Iterable*
    @GetMapping
    public ResponseEntity<Message> list(Principal principal) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        Optional<User> user = null;
        try {
            user = userRepo.findByPhone(principal.getName());
            user.orElseThrow(() -> new Exception("User Token invalid."));
        }
        catch(Exception e) {
            message.setStatus(StatusEnum.UNAUTHORIZED);
            message.setMessage(e.getMessage());
            return new ResponseEntity<>(message, headers, HttpStatus.UNAUTHORIZED);
        }

        Iterable<Product> prodList = prodRepo.findAll();

        message.setMessage("finding all of product is success.");
        message.setData(prodList);
        message.setStatus(StatusEnum.OK);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findByProductId(Principal principal, @PathVariable Long id) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json",Charset.forName("UTF-8")));
        Optional<User> user = null;
        try {
            try {
                user = userRepo.findByPhone(principal.getName());
                user.orElseThrow(() -> new Exception("User Token invalid."));
            }
            catch(Exception e) {
                message.setStatus(StatusEnum.UNAUTHORIZED);
                message.setMessage(e.getMessage());
                return new ResponseEntity<>(message, headers, HttpStatus.UNAUTHORIZED);
            }

            Optional<Product> prod = prodRepo.findById(id);
            prod.orElseThrow(()-> new Exception("Finding with id product failed."));
            message.setMessage("Finding with id product is Success.");
            message.setStatus(StatusEnum.OK);
            message.setData(prod);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (Exception e) {
            message.setMessage(e.getMessage());
            message.setStatus(StatusEnum.NOT_FOUND);
            return new ResponseEntity<>(message, headers, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Message> findByUserId(Principal principal, @PathVariable Long id) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json",Charset.forName("UTF-8")));
        // 게시글이 없는 경우 빈 데이터.
        Optional<User> current_user = null;
        Optional<User> product_author = null;
        try {
            try {
                current_user = userRepo.findByPhone(principal.getName());
                current_user.orElseThrow(() -> new Exception("Current User Token invalid."));
                product_author = userRepo.findById(id);
                product_author.orElseThrow(()->new Exception("Author id invalid."));
            }
            catch(Exception e) {

                message.setStatus(StatusEnum.UNAUTHORIZED);
                message.setMessage(e.getMessage());
                return new ResponseEntity<>(message, headers, HttpStatus.UNAUTHORIZED);
            }

            Iterable<Product> prod = prodRepo.findByUserId(id);
            message.setMessage("Finding with user id is Success.");
            message.setStatus(StatusEnum.OK);
            message.setData(prod);
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        catch (Exception e) {
            message.setMessage("User id invalid.");
            message.setStatus(StatusEnum.NOT_FOUND);
            return new ResponseEntity<>(message, headers, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/current-login-user")
    public ResponseEntity<Message> findByLoggedUserId(Principal principal) {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application","json",Charset.forName("UTF-8")));

        Optional<User> current_user = null;

        try {
            current_user = userRepo.findByPhone(principal.getName());
            current_user.orElseThrow(() -> new Exception("Current User Token invalid."));
        } catch (Exception e) {
            message.setStatus(StatusEnum.UNAUTHORIZED);
            message.setMessage(e.getMessage());
            return new ResponseEntity<>(message, headers, HttpStatus.UNAUTHORIZED);
        }
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

        Optional<User> user = null;
        Optional<Product> prod = null;
        try {
            user = userRepo.findByPhone(principal.getName());
            user.orElseThrow(()-> new Exception("User Token Invalid."));
        }
        catch (Exception e) {
            message.setStatus(StatusEnum.UNAUTHORIZED);
            message.setMessage(e.getMessage());
            return new ResponseEntity<>(message, headers, HttpStatus.UNAUTHORIZED);
        }

        try {
            List<PhotoFile> photoFileList = null;

            try {
                prod = prodRepo.findById(id);
                prod.orElseThrow(()-> new Exception("Product is not existed."));
            }
            catch (Exception e) {
                message.setStatus(StatusEnum.NOT_FOUND);
                message.setMessage(e.getMessage());
                return new ResponseEntity<>(message, headers, HttpStatus.NOT_FOUND);
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

                PhotoFileDto fileDto = new PhotoFileDto();
                fileDto.setOrigFilename(origFilename);
                fileDto.setFilename(filename);
                fileDto.setFilePath(filePath);

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
// 위에까지 수정함 ㅠ
    @DeleteMapping("/{product_id}/photo/{photo_id}")
    public ResponseEntity<Message> deletePhotoToProduct(Principal principal, @PathVariable Long product_id, @PathVariable Long photo_id) {
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Optional<Product> prod = prodRepo.findById(product_id);
        Optional<User> user = userRepo.findByPhone(principal.getName());
        Optional<PhotoFile> photoFile = photoFileRepo.findById(photo_id);

        // 게시글이 없을 때 등등 추가적인 예외처리 필요함.
        if(prod.get().getUser().getId() == user.get().getId()) {
            if(prod.get().getPhotoFile().contains(photoFile.get())) {
                prod.get().deletePhoto(photoFile.get());
                prodRepo.save(prod.get());
                photoFileRepo.delete(photoFile.get());
                message.setStatus(StatusEnum.OK);
                message.setMessage("게시글을 삭제했습니다.");
                message.setData(user);
                return new ResponseEntity<>(message, headers, HttpStatus.OK);
            }
            else {
                message.setStatus(StatusEnum.NOT_FOUND);
                message.setMessage("게시글에 해당 사진이 존재하지 않습니다.");
                message.setData(user);
                return new ResponseEntity<>(message, headers, HttpStatus.NOT_FOUND);
            }
        }
        else {
            message.setStatus(StatusEnum.UNAUTHORIZED);
            message.setMessage("게시글 작성자가 아니면 삭제할 수 없습니다.");
            message.setData(user);
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

        if(!user.isPresent()){
            message.setStatus(StatusEnum.NOT_FOUND);
            message.setMessage("유저를 찾을 수 없습니다.");
            return new ResponseEntity<>(message,headers, HttpStatus.NOT_FOUND);
        }
        message.setData(user);
        Optional<Product> prod = prodRepo.findById(id);

        if(prod.isPresent()) {

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
            message.setMessage("게시글을 업데이트 했습니다.");
            return new ResponseEntity<>(message,headers, HttpStatus.OK);
        }
        else {
            message.setStatus(StatusEnum.NOT_FOUND);
            message.setMessage("게시글이 존재하지 않습니다.");
            return new ResponseEntity<>(message,headers, HttpStatus.NOT_FOUND);
        }

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> delete(Principal principal, @PathVariable Long id) {
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Optional<Product> prod = prodRepo.findById(id);
        Optional<User> user = userRepo.findByPhone(principal.getName());

        // 게시글이 없을 때 등등 추가적인 예외처리 필요함.
        if(prod.get().getUser().getId() == user.get().getId()) {
            prodRepo.deleteById(id);

            message.setStatus(StatusEnum.OK);
            message.setMessage("게시글을 삭제했습니다.");
            return new ResponseEntity<>(message, headers, HttpStatus.OK);
        }
        else {
            message.setStatus(StatusEnum.UNAUTHORIZED);
            message.setMessage("게시글 작성자가 아니면 삭제할 수 없습니다.");
            return new ResponseEntity<>(message, headers, HttpStatus.UNAUTHORIZED);
        }


    }



}

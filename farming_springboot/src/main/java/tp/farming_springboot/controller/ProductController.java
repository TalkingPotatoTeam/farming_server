package tp.farming_springboot.controller;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

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
import tp.farming_springboot.response.ResponseEntity;
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
    public Product create(Principal principal, @RequestPart ProductCreateDto prodDto, @RequestPart (value="PhotoFile", required=false) List<MultipartFile> files) {
        // user부터 체크하지 않으면 토큰이 만료됐어도 사진은 업로드됨 => try~ catch 예외처리해야될듯?
        Optional<User> user = userRepo.findByPhone(principal.getName());  // 토큰으로 인증된 로그인 유저 가져옴
        try {
            List<PhotoFile> photoFileList = null;

            for(MultipartFile file : files) {
                String origFilename = file.getOriginalFilename();
                String filename = new MD5Generator(origFilename).toString(); // file name 암호화
                String savePath = System.getProperty("user.dir") + "/photo_files";

                if (!new File(savePath).exists()) {
                    try{
                        new File(savePath).mkdir();
                    }
                    catch(Exception e){
                        e.getStackTrace();
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
            prodDto.setPhotoFile(photoFileList);

        } catch(Exception e) {
            e.printStackTrace();
        }

        prodDto.setAddress(user.get().getCurrent().getContent());
        return prodRepo.save(new Product(user, prodDto));
    }

    // prodRepo의 findall return type => *Iterable*
    @GetMapping
    public Iterable<Product> list(Principal principal) {
        return prodRepo.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Product> findByProductId(Principal principal, @PathVariable Long id) {
        return prodRepo.findById(id);
    }

    @GetMapping("/user/{id}")
    public Iterable<Product> findByUserId(Principal principal, @PathVariable Long id) {
        return prodRepo.findByUserId(id);
    }

    @GetMapping("/current-login-user")
    public Iterable<Product> findByLoggedUserId(Principal principal) {
        Optional<User> user = userRepo.findByPhone(principal.getName());
        return prodRepo.findByUserId(user.get().getId());
    }
    // 각종 예외 처리 아직..
    @PostMapping("/{id}/photo")
    public ResponseEntity<Message> addPhotoToProduct(Principal principal, @PathVariable Long id, @RequestPart(value="PhotoFile") List<MultipartFile> files) {
        Message message =new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        //  토큰 유효 절차 확인 필
        Optional<User> user = userRepo.findByPhone(principal.getName());

        if(!user.isPresent()){
            message.setStatus(HttpStatus.NOT_FOUND);
            message.setMessage("유저를 찾을 수 없습니다.");
            return new ResponseEntity<>(message,headers, message.getStatus());
        }
        message.setData(user);
        Optional<Product> prod = prodRepo.findById(id);

        try {
            List<PhotoFile> photoFileList = null;

            for(MultipartFile file : files) {

                String origFilename = file.getOriginalFilename();
                String filename = new MD5Generator(origFilename).toString(); // file name 암호화
                String savePath = System.getProperty("user.dir") + "/photo_files";

                if (!new File(savePath).exists()) {
                    try{
                        new File(savePath).mkdir();
                    }
                    catch(Exception e){
                        e.getStackTrace();
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
            message.setStatus(HttpStatus.OK);
            message.setMessage("게시물 사진을 추가하였습니다.");

        } catch(Exception e) {
            message.setStatus(HttpStatus.NOT_FOUND);
            message.setMessage("사진 추가를 실패했습니다.");
            e.printStackTrace();
        }
        return new ResponseEntity<>(message, headers, message.getStatus());
    }

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
                message.setStatus(HttpStatus.OK);
                message.setMessage("게시글을 삭제했습니다.");
                message.setData(user);
            }
            else {
                message.setStatus(HttpStatus.BAD_REQUEST);
                message.setMessage("게시글에 해당 사진이 존재하지 않습니다.");
                message.setData(user);
            }
        }
        else {
            message.setStatus(HttpStatus.BAD_REQUEST);
            message.setMessage("게시글 작성자가 아니면 삭제할 수 없습니다.");
            message.setData(user);
        }

        return new ResponseEntity<>(message, headers, message.getStatus());
    }

    // 게시물 id로 수정하기
    @PutMapping("/{id}")
    public ResponseEntity<Message> update(Principal principal, @PathVariable Long id, @RequestPart ProductCreateDto prodDto) {
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Optional<User> user = userRepo.findByPhone(principal.getName());

        if(!user.isPresent()){
            message.setStatus(HttpStatus.NOT_FOUND);
            message.setMessage("유저를 찾을 수 없습니다.");
            return new ResponseEntity<>(message,headers, message.getStatus());
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
            message.setStatus(HttpStatus.OK);
            message.setMessage("게시글을 업데이트 했습니다.");
        }
        else {
            message.setStatus(HttpStatus.NOT_FOUND);
            message.setMessage("게시글이 존재하지 않습니다.");

        }

        return new ResponseEntity<>(message,headers, message.getStatus());
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

            message.setStatus(HttpStatus.OK);
            message.setMessage("게시글을 삭제했습니다.");
            message.setData(user);
        }
        else {
            message.setStatus(HttpStatus.BAD_REQUEST);
            message.setMessage("게시글 작성자가 아니면 삭제할 수 없습니다.");
            message.setData(user);
        }

        return new ResponseEntity<>(message, headers, message.getStatus());

    }
}

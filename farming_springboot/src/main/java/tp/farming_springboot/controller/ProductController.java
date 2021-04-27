package tp.farming_springboot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.domain.product.dto.ProductCreateDto;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.product.repository.ProductRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.ResponseEntity;
import tp.farming_springboot.response.StatusEnum;

import java.nio.charset.Charset;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping(value="/product")
public class ProductController {
    @Autowired
    private ProductRepository prodRepo;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public ProductController(ProductRepository prodRepo) {
        this.prodRepo = prodRepo;
    }


    @PostMapping
    @ResponseBody
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Product create(Principal principal, @RequestBody ProductCreateDto prodDto) {
        System.out.println(principal.getName());

        Optional<User> user = userRepository.findByPhone(principal.getName());

        return prodRepo.save(new Product(user, prodDto));
    }

    // prodRepo의 findall return type => *Iterable*
    @GetMapping
    public Iterable<Product> list() {
        Iterable<Product> prodList = prodRepo.findAll();

        for (Product p : prodList) {
            System.out.println(p.getTitle());
        }
        return prodRepo.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Product> findOne(@PathVariable Long id) {
        return prodRepo.findById(id);
    }

    @GetMapping("/byUser/{id}")
    public Iterable<Product> findByUserId(@PathVariable Long id) {
        return prodRepo.findByUserId(id);
    }

    @GetMapping("/byLoggedUser")
    public Iterable<Product> findByLoggedUserId(Principal principal) {

        Optional<User> user = userRepository.findByPhone(principal.getName());

        return prodRepo.findByUserId(user.get().getId());
    }
    // 게시물 id로 수정하기
    @PutMapping("/{id}")
    public ResponseEntity<Message> update(Principal principal, @PathVariable Long id, @RequestBody ProductCreateDto prodDto) {
        Message message = new Message();
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Optional<User> user = userRepository.findByPhone(principal.getName());

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
        Optional<User> user = userRepository.findByPhone(principal.getName());


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

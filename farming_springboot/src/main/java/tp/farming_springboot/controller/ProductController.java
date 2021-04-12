package tp.farming_springboot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.product.repository.ProductRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping(value="/product")
public class ProductController {

    private ProductRepository prodRepo;
    @Autowired
    UserRepository userRepository;
    @Autowired
    public ProductController(ProductRepository prodRepo) {
        this.prodRepo = prodRepo;
    }


    @PostMapping("/create")
    @ResponseBody
    public Product put(@RequestParam String username) {

        System.out.println(username + " ");

        // find user object by user id
        // post request로 데이터베이스에 저장되는 것 확인하였음(완성x)
        return prodRepo.save(new Product(new User()));
    }

    // prodRepo의 findall return type => *Iterable*
    @GetMapping("/get")
    public Iterable<Product> list() {
        return prodRepo.findAll();
    }

    @GetMapping("/get/{id}")
    public Optional<Product> findOne(@PathVariable Long id) {
        return prodRepo.findById(id);
    }

    @GetMapping("/get/byUser/{id}")
    public Iterable<Product> findByUserId(@PathVariable Long id) {
        return prodRepo.findByUserId(id);
    }



}

package tp.farming_springboot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @Autowired
    private ProductRepository prodRepo;
    @Autowired
    UserRepository userRepository;

    @Autowired
    public ProductController(ProductRepository prodRepo) {
        this.prodRepo = prodRepo;
    }


    @PostMapping("/create")
    @ResponseBody
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public Product put(Principal principal, @RequestBody String title, String content) {
        System.out.println(principal.getName());

        Optional<User> user = userRepository.findByPhone(principal.getName());

        return prodRepo.save(new Product(user, content));
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

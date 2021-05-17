package tp.farming_springboot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tp.farming_springboot.domain.product.dto.PagingDTO;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.product.repository.ProductRepository;

@RestController
@RequiredArgsConstructor
public class PageController {
    @Autowired
    ProductRepository productRepository;

    @CrossOrigin(origins = "*",allowedHeaders = "*")
    @GetMapping("/home")
    public Page<PagingDTO> home(@PageableDefault(size=3, sort="createdDate",direction= Sort.Direction.DESC) Pageable pageRequest){
        Page<Product> productList = productRepository.findAll(pageRequest);
        Page<PagingDTO> pagingList = productList.map(
                product -> new PagingDTO(
                        product.getId(),product.getTitle(),
                        product.getUser().getId(), product.getCreatedDate()
                ));
        return pagingList;
    }
    @CrossOrigin(origins = "*",allowedHeaders = "*")
    @GetMapping("/search")
    public Page<PagingDTO> search(
            @RequestParam String title,
            @RequestParam String content,
            @PageableDefault(size=3, sort="createdDate",direction= Sort.Direction.DESC) Pageable pageRequest){
        Page<Product> productList = productRepository.findAll(pageRequest);
        Page<PagingDTO> pagingList = productList.map(
                product -> new PagingDTO(
                        product.getId(),product.getTitle(),
                        product.getUser().getId(), product.getCreatedDate()
                ));
        return pagingList;
    }
}

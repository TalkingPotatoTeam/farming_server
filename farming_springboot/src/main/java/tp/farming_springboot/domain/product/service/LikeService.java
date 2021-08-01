package tp.farming_springboot.domain.product.service;


import lombok.RequiredArgsConstructor;
import java.util.*;
import org.springframework.stereotype.Service;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.product.repository.ProductRepository;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.service.UserService;
import tp.farming_springboot.exception.UserAlreadyLikeProductException;
import tp.farming_springboot.exception.UserNotLikeProductException;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final ProductService productService;
    private final UserService userService;
    private final ProductRepository productRepository;

    public void create(String userPhone, Long productId) throws UserAlreadyLikeProductException {
        User user = userService.findUserByPhone(userPhone);
        Product product = productRepository.findByIdOrElseThrow(productId);

        if(product.getLikeUsers().contains(user)){
            throw new UserAlreadyLikeProductException("User { id: " + user.getId() + " }" + " already liked this product.");
        }else {
            product.getLikeUsers().add(user);
            productRepository.save(product);
        }

    }

    public void delete(String userPhone, Long productId) throws UserNotLikeProductException {
        User user = userService.findUserByPhone(userPhone);
        Product product = productRepository.findByIdOrElseThrow(productId);

        if(product.getLikeUsers().contains(user)){
            product.getLikeUsers().remove(user);
            productRepository.save(product);
        } else {
            throw new UserNotLikeProductException("User { id: " + user.getId() + " }" + " not included in like user list.");
        }
    }

    public Set<User> getLikeUserSet(Long productId) {
        Product product = productRepository.findByIdOrElseThrow(productId);
        return product.getLikeUsers();
    }
}

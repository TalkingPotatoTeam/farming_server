package tp.farming_springboot.application;

import lombok.RequiredArgsConstructor;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import tp.farming_springboot.application.dto.response.ProductDetailResDto;
import tp.farming_springboot.domain.entity.Product;
import tp.farming_springboot.domain.repository.ProductRepository;
import tp.farming_springboot.application.dto.response.LikeUserResDto;
import tp.farming_springboot.domain.entity.User;
import tp.farming_springboot.domain.repository.UserRepository;
import tp.farming_springboot.domain.exception.UserAlreadyLikeProductException;
import tp.farming_springboot.domain.exception.UserNotLikeProductException;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void create(String userPhone, Long productId) throws UserAlreadyLikeProductException {
        User user = userRepository.findByPhoneElseThrow(userPhone);

        Product product = productRepository.findByIdOrElseThrow(productId);

        if(product.getLikeUsers().contains(user)){
            throw new UserAlreadyLikeProductException(String.format("User { id: %d } already liked this product.", user.getId()));
        } else {
            product.getLikeUsers().add(user);
            productRepository.save(product);
        }

    }

    public void delete(String userPhone, Long productId) throws UserNotLikeProductException {
        User user = userRepository.findByPhoneElseThrow(userPhone);
        Product product = productRepository.findByIdOrElseThrow(productId);

        if(product.getLikeUsers().contains(user)){
            product.getLikeUsers().remove(user);
            productRepository.save(product);
        } else {
            throw new UserNotLikeProductException(String.format("User { id: %d } not included in like user list.", user.getId()));
        }
    }

    public Set<LikeUserResDto> getLikeUserSet(Long productId) {
        Product product = productRepository.findByIdOrElseThrow(productId);

        Set<LikeUserResDto> userSet = product.getLikeUsers()
                .stream()
                .map(LikeUserResDto::from)
                .collect(Collectors.toSet());

        return userSet;
    }

    public Set<ProductDetailResDto> getLikelistByUser(Long userId) {
        User user = userRepository.findByIdElseThrow(userId);

        Set<ProductDetailResDto> productResponseDtos = user.getLikeProducts()
                                .stream()
                                .map(ProductDetailResDto::from)
                                .collect(Collectors.toSet());


        return productResponseDtos;
    }
}

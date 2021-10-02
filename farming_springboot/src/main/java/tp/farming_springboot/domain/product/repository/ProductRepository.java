package tp.farming_springboot.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.exception.RestNullPointerException;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Iterable<Product> findByUserId(Long id);

    Optional<Product> findById(Long id);

    default Product findByIdOrElseThrow(Long id) {
        return this.findById(id).orElseThrow(
                () -> new RestNullPointerException("Can't Find Product by <Id: " + id + ">")
        );
    }

    Page<Product> findAll(Pageable pageable);
    @Query(
            value = "SELECT p FROM Product p WHERE p.title LIKE %:title% OR p.content LIKE %:content%",
            countQuery = "SELECT COUNT(p.id) FROM Product p WHERE p.title  LIKE %:title% OR p.content LIKE %:content% "
    )
    Page<Product> findAllSearch(String title, String content, Pageable pageable);
}

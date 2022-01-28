package tp.farming_springboot.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import tp.farming_springboot.domain.product.model.Category;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.exception.RestNullPointerException;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUserId(Long id);

    Optional<Product> findById(Long id);

    default Product findByIdOrElseThrow(Long id) {
        return this.findById(id).orElseThrow(
                () -> new RestNullPointerException("Can't Find Product by <Id: " + id + ">")
        );
    }

    Page<Product> findAll(Pageable pageable);

    @Query(
            value = "SELECT p FROM Product p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) AND p.category in :categoryList",
            countQuery = "SELECT COUNT(p.id) FROM Product p WHERE p.title  LIKE %:keyword% OR p.content LIKE %:keyword% "
    )
    Page<Product> findByKeywordInCategoryList(String keyword,List<Category> categoryList, Pageable pageable);


    Page<Product> findByCategory(Category category, Pageable pageable);
}

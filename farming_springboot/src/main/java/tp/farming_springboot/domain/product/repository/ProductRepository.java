package tp.farming_springboot.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import tp.farming_springboot.domain.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public Iterable<Product> findByUserId(Long id);



    Page<Product> findAll(Pageable pageable);
    @Query(
            value = "SELECT p FROM Product p WHERE p.title LIKE %:title% OR p.content LIKE %:content%",
            countQuery = "SELECT COUNT(p.id) FROM Product p WHERE p.title  LIKE %:title% OR p.content LIKE %:content% "
    )
    Page<Product> findAllSearch(String title, String content, Pageable pageable);
}

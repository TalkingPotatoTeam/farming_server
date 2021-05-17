package tp.farming_springboot.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import tp.farming_springboot.domain.product.model.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {
    public Iterable<Product> findByUserId(Long id);
    Page<Product> findAll(Pageable pageable);
}

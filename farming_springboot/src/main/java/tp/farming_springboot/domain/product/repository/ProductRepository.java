package tp.farming_springboot.domain.product.repository;

import org.springframework.data.repository.CrudRepository;
import tp.farming_springboot.domain.product.model.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {
    public Iterable<Product> findByUserId(Long id);
}

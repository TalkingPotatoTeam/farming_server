package tp.farming_springboot.repository;

import org.springframework.data.repository.CrudRepository;
import tp.farming_springboot.domain.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

}

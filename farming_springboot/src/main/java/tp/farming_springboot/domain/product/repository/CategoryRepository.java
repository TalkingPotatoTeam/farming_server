package tp.farming_springboot.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tp.farming_springboot.domain.product.model.Category;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}

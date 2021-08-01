package tp.farming_springboot.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tp.farming_springboot.domain.product.model.Category;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.exception.RestNullPointerException;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);


    default Category findByNameOrElseThrow(String name) {
        return this.findByName(name).orElseThrow(
                () -> new RestNullPointerException("Can't Find Category by <name: " + name + ">")
        );
    }



    @Query(
            value = "SELECT distinct(p.name) FROM Category p"
    )
    Iterable<Category> getCategories();
}

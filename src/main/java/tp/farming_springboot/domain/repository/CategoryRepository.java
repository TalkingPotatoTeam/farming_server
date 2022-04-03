package tp.farming_springboot.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tp.farming_springboot.api.ResultCode;
import tp.farming_springboot.domain.entity.Category;
import tp.farming_springboot.domain.exception.RestNullPointerException;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);


    default Category findByNameOrElseThrow(String name) {
        return this.findByName(name).orElseThrow(() -> new RestNullPointerException(ResultCode.CATEGORY_NOT_FOUND.getMessage()));
    }

    default Category findByIdOrElseThrow(Long id) {
        return this.findById(id).orElseThrow(()-> new RestNullPointerException(ResultCode.CATEGORY_NOT_FOUND.getMessage()));
    }

    List<Category> findByNameIn(List<String> categoryName);


    @Query(
            value = "SELECT distinct(p.name) FROM Category p"
    )
    Iterable<Category> getCategories();
}

package tp.farming_springboot.domain.product.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tp.farming_springboot.domain.product.model.PhotoFile;
import tp.farming_springboot.domain.product.model.Product;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<PhotoFile, Long> {
    public Iterable<PhotoFile> findByProductId(Long id);
    @Modifying
    @Transactional
    @Query(
            value = "delete from PhotoFile p where p.product.id = :id"
    )
    public void deleteRelatedProductId(Long id);

    @Modifying
    @Transactional
    @Query(
            value = "delete from PhotoFile p where p.id = :id"
    )
    public void deleteByPhotoId(Long id);
}

package tp.farming_springboot.domain.product.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tp.farming_springboot.domain.product.model.PhotoFile;



@Repository
@Transactional
public interface FileRepository extends JpaRepository<PhotoFile, Long> {
     Iterable<PhotoFile> findByProductId(Long id);


    @Modifying
    @Transactional
    @Query(
            value = "delete from PhotoFile p where p.product.id = :id"
    )
     void deleteRelatedProductId(Long id);

    @Modifying
    @Transactional
    @Query(
            value = "delete from PhotoFile p where p.id = :id"
    )
     void deleteByPhotoId(Long id);
}

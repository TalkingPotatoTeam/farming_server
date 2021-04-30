package tp.farming_springboot.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tp.farming_springboot.domain.product.model.PhotoFile;


public interface FileRepository extends JpaRepository<PhotoFile, Long> {

}

package tp.farming_springboot.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tp.farming_springboot.domain.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

}

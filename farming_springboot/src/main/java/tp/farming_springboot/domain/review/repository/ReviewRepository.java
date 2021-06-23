package tp.farming_springboot.domain.review.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tp.farming_springboot.domain.review.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

}

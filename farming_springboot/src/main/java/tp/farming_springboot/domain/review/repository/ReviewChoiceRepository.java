package tp.farming_springboot.domain.review.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tp.farming_springboot.domain.review.model.ReviewChoice;

import java.util.Optional;

@Repository
public interface ReviewChoiceRepository extends JpaRepository<ReviewChoice, Long> {

    Optional<ReviewChoice> findByReviewContent(String reviewContent);
}

package tp.farming_springboot.domain.user;
import tp.farming_springboot.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
//기본적인 Create, Read, Update, Delete가 자동으로 생성됩니다!
    Optional<User> findByPhone(String phone);
    Boolean existsByPhone(String phone);
}

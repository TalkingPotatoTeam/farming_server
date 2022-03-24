package tp.farming_springboot.domain.repository;
import tp.farming_springboot.domain.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByPhone(String phone);


    default User findByPhoneElseThrow(String phone) {
        return findByPhone(phone).orElseThrow(
                () -> new NullPointerException("사용자를 찾을 수 없어요.")
        );
    }


    Optional<User> findById(Long id);

    default User findByIdElseThrow(Long id) {
        return this.findById(id).orElseThrow(
                () -> new NullPointerException("사용자를 찾을 수 없어요.")
        );
    }

}

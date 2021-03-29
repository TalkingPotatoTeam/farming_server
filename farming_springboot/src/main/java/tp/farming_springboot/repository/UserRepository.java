package tp.farming_springboot.repository;
import tp.farming_springboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
//기본적인 Create, Read, Update, Delete가 자동으로 생성됩니다!
}

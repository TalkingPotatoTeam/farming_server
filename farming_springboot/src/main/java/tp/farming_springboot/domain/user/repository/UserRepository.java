package tp.farming_springboot.domain.user.repository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import tp.farming_springboot.config.UserDetailsImpl;
import tp.farming_springboot.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
//기본적인 Create, Read, Update, Delete가 자동으로 생성됩니다!
    Optional<User> findByPhone(String phone);
    @Transactional
    public UserDetails loadUserByUsername(String username) ;


}

package tp.farming_springboot.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import tp.farming_springboot.domain.user.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}

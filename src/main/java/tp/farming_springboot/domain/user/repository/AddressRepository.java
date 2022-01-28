package tp.farming_springboot.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import tp.farming_springboot.domain.user.model.Address;


@Component
public interface AddressRepository extends JpaRepository<Address, Long> {
}

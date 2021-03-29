package tp.farming_springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tp.farming_springboot.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}

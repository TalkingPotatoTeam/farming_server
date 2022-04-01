package tp.farming_springboot.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tp.farming_springboot.domain.entity.Address;
import tp.farming_springboot.domain.repository.AddressRepository;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    //create
    public Address create(Long user_id, String content, Double lat, Double lon){
        Address newAddress = new Address(user_id, content, lat, lon);
        addressRepository.save(newAddress);
        return newAddress;
    }
    //delete
    public void delete(long addressId){
        addressRepository.deleteById(addressId);
    }

}

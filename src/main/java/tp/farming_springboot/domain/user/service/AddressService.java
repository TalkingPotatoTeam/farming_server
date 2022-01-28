package tp.farming_springboot.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.domain.user.repository.AddressRepository;

import java.util.Optional;

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

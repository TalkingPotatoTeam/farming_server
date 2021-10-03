package tp.farming_springboot.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tp.farming_springboot.config.JwtUtils;
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.AddressRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;
import tp.farming_springboot.exception.AddressRemoveException;
import tp.farming_springboot.exception.RestNullPointerException;
import tp.farming_springboot.exception.UserExistsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    private final AddressRepository addressRepository;


    private final JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    private final OtpService otpService;
    //check if user exists

    public boolean checkUserExists(String phone){
        Optional<User> user = userRepository.findByPhone(phone);
        if(user.isPresent())return true;
        return false;
    }
    //check if user does not exist
    public User findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RestNullPointerException("Can't find User {id:" + id + "}"));
        return user;
    }

    public User findUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone).orElseThrow(() -> new RestNullPointerException("Can't find User {phone:" + phone + "}"));
        return user;
    }

    //create
    public void create(String userPhone)throws UserExistsException {
        Optional<User> user = userRepository.findByPhone(userPhone);
        if (user.isPresent()) throw new UserExistsException("User already exists");
        User newUser = new User(userPhone);
        newUser.setPassword(encoder.encode(userPhone));
        userRepository.save(newUser);
    }
    //update Phone num
    public void updatePhone(String userPhone, String newPhone)throws UserExistsException{
        Optional<User> user = userRepository.findByPhone(userPhone);
        Optional<User> newUser = userRepository.findByPhone(newPhone);
        if (newUser.isPresent()) throw new UserExistsException("This phone number is already taken");
        user.get().setPhone(newPhone);
        userRepository.save(user.get());
    }

    //delete
    public void delete(String userPhone){
        Optional<User> user = userRepository.findByPhone(userPhone);
        userRepository.deleteById(user.get().getId());
    }
    //add address
    public void addAddress(String userPhone, Address address){
        Optional<User> user = userRepository.findByPhone(userPhone);
        List<Address> addresses = new ArrayList<>();
        if (user.get().getAddresses() != null){
            addresses = user.get().getAddresses();
        }

        boolean isExisted = false;
        for(Address ad  : addresses) {
            if((ad.getContent()).equals(address.getContent())) {
                isExisted = true;
                break;
            }
        }

        if(isExisted) {
            throw new IllegalArgumentException("User already have this address: " + address.getContent());
        }else {
            addresses.add(address);
            user.get().setAddresses(addresses);
            setCurrentAddress(userPhone, address.getId());
            userRepository.save(user.get());
        }
    }
    //delete address
    public void deleteAddress(String userPhone, Long addressId) throws AddressRemoveException{
        Optional<User> user = userRepository.findByPhone(userPhone);
        List<Address> addresses = user.get().getAddresses();
        Optional<Address> toDelete = addressRepository.findById(addressId);

        if(!toDelete.isPresent() || !addresses.contains(toDelete.get())) throw new AddressRemoveException("Address does not exist. Check address Id.");
        if (user.get().getCurrent().getId() == addressId)throw new AddressRemoveException("Current address can not be deleted. Change Address First.");
        if(addresses.size() == 1)throw new AddressRemoveException("User should have at least one address");

        user.get().deleteAddress(toDelete.get());
        userRepository.save(user.get());
    }

    //set current address
    public void setCurrentAddress(String userPhone, Long addressId){
        Optional<User> user = userRepository.findByPhone(userPhone);
        Optional<Address> toCurrent = addressRepository.findById(addressId);
        user.get().setCurrent(toCurrent.get());
        userRepository.save(user.get());
    }

}

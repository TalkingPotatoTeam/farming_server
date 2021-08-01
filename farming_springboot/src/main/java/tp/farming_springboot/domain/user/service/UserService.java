package tp.farming_springboot.domain.user.service;


import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import tp.farming_springboot.config.JwtUtils;
import tp.farming_springboot.domain.product.model.Product;
import tp.farming_springboot.domain.user.dto.UserDto;
import tp.farming_springboot.domain.user.model.Address;
import tp.farming_springboot.domain.user.model.ERole;
import tp.farming_springboot.domain.user.model.Role;
import tp.farming_springboot.domain.user.model.User;
import tp.farming_springboot.domain.user.repository.AddressRepository;
import tp.farming_springboot.domain.user.repository.RoleRepository;
import tp.farming_springboot.domain.user.repository.UserRepository;
import tp.farming_springboot.exception.AddressRemoveException;
import tp.farming_springboot.exception.RestNullPointerException;
import tp.farming_springboot.exception.UserExistsException;
import tp.farming_springboot.response.Message;
import tp.farming_springboot.response.StatusEnum;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    private final AddressRepository addressRepository;

    private final RoleRepository roleRepository;

    private final JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    private final OtpService otpService;
    //check if user exists
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
        Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        newUser.addRole(userRole);
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
        List<Address> addresses = new ArrayList<Address>();
        if (user.get().getAddresses() != null){
            addresses = user.get().getAddresses();
        }
        addresses.add(address);
        user.get().setAddresses(addresses);
        userRepository.save(user.get());
    }
    //delete address
    public void deleteAddress(String userPhone, Long addressId) throws AddressRemoveException{
        Optional<User> user = userRepository.findByPhone(userPhone);
        List<Address> addresses = user.get().getAddresses();
        Optional<Address> toDelete = addressRepository.findById(addressId);

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

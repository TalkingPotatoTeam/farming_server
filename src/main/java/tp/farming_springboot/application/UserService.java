package tp.farming_springboot.application;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tp.farming_springboot.config.jwt.JwtUtils;
import tp.farming_springboot.application.dto.response.TokenDto;
import tp.farming_springboot.application.dto.request.UserForceCreateDto;
import tp.farming_springboot.application.dto.response.UserResDto;
import tp.farming_springboot.domain.dao.Address;
import tp.farming_springboot.domain.dao.User;
import tp.farming_springboot.domain.repository.AddressRepository;
import tp.farming_springboot.domain.repository.UserRepository;
import tp.farming_springboot.domain.exception.AddressRemoveException;
import tp.farming_springboot.domain.exception.UserExistsException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final JwtUtils jwtUtils;

    public boolean checkUserExists(String phone) {
        Optional<User> user = userRepository.findByPhone(phone);
        if (user.isPresent()) return true;
        return false;
    }

    @Transactional(rollbackOn = Exception.class)
    public void create(String userPhone) throws UserExistsException {
        Optional<User> user = userRepository.findByPhone(userPhone);
        if (user.isPresent()) throw new UserExistsException("User already exists");
        User newUser = new User(userPhone);
        userRepository.save(newUser);
    }

    @Transactional(rollbackOn = Exception.class)
    public void updatePhone(String userPhone, String newPhone) throws UserExistsException {
        User user = userRepository.findByPhoneElseThrow(userPhone);
        Optional<User> newUser = userRepository.findByPhone(newPhone);
        if (newUser.isPresent()) throw new UserExistsException("This phone number is already taken");
        user.updatePhone(newPhone);
        userRepository.save(user);
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(String userPhone) {
        Optional<User> user = userRepository.findByPhone(userPhone);
        userRepository.deleteById(user.get().getId());
    }

    @Transactional(rollbackOn = Exception.class)
    public void addAddress(String userPhone, Address address) {
        User user = userRepository.findByPhoneElseThrow(userPhone);
        List<Address> addresses = new ArrayList<>();
        if (user.getAddresses() != null) {
            addresses = user.getAddresses();
        }

        boolean isExisted = false;
        for (Address ad : addresses) {
            if ((ad.getContent()).equals(address.getContent())) {
                isExisted = true;
                break;
            }
        }

        if (isExisted) {
            throw new IllegalArgumentException("User already have this address: " + address.getContent());
        } else {
            addresses.add(address);
            user.setAddresses(addresses);

            setCurrentAddress(userPhone, address.getId());
            userRepository.save(user);
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteAddress(String userPhone, Long addressId) throws AddressRemoveException {
        Optional<User> user = userRepository.findByPhone(userPhone);
        List<Address> addresses = user.get().getAddresses();
        Optional<Address> toDelete = addressRepository.findById(addressId);

        if (!toDelete.isPresent() || !addresses.contains(toDelete.get()))
            throw new AddressRemoveException("Address does not exist. Check address Id.");
        if (user.get().getCurrent().getId() == addressId)
            throw new AddressRemoveException("Current address can not be deleted. Change Address First.");
        if (addresses.size() == 1) throw new AddressRemoveException("User should have at least one address");

        user.get().deleteAddress(toDelete.get());
        userRepository.save(user.get());
    }

    @Transactional(rollbackOn = Exception.class)
    public void setCurrentAddress(String userPhone, Long addressId) {
        User user = userRepository.findByPhoneElseThrow(userPhone);
        Optional<Address> toCurrent = addressRepository.findById(addressId);

        user.updateCurrentAddress(toCurrent.get());
        userRepository.save(user);
    }

    @Transactional(rollbackOn = Exception.class)
    public TokenDto createUserForce(UserForceCreateDto userDto) {
        User user = new User(userDto.getPhone());
        Address address = Address.of(user.getId(), userDto.getAddress(), 32.7, 32.8);

        user.addAddress(address);
        user.updateCurrentAddress(address);
        userRepository.save(user);

        String access = jwtUtils.generateJwtToken(user.getUsername());

        TokenDto tokenDto = new TokenDto(access, null);
        return tokenDto;
    }

    public UserResDto getUserInfo(String userPhone) {
        User user = userRepository.findByPhoneElseThrow(userPhone);
        return UserResDto.of(user.getId(), user.getPhone(), user.getCurrent().getContent());
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with phone num: " + username));

        return user;
    }

}



package tp.farming_springboot.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tp.farming_springboot.config.jwt.JwtUtils;
import tp.farming_springboot.application.dto.response.TokenDto;
import tp.farming_springboot.domain.entity.User;
import tp.farming_springboot.domain.exception.VerificationException;
import tp.farming_springboot.domain.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthenticateService {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    public void verifyOtp(int userOtp, int serverOtp) throws VerificationException{
        if(userOtp >= 0){
            if(userOtp == 12345) return; //otp for testing
            if(serverOtp >=0){
                if (userOtp == serverOtp) return;
                else throw new VerificationException("Invalid Otp");
            }
            else throw new VerificationException("Expired Otp");
        }
        else throw new VerificationException("Contact Admin");
    }

    public TokenDto getNewTokens(String userPhone){
        Optional<User> userOptional = userRepository.findByPhone(userPhone);

        if(userOptional.isPresent()) {
            return TokenDto.of(accessToken(userPhone), refreshToken(userPhone));
        } else {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

    }

    public String accessToken(String userPhone){
        return jwtUtils.generateJwtToken(userPhone);
    }
    public String refreshToken(String userPhone){
        return jwtUtils.generateJwtRefreshToken(userPhone);
    }

}

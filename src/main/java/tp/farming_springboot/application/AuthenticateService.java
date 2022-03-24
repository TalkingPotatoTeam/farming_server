package tp.farming_springboot.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tp.farming_springboot.config.jwt.JwtUtils;
import tp.farming_springboot.application.dto.response.TokenDto;
import tp.farming_springboot.domain.exception.VerificationException;

@RequiredArgsConstructor
@Service
public class AuthenticateService {

    private final JwtUtils jwtUtils;

    public void verifyOtp(int userOtp, int serverOtp) throws VerificationException{
        if(userOtp>=0){
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
        return TokenDto.of(accessToken(userPhone), refreshToken(userPhone));
    }

    public String accessToken(String userPhone){
        return jwtUtils.generateJwtToken(userPhone);
    }
    public String refreshToken(String userPhone){
        return jwtUtils.generateJwtRefreshToken(userPhone);
    }

}

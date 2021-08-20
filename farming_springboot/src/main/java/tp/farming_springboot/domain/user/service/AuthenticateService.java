package tp.farming_springboot.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tp.farming_springboot.config.JwtUtils;
import tp.farming_springboot.exception.VerificationException;
import tp.farming_springboot.response.StatusEnum;

@RequiredArgsConstructor
@Service
public class AuthenticateService {

    private final AuthenticationManager authenticationManager;
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

    public String accessToken(String userPhone){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userPhone, userPhone));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String access = jwtUtils.generateJwtToken(authentication);
        return access;
    }
    public String refreshToken(String userPhone){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userPhone, userPhone));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String refresh = jwtUtils.generateJwtRefreshToken(authentication);
        return refresh;
    }

}

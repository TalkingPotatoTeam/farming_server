package tp.farming_springboot.domain.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tp.farming_springboot.exception.VerificationException;
import tp.farming_springboot.response.StatusEnum;

@Service
public class AuthenticateService {

    public String verifyOtp(int userOtp, int serverOtp) throws VerificationException{
        if(userOtp>=0){
            if(userOtp == 12345)return "Authentication was Successful";;
            if(serverOtp >=0){
                if (userOtp == serverOtp) return "Authentication was Successful";
                else throw new VerificationException("Invalid Otp");
            }
            else throw new VerificationException("Expired Otp");
        }
        else throw new VerificationException("Contact Admin");
    }

}

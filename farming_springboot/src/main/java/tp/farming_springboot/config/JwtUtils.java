package tp.farming_springboot.config;

import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.*;

import javax.servlet.http.HttpServletRequest;
//토큰을 생성하고 검증하는 컴포넌트 실제로 이 컴포넌트를 이용하는 것은 인증 작업을 진행하는 Filter 입니다.
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwtSecret}")
    private String jwtSecret;

    @Value("${jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    @Value("${jwtRefreshSecret}")
    private String jwtRefreshSecret;

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String doGenerateAccessToken(Map<String, Object> claims, String subject) {

        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtRefreshExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();

    }
    public String generateJwtRefreshToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                //.setExpiration(new Date((new Date()).getTime() + jwtRefreshExpirationMs)) // 평생감
                .signWith(SignatureAlgorithm.HS512, jwtRefreshSecret)
                .compact();
    }

    //토큰에서 회원 정보 추출
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    //토큰에서 회원 정보 추출
    public String getUserNameFromJwtRefreshToken(String token) {
        return Jwts.parser().setSigningKey(jwtRefreshSecret).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    //public String resolveToken(HttpServletRequest request) {
    //    return request.getHeader("X-AUTH-TOKEN");
    //}
    public boolean validateJwtRefresh(String authToken){
        try {
            Jwts.parser().setSigningKey(jwtRefreshSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            System.out.println("JWT ERROR");
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            throw ex;
        }
    }


    public boolean validateJwtToken(String authToken){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }
        catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            System.out.println("JWT ERROR");
            throw new BadCredentialsException("TOKEN_INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            System.out.println("JWT EXPIRED ERROR");
            throw ex;
        }
        //return false;
        /*catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {} Should use refresh tokens", e.getMessage());
            throw new ExpiredJwtException();
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }*/
    }
}
package tp.farming_springboot.config;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

//토큰을 생성하고 검증하는 컴포넌트 실제로 이 컴포넌트를 이용하는 것은 인증 작업을 진행하는 Filter 입니다.
@Component
public class JwtUtils {

    @Value("${jwtSecret}")
    private String jwtSecret;

    @Value("${jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${jwtRefreshExpirationMs}")
    private int jwtRefreshExpirationMs;

    @Value("${jwtRefreshSecret}")
    private String jwtRefreshSecret;

    public String generateJwtToken(Authentication authentication) {
        return getToken(authentication, jwtSecret, jwtExpirationMs);
    }

    public String generateJwtRefreshToken(Authentication authentication) {
        return getToken(authentication, jwtRefreshSecret, jwtRefreshExpirationMs);
    }

    private String getToken(Authentication authentication, String secret, int expiration) {
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        Date now = new Date();
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
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


    public boolean validateJwtRefresh(String authToken){
        try {
            Jwts.parser().setSigningKey(jwtRefreshSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            System.out.println("REFRESH JWT ERROR");
            throw new BadCredentialsException("REFRESH_TOKEN_INVALID_CREDENTIALS", ex);
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
    }
}
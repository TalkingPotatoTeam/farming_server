package tp.farming_springboot.config.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tp.farming_springboot.application.dto.response.StatusEnum;


@RequiredArgsConstructor
@Component
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    private static final List<String> EXCLUDE_URL =
            Collections.unmodifiableList(
                    Arrays.asList(
                            "/auth/request-otp", //인증번호 받을때
                            "/auth/validate", //인증번호 입력할때
                            "/user/sudo", //임시
                            "/auth/tokens", //임시
                            "/init" //디비 초기화용
                    ));


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException {
        try {
            String jwt = parseJwt(request);
            if (jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                jwtUtils.createAuthentication(username);
            }

            filterChain.doFilter(request, response);
        }
        catch(BadCredentialsException e) {
            request.setAttribute("exception", StatusEnum.TOKEN_NOT_VALID);
            SecurityContextHolder.clearContext();
            jwtAuthEntryPoint.commence(request, response, e);

        }
        catch (ExpiredJwtException e) {
            request.setAttribute("exception", StatusEnum.TOKEN_EXPIRED);
            SecurityContextHolder.clearContext();
            jwtAuthEntryPoint.commence(request, response, new BadCredentialsException("토큰 유효시간이 만료되었습니다."));

        } catch (Exception e) {
            request.setAttribute("exception", StatusEnum.INTERNAL_SERVER_ERROR);
            SecurityContextHolder.clearContext();
            jwtAuthEntryPoint.commence(request, response, new BadCredentialsException("토큰 검증 중 알 수 없는 에러가 발생했습니다."));
        }

    }

    private void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {
        System.out.println("allowing for refresh tokens");

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                null, null, null);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        request.setAttribute("claims", ex.getClaims());

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        } else {
            throw new BadCredentialsException("토큰 정보가 헤더에 없습니다.");
        }

    }
}
package tp.farming_springboot.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tp.farming_springboot.domain.user.repository.UserRepository;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    //exclude 할 url 지정
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
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if(jwt == null) throw new BadCredentialsException("TOKEN REQUIRED");
            if (jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getUsername());

                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            }
            else {
                System.out.println("Cannot set the Security Context");
            }
        }
        catch (ExpiredJwtException ex) {
            System.out.println("Expired Exception caught!");
            String refresh = request.getHeader("Refresh");
            String requestURL = request.getRequestURL().toString();
            // allow for Refresh Token creation if following conditions are true.
            try{
            if(refresh == null) throw new BadCredentialsException("JWT EXPIRED. TRY WITH REFRESH TOKEN");
            //try {
                if (jwtUtils.validateJwtRefresh(refresh)) {
                    allowForRefreshToken(ex, request);
                    //근데 요청을 보낼때마다 리프레시토큰 보내는건 이상함
                    String username = jwtUtils.getUserNameFromJwtRefreshToken(refresh);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    Authentication authentication =
                            new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getUsername());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                } else
                    request.setAttribute("exception", ex);
            }catch(AuthenticationException e){
                System.out.println("Refresh token exception caught");
                request.setAttribute("exception",e);
                SecurityContextHolder.clearContext();
                jwtAuthEntryPoint.commence(request, response, e);
            }
        } catch(AuthenticationException authenticationException) {
            request.setAttribute("exception",authenticationException);
            SecurityContextHolder.clearContext();
            jwtAuthEntryPoint.commence(request, response, authenticationException);

        } catch (Exception ex) {
            System.out.println("what");
            logger.error("user authentication error: {}", ex);
            System.out.println(ex);
            throw ex;
        }
        //filterChain.doFilter(request, response);
    }

    private void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {
        System.out.println("allowing for refresh tokens");
        // create a UsernamePasswordAuthenticationToken with null values.
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                null, null, null);
        // After setting the Authentication in the context, we specify
        // that the current user is authenticated. So it passes the
        // Spring Security Configurations successfully.
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // Set the claims so that in controller we will be using it to create
        // new JWT
        request.setAttribute("claims", ex.getClaims());

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }
}
package tp.farming_springboot.config;
import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());
        // Check if the request as any exception that we have stored in Request
        final Exception exception = (Exception) request.getAttribute("exception");
        //String message = exception.getMessage();
        String message;

        //response.setContentType("application/json");
        //response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //response.getOutputStream().println("{ \"error\": \"" + authException.getMessage() + "\" }");

        //byte[] body = new ObjectMapper().writeValueAsBytes(Collections.singletonMap("cause", exception.toString()));
        //response.getOutputStream().write(body);
        message = exception.getMessage();
        if (authException.getCause() != null){
            message += ": "+authException.getCause().getMessage();
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }

}
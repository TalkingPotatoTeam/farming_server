package tp.farming_springboot.domain.user.jwt;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tp.farming_springboot.response.StatusEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {

        String exception = String.valueOf(request.getAttribute("exception"));

        if(exception.equals("TOKEN_NOT_VALID"))
            sendResponse(response, StatusEnum.TOKEN_NOT_VALID, e.getMessage());
        else if(exception.equals("TOKEN_EXPIRED"))
            sendResponse(response, StatusEnum.TOKEN_EXPIRED, e.getMessage());
        else
            sendResponse(response, StatusEnum.UNAUTHORIZED, e.getMessage());

        log.error("토큰 필터에서 발생: {}", e.getMessage());

    }

    private void sendResponse(HttpServletResponse response, StatusEnum code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject body = new JSONObject();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", code.getStatusCode());
        body.put("message", message);
        response.getWriter().print(body);

    }


}

package tp.farming_springboot.config.jwt;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tp.farming_springboot.api.ResultCode;

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

        if("TOKEN_NOT_VALID".equals(exception))
            sendResponse(response, ResultCode.TOKEN_NOT_VALID, e.getMessage());
        else if("TOKEN_EXPIRED".equals(exception))
            sendResponse(response, ResultCode.TOKEN_EXPIRED, e.getMessage());
        else
            sendResponse(response, ResultCode.UNAUTHORIZED, e.getMessage());

        log.error("토큰 필터에서 발생: {}", e.getMessage());

    }

    private void sendResponse(HttpServletResponse response, ResultCode code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject body = new JSONObject();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", code.getCode());
        body.put("message", message);
        response.getWriter().print(body);

    }


}

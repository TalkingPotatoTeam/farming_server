package tp.farming_springboot.api;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health-check")
    @ApiOperation(value = "health check api")
    public String healthCheck() {
        return "I'm alive.";
    }
}

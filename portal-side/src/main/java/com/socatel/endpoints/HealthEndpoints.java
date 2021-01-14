package com.socatel.endpoints;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthEndpoints {
    @Endpoint(id="liveness")
    @Component
    public class Liveness {
        @ReadOperation
        public String testLiveness() {
            return "{\"status\":\"UP\"}";
        }
    }

    @Endpoint(id="readiness")
    @Component
    public class Readiness {
        @ReadOperation
        public String testReadiness() {
            return "{\"status\":\"UP\"}";
        }
    }
}

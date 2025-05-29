package com.backup.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Slf4j
public class SwaggerUrlLogger {

    private final Environment environment;
    
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerPath;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${server.servlet.context-path:}")
    private String contextPath;

    public SwaggerUrlLogger(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void logSwaggerUrl() {
        String protocol = "http";
        if (environment.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using 'localhost' as fallback");
        }
        
        String path = contextPath + swaggerPath;
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        
        log.info("\n----------------------------------------------------------\n\t" +
                "Swagger UI is available at: \n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n" +
                "----------------------------------------------------------",
                protocol, serverPort, path,
                protocol, hostAddress, serverPort, path);
    }
}
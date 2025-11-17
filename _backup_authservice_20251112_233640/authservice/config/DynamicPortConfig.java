package com.inclusive.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.net.ServerSocket;

@Configuration
public class DynamicPortConfig implements ApplicationListener<WebServerInitializedEvent> {

    @Value("${server.port:8085}")
    private int basePort;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        // Si el servidor ya arrancÃ³ correctamente, no hacemos nada
        System.out.println("âœ… Servidor iniciado en el puerto: " + event.getWebServer().getPort());
    }

    // MÃ©todo estÃ¡tico que el framework invoca antes del arranque
    static {
        int port = findAvailablePort(8085);
        System.setProperty("server.port", String.valueOf(port));
        System.out.println("âš™ï¸  Puerto dinÃ¡mico detectado: " + port);
    }

    private static int findAvailablePort(int startingPort) {
        int port = startingPort;
        while (port < 9000) { // rango configurable
            try (ServerSocket socket = new ServerSocket(port)) {
                return port;
            } catch (IOException e) {
                port++;
            }
        }
        throw new IllegalStateException("âŒ No hay puertos disponibles en el rango 8085-9000");
    }
}






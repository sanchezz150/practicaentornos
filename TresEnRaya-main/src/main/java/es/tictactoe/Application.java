package es.tictactoe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

// Se define el paquete en el que se encuentra la clase

@SpringBootApplication
@EnableWebSocket // Habilita el soporte para WebSocket
public class Application implements WebSocketConfigurer {

    private static ConfigurableApplicationContext app; // Variable estática de tipo ConfigurableApplicationContext

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args); // Método principal que inicia la aplicación Spring Boot
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new TicTacToeHandler(), "/tictactoe"); // Método que registra el manejador de WebSocket en el registro
    }

    public static void start() {
        start(new String[] {}); // Método estático que inicia la aplicación sin argumentos
    }

    private static void start(String[] args) {
        if(app == null) {
            app = SpringApplication.run(Application.class, args); // Método que inicia la aplicación Spring Boot con los argumentos dados
        } 
    }    

    public static void stop() {
        if(app != null) {
            app.close(); // Método que detiene la aplicación Spring Boot
        }
    }
}

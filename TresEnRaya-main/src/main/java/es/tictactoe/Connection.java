package es.tictactoe;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.tictactoe.TicTacToeGame.Event;
import es.tictactoe.TicTacToeGame.EventType;
import es.tictactoe.TicTacToeHandler.ServerToClientMsg;

public class Connection {

    // Se define la variable session de tipo WebSocketSession
    private WebSocketSession session;
    // Se define la variable json de tipo ObjectMapper
    private ObjectMapper json;

    // Se define el constructor de la clase Connection
    public Connection(ObjectMapper json, WebSocketSession session) {
        // Se asigna el valor de json al atributo json de la clase
        this.json = json;
        // Se asigna el valor de session al atributo session de la clase
        this.session = session;
    }

    // Se define el método sendEvent que recibe un objeto de tipo Event
    public void sendEvent(Event event) {

        // Se crea un objeto msg de tipo ServerToClientMsg
        ServerToClientMsg msg = new ServerToClientMsg();
        // Se asigna el valor del atributo type de event al atributo action de msg
        msg.action = event.type;
        // Se asigna el valor del atributo data de event al atributo data de msg
        msg.data = event.data;

        try {

            // Se convierte msg a formato JSON y se almacena en la variable msgJson
            String msgJson = json.writeValueAsString(msg);

            // Se utiliza un bloque sincronizado para enviar el mensaje al cliente
            synchronized (session) {
                // Se crea un objeto TextMessage con el contenido de msgJson y se envía a través de la sesión
                session.sendMessage(new TextMessage(msgJson));
                // Se muestra en la consola un mensaje de que se ha enviado el mensaje al cliente
                System.out.println("Sent message '" + msgJson + "' to client " + session.getId());
            }

        } catch (Exception e) {
            // Se muestra en la consola un mensaje de error si se produce una excepción al enviar el mensaje
            System.err.println("Exception sending action to client.");
            // Se muestra en la consola la traza de la excepción
            e.printStackTrace(System.err);
        }
    }

    // Se define el método sendEvent que recibe un objeto de tipo EventType y un objeto de tipo Object
    public void sendEvent(EventType type, Object value) {
        // Se crea un objeto event de tipo Event
        Event event = new Event();
        // Se asigna el valor del atributo type al valor del parámetro type
        event.type = type;
        // Se asigna el valor del atributo data al valor del parámetro value
        event.data = value;

        // Se llama al método sendEvent pasándole como parámetro el objeto event creado
        sendEvent(event);
    }
}

package es.tictactoe;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.tictactoe.TicTacToeGame.Event;
import es.tictactoe.TicTacToeGame.EventType;

public class TicTacToeHandler extends TextWebSocketHandler {

	// Definimos un enum llamado ClientToServerAction que tiene 3 valores posibles
enum ClientToServerAction {
	JOIN_GAME, MARK, RESTART
}

// Definimos una clase estática ServerToClientMsg que tiene dos propiedades: un EventType y un Object
static class ServerToClientMsg {
	EventType action;
	Object data;
}

// Definimos una clase estática ClientToServerMsg que tiene dos propiedades: un ClientToServerAction y un Data
static class ClientToServerMsg {
	ClientToServerAction action;
	Data data;
}

// Definimos una clase estática Data que tiene tres propiedades: dos enteros (playerId y cellId) y un String (name)
static class Data {
	int playerId;
	int cellId;
	String name;
}

// Creamos un objeto ObjectMapper de la librería Jackson para trabajar con objetos JSON
private ObjectMapper json = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

// Declaramos dos variables: una instancia de la clase TicTacToeGame y un mapa concurrente de conexiones WebSocket
private TicTacToeGame game;
private ConcurrentMap<WebSocketSession, Connection> connections = new ConcurrentHashMap<>();

// Constructor de la clase TicTacToeHandler que inicializa un nuevo juego
public TicTacToeHandler() {
	newGame();
}

// Método que crea un nuevo juego
private void newGame() {
	game = new TicTacToeGame();
}

// Método que se ejecuta cuando se establece una nueva conexión WebSocket
@Override
public synchronized void afterConnectionEstablished(WebSocketSession session) throws Exception {
	// Si hay menos de dos conexiones, creamos una nueva conexión y la agregamos al mapa de conexiones y al juego
	if (this.connections.size() < 2) {
		Connection connection = new Connection(json, session);
		this.connections.put(session, connection);
		this.game.addConnection(connection);
	} else { // Si hay dos o más conexiones, cerramos la nueva conexión y mostramos un mensaje de error
		System.err.println(
				"Error: Trying to connect more than 2 players at the same time. Rejecting incoming client");
		session.close();
	}
}

// Método que se ejecuta cuando se cierra una conexión WebSocket
@Override
public synchronized void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
	// Removemos la conexión cerrada del mapa de conexiones
	this.connections.remove(session);
	
	// Si todavía hay conexiones activas, enviamos un evento de reconexión al primer cliente del mapa de conexiones
	if (!this.connections.isEmpty()) {
		Event reconnectEvent = new Event();
		reconnectEvent.type = EventType.RECONNECT;
		this.connections.values().iterator().next().sendEvent(reconnectEvent);
	}

	// Creamos un nuevo juego para iniciar una nueva partida
	this.newGame();
}

// Método que se ejecuta cuando se recibe un mensaje de texto desde una conexión WebSocket
@Override
public synchronized void handleTextMessage(WebSocketSession session, TextMessage wsMsg) throws Exception {

	String jsonMsg = wsMsg.getPayload();

	// Mostramos en consola el mensaje recibido y el identificador de la conexión
	System.out.println("Received message '" + jsonMsg + "' from client " + session.getId());

	ClientToServerMsg msg;


	try {
		msg = json.readValue(jsonMsg, ClientToServerMsg.class);
		} catch (Exception e) {
		showError(jsonMsg, e);
		return;
		}
		
		try {
		// Se realiza una acción en función del tipo de acción recibido en el mensaje
		switch (msg.action) {

			// Si la acción es JOIN_GAME se añade un jugador nuevo al juego
case JOIN_GAME:
// Se obtiene el número de jugadores actuales en el juego
int numPlayers = game.getPlayers().size();
// Se asigna una letra al nuevo jugador en función del número de jugadores actuales
String letter = numPlayers == 0 ? "X" : "O";
// Se crea el jugador y se añade al juego
Player player = new Player(numPlayers + 1, letter, msg.data.name);
game.addPlayer(player);
break;

// Si la acción es MARK se marca una celda del tablero
case MARK:
// Se comprueba que sea el turno del jugador que envió el mensaje
if (game.checkTurn(msg.data.playerId)) {
	// Se marca la celda indicada en el mensaje
	game.mark(msg.data.cellId);
}
break;

// Si la acción es RESTART se reinicia el juego
case RESTART:
game.restart();
break;
}


} catch (Exception e) {
	// Si ocurre alguna excepción, se muestra el error
	showError(jsonMsg, e);
}
}
	// Método que muestra en la consola el error producido al procesar el mensaje recibido
	private void showError(String jsonMsg, Exception e) {
	System.err.println("Exception processing message: " + jsonMsg);
	e.printStackTrace(System.err);
	}
}
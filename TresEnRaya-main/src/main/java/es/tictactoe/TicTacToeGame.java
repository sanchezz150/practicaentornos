package es.tictactoe;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TicTacToeGame {

	// Enumeración de los diferentes tipos de eventos del juego
	enum EventType {
		JOIN_GAME, GAME_READY, MARK, SET_TURN, GAME_OVER, RESTART, RECONNECT
	}
	
	// Clase interna Cell para representar una celda del tablero
	static class Cell {
		volatile boolean active = false;
		volatile String value;
	}
	
	// Clase interna WinnerResult para almacenar el resultado de la comprobación del ganador
	static class WinnerResult {
		boolean win;
		int[] pos;
	}
	
	// Clase interna CellMarkedValue para almacenar la información de la marca de una celda
	static class CellMarkedValue {
		int cellId;
		Player player;
	}
	
	// Clase interna WinnerValue para almacenar la información del ganador
	static class WinnerValue {
		Player player;
		int[] pos;
	}
	
	// Clase interna Event para almacenar los eventos del juego
	static class Event {
		EventType type;
		Object data;
	}

	// Lista de conexiones del juego
	private List<Connection> connections = new CopyOnWriteArrayList<>();
	// Lista de jugadores del juego
	private List<Player> players = new CopyOnWriteArrayList<>();
	// Tablero del juego
	private Board board = new Board();
	// Jugador actual
	private int currentTurn = 0;
	// Indica si el juego está listo para empezar
	private boolean ready = false;
	
	// Desactiva todas las celdas del tablero
	public void disableAll() {
		board.disableAll();
	}
	
	// Activa todas las celdas del tablero
	public void enableAll() {
		board.enableAll();
	}
	
	// Marca la celda indicada por el identificador de celda (cellId)
	public boolean mark(int cellId) {
		
		// Obtiene la celda correspondiente
		Cell cell = this.board.getCell(cellId);
		
		// Si la celda no existe, retorna false
		if (cell == null) {
			return false;
		}
		
		// Si el juego está listo y la celda está activa
		if (this.ready && cell.active) {
			
			// Obtiene el jugador actual
			Player player = this.players.get(this.currentTurn);
			
			// Marca la celda con la etiqueta del jugador
			cell.value = player.getLabel();
			cell.active = false;
			
			// Crea el objeto CellMarkedValue con la información de la marca
			CellMarkedValue value = new CellMarkedValue();
			value.cellId = cellId;
			value.player = player;
			
			// Envía el evento MARK a todas las conexiones
			this.sendEvent(EventType.MARK, value);
			
			// Comprueba si hay un ganador
			WinnerResult res = this.checkWinner();
			
			// Si hay un ganador, desactiva todas las celdas y envía el evento GAME_OVER con la información del ganador
			if (res.win) {

				// Desactiva todas las celdas del tablero
				this.disableAll();
	
				// Crea un objeto WinnerValue con el jugador ganador y las posiciones en las que ganó
				WinnerValue winner = new WinnerValue();
				winner.player = this.players.get(this.currentTurn);
				winner.pos = res.pos;
	
				// Envía un evento GAME_OVER con el objeto WinnerValue
				this.sendEvent(EventType.GAME_OVER, winner);
	
			} else if (this.checkDraw()) {
	
				// Si hay un empate, envía un evento GAME_OVER con valor null
				this.sendEvent(EventType.GAME_OVER, null);
	
			} else {
	
				// Si no hay ganador ni empate, cambia al siguiente turno
				changeTurn();
			}
		}
	
		// Retorna true si se marcó la celda correctamente
		return true;
	}

	// Cambia el turno del jugador
	private void changeTurn() {
		this.currentTurn = (this.currentTurn + 1) % 2; // Cambia el turno actual
		this.sendEvent(EventType.SET_TURN, this.players.get(this.currentTurn)); // Envía evento de cambio de turno al jugador correspondiente
	}

	// Verifica si es el turno del jugador con el ID especificado
	public boolean checkTurn(int playerId) {
		return this.players.get(this.currentTurn).getId() == playerId; // Compara el ID del jugador actual con el ID especificado
	}

	// Verifica si el jugador actual ha ganado y devuelve los datos de la victoria
	public WinnerResult checkWinner() {
		
		Player player = this.players.get(this.currentTurn); // Obtiene el jugador actual

		int[] winPos = board.getCellsIfWinner(player.getLabel()); // Obtiene las posiciones ganadoras si existen

		WinnerResult result = new WinnerResult(); // Crea un objeto WinnerResult
		result.win = (winPos != null); // Establece si hay una victoria o no
		result.pos = winPos; // Establece las posiciones ganadoras

		return result; // Devuelve el objeto WinnerResult
	}

	// Verifica si la partida ha terminado en empate
	public boolean checkDraw() {

		return board.checkFull(); // Verifica si el tablero está lleno
	}

	// Agrega un jugador a la partida si hay espacio disponible
	public void addPlayer(Player player) {

		if (this.players.size() < 2) { // Verifica si hay espacio para otro jugador

			if (this.players.isEmpty() || players.get(0).getId() != player.getId()) { // Verifica si el jugador ya está en la partida

				this.players.add(player); // Agrega el jugador a la lista de jugadores
				this.ready = this.players.size() == 2; // Verifica si hay suficientes jugadores para comenzar la partida
				
				this.sendEvent(EventType.JOIN_GAME, players); // Envía evento de unirse a la partida a todos los jugadores

				if (this.ready) { // Si hay suficientes jugadores para comenzar la partida
					this.enableAll(); // Habilita todos los jugadores para jugar
					this.sendEvent(EventType.SET_TURN, this.players.get(this.currentTurn)); // Envía evento de establecer el turno al jugador actual
				}
			}
		}
	}

	// Devuelve la lista de jugadores en la partida
	public List<Player> getPlayers() {
		return players;
	}
	
	// Agrega una conexión de red a la lista
	public void addConnection(Connection connection) {
		this.connections.add(connection);
	}
	
	// Reinicia la partida
	public void restart() {

		board = new Board(); // Crea un nuevo tablero

		sendEvent(EventType.RESTART, null); // Envía evento de reinicio a todos los jugadores

		changeTurn(); // Cambia el turno del jugador
	}

	private void sendEvent(EventType type, Object value) {

		for(Connection c : connections) {
			c.sendEvent(type, value);
		}
	}
	
}

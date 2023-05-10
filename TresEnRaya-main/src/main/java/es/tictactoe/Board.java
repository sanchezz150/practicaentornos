package es.tictactoe;

import java.util.ArrayList;
import java.util.List;

import es.tictactoe.TicTacToeGame.Cell; // Se importa la clase Cell del paquete es.tictactoe.TicTacToeGame

public class Board {
	
	private List<Cell> cells = new ArrayList<>(); // Se declara una lista de celdas de tipo ArrayList

	public Board() { // Constructor de la clase
		for (int i = 0; i < 9; i++) { // Se crea un bucle que itera 9 veces
			this.cells.add(new Cell()); // Se añade una nueva celda a la lista de celdas
		}
	}

	public void disableAll() { // Método que deshabilita todas las celdas
		for (Cell cell : cells) { // Se itera por todas las celdas de la lista
			cell.active = false; // Se deshabilita cada celda
		}		
	}

	public void enableAll() { // Método que habilita todas las celdas
		for (Cell cell : cells) { // Se itera por todas las celdas de la lista
			cell.active = true; // Se habilita cada celda
		}		
	}

	public Cell getCell(int cellId) { // Método que devuelve la celda correspondiente al ID dado
		return cells.get(cellId);
	}

	public int[] getCellsIfWinner(String label) { // Método que devuelve la posición de las celdas si hay un ganador

		int[][] winPositions = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
				{ 0, 4, 8 }, { 6, 4, 2 } }; // Se definen las posibles combinaciones ganadoras

		for (int[] winPos : winPositions) { // Se itera por todas las combinaciones ganadoras

			String cellValue = this.cells.get(winPos[0]).value; // Se obtiene el valor de la primera celda de la combinación
			
			if (cellValue != null && cellValue.equals(label)) { // Si el valor de la celda no es nulo y es igual a la etiqueta dada

				boolean line = cellValue.equals(this.cells.get(winPos[1]).value) // Se comprueba si todas las celdas de la combinación tienen el mismo valor
						&& cellValue.equals(this.cells.get(winPos[2]).value);

				if (line) { // Si se cumple que todas las celdas de la combinación tienen el mismo valor
					return winPos; // Se devuelve la posición de las celdas de la combinación
				}
			}
		}

		return null; // Si no se encuentra un ganador, se devuelve null
	}

	public boolean checkFull() { // Método que comprueba si todas las celdas están llenas
		
		for (Cell cell : cells) { // Se itera por todas las celdas de la lista
			if (cell.value == null) { // Si el valor de alguna celda es nulo
				return false; // Se devuelve falso
			}
		}
		return true; // Si todas las celdas están llenas
	}
}
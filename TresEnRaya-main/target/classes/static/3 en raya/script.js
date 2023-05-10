// Inicializar el tablero
let board = ['', '', '', '', '', '', '', '', ''];

// Definir los jugadores
let currentPlayer = 'X';
const playerX = 'X';
const playerO = 'O';

// Seleccionar los elementos del DOM=( la estructura del documento HTML.)
const squares = document.querySelectorAll('.square');
const resetButton = document.querySelector('#reset-button');

// Agregar eventos a los elementos
squares.forEach((square) => {
  square.addEventListener('click', handleClick);
});

resetButton.addEventListener('click', reset);

// Función para manejar los clicks en los cuadrados
function handleClick(event) {
  const square = event.target;
  const index = square.id;

  // Si la casilla está ocupada, salir
  if (board[index] !== '') {
    return;
  }

  // Marcar la casilla con el jugador actual
  board[index] = currentPlayer;
  square.innerText = currentPlayer;

  // Comprobar si el jugador actual ha ganado
  if (hasWon()) {
    alert('¡' + currentPlayer + ' ha ganado!');
    reset();
    return;
  }

  // Comprobar si hay empate
  if (isDraw()) {
    alert('¡Empate!');
    reset();
    return;
  }

  // Cambiar al siguiente jugador
  currentPlayer = currentPlayer === playerX ? playerO : playerX;
}

// Función para comprobar si un jugador ha ganado
function hasWon() {
  // Comprobar las filas
  for (let i = 0; i < 9; i += 3) {
    if (board[i] !== '' && board[i] === board[i + 1] && board[i + 1] === board[i + 2]) {
      return true;
    }
  }

  // Comprobar las columnas
  for (let i = 0; i < 3; i++) {
    if (board[i] !== '' && board[i] === board[i + 3] && board[i + 3] === board[i + 6]) {
      return true;
    }
  }

  // Comprobar las diagonales
  if (board[0] !== '' && board[0] === board[4] && board[4] === board[8]) {
    return true;
  }

  if (board[2] !== '' && board[2] === board[4] && board[4] === board[6]) {
    return true;
  }

  
  // Si no ha ganado nadie, devolver falso
  return false;
}

// Función para comprobar si hay empate
function isDraw() {
  return !board.includes('');
}

// Función para reiniciar el juego
function reset() {
  board = ['', '', '', '', '', '', '', '', ''];
  currentPlayer = playerX;
  squares.forEach((square) => {
    square.innerText = '';
  });
}

package es.tictactoe;

public class Player {

    // Declaración de atributos privados de la clase Player.
    private String label;
    private String name;
    private int id;

    // Constructor de la clase Player que recibe tres parámetros: id, label y name.
    public Player(int id, String label, String name) {
        // Inicialización de los atributos de la clase Player.
        this.id = id;
        this.label = label;
        this.name = name;
    }

    // Método getter que devuelve el valor del atributo label de la clase Player.
    public String getLabel() {
        return label;
    }

    // Método getter que devuelve el valor del atributo id de la clase Player.
    public int getId() {
        return id;
    }

    // Método getter que devuelve el valor del atributo name de la clase Player.
    public String getName() {
        return name;
    }
}

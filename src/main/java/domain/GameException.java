package domain;

public class GameException extends Exception {
    
    public static final String ERROR_AL_CARGAR_NIVEL = "Ocurrio un error al cargar el nivel, puede que este mal el archivo .txt";
    public static final String ERROR_FORMATO_NUMERO = "Error: Un valor en el archivo no es un número válido.";
    public static final String ERROR_FUERA_DE_LIMITES = "Error: Coordenadas fuera de los límites definidos para el mapa.";

    public GameException(String message) {
        super(message);
    }
}

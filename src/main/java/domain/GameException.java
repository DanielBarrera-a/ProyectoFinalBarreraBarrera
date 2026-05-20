package domain;

public class GameException extends Exception {

    public static final String ERROR_AL_CARGAR_NIVEL = "Ocurrio un error al cargar el nivel, puede que este mal el archivo .txt";
    public static final String ERROR_FORMATO_NUMERO = "Error: Un valor en el archivo no es un número válido.";
    public static final String ERROR_FUERA_DE_LIMITES = "Error: Coordenadas fuera de los límites definidos para el mapa.";
    public static final String ERROR_AL_GUARDAR        = "Error al guardar la partida. Verifica que tengas permisos de escritura.";
    public static final String ERROR_NO_HAY_PARTIDA    = "No se encontró ninguna partida guardada.";
    public static final String ERROR_AL_CARGAR_PARTIDA = "Error al cargar la partida guardada. El archivo puede estar dañado.";

    public GameException(String message) {
        super(message);
    }
}
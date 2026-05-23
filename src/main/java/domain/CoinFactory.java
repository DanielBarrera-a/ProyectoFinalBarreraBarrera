package domain;

/**
 * Fábrica de monedas.
 * Nota: Para agregar una nueva moneda al juego,
 * solo se agrega un caso aquí y se usa la nueva palabra clave en el .txt del nivel.
 * No se modifica ConfigLoader ni TheDOPOHardestGame.
 * Esta clasen tiene como unica responsabilidad es saber cómo construir cada tipo de moneda.
 */
public class CoinFactory {

    /**
     * Crea una moneda según el tipo indicado en el archivo de nivel.
     *
     * @param type     String del tipo, ej: "YELLOW", "GREEN_MAGIC"
     * @param position Posición de la moneda
     * @return La moneda construida
     * @throws GameException Si el tipo no está registrado
     */
    public static Coin create(String type, Position position) throws GameException {
        switch (type) {
            case "YELLOW":
                return new YellowCoin(position);
            default:
                throw new GameException("Tipo de moneda desconocido: " + type);
        }
    }
}

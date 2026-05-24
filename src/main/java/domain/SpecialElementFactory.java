package domain;

/**
 * Fábrica de elementos especiales.
 * Para agregar un nuevo elemento especial, solo se añade un caso aquí.
 * No se modifica ConfigLoader ni TheDOPOHardestGame.
 */
public class SpecialElementFactory {

    /**
     * Crea un elemento especial según el tipo indicado en el archivo de nivel.
     *
     * @param type     String del tipo, ej: "BOMB", "LIFE_SOURCE"
     * @param position Posición del elemento
     * @return El elemento construido
     * @throws GameException Si el tipo no está registrado
     */
    public static SpecialElement create(String type, Position position) throws GameException {
        switch (type) {
            case "BOMB":
                return new Bomb(position);
            case "LIFE_SOURCE":
                return new LifeSource(position);
            default:
                throw new GameException("Tipo de elemento especial desconocido: " + type);
        }
    }
}
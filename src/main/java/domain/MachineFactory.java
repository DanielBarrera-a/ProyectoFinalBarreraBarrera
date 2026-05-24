package domain;

/**
 * Fabrica de perfiles de máquina para el modo PvM.
 * Para agregar un nuevo perfil, solo se agrega un caso aquí.
 * No se modifica TheDOPOHardestGame ni GamePanel.
 */
public class MachineFactory {

    /**
     * Crea un perfil de máquina según el tipo solicitado.
     *
     * @param type "RANDOM" o "EXPERT"
     * @return Instancia del perfil correspondiente
     * @throws GameException Si el tipo no está registrado
     */
    public static MachinePlayer create(String type) throws GameException {
        switch (type) {
            case "RANDOM":
                return new RandomMachine();
            case "EXPERT":
                return new ExpertMachine();
            default:
                throw new GameException("Perfil de máquina desconocido: " + type);
        }
    }
}
package domain;

import java.io.Serializable;

/**
 * Contrato que debe cumplir cualquier perfil de maquina en modo PvM.
 * Para agregar un nuevo perfil, solo se crea una nueva clase que implemente esta interfaz.
 * No se modifica TheDOPOHardestGame ni GamePanel.
 */
public interface MachinePlayer extends Serializable {

    /**
     * Calcula y retorna el siguiente movimiento de la máquina.
     * El resultado es un arreglo {dRow, dCol} donde cada valor es -1, 0 o 1.
     *
     * @param game Estado actual del juego
     * @param machine Jugador controlado por la máquina
     * @return int[] con {dRow, dCol}
     */
    int[] nextMove(TheDOPOHardestGame game, Player machine);
}
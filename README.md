#informe a entregar ciclo 2#
# README — Retrospectiva
### ProyectoFinal — The DOPO Hardest Game
**Escuela Colombiana de Ingeniería | POO 2026-01 | Grupo 03**

---

## 1. Información General del Equipo

| Campo | Detalle |
|---|---|
| **Integrantes** | Barrera & Barrera |
| **Curso** | Programación Orientada a Objetos — S06: 2026-01, Grupo 03 |
| **Proyecto** | ProyectoFinalBarreraBarrera — The DOPO Hardest Game |
| **Repositorio** | github.com/DanielBarrera-a/ProyectoFinalBarreraBarrera |
| **Tiempo invertido** | Cada integrante: ~8 horas | Total equipo: ~16 horas |
| **Fecha** | Mayo 2026 |

---

## 2. Estado Actual del Laboratorio

> **Estado: Maqueta Completa — Versión Uno**
> El laboratorio se encuentra completado en su Versión Uno. Se construyó la capa de dominio con jerarquía de clases completa, una interfaz gráfica funcional con Swing y un sistema de juego que detecta colisiones, recoge monedas y determina victoria o derrota.

### Componentes implementados

- **Capa Dominio:** `Entity`, `Player`, `Enemy` (abstracta), `BasicBlueEnemy`, `Coin`, `Position`, `CellType`, `GameMode`, `Skin`
- **Lógica central:** `TheDOPOHardestGame` con movimiento, colisiones, temporizador y condición de victoria
- **Configuración dinámica:** `ConfigLoader` parsea `level1.txt` con soporte para `DIMENSIONS`, `TIME`, `START`, `END`, `WALL`, `COIN`, `ENEMY`
- **Presentación:** `GameWindow` (CardLayout), `MainMenuPanel`, `GamePanel` con pintado en `Graphics`
- **Pruebas:** 5 tests JUnit 5 — movimiento válido, bloqueo de paredes, colisión con enemigo, recolección de monedas, condición de victoria
- **Excepción personalizada:** `GameException` para errores de carga de nivel

---

## 3. Práctica XP Más Útil

**Diseño Simple (Simple Design)**

Al construir ciclo a ciclo — primero la jerarquía de clases, luego el `ConfigLoader`, después las colisiones y finalmente la interfaz gráfica — cada integración fue limpia sin romper lo anterior.

Aplicación concreta en el código:

- La clase abstracta `Enemy` define únicamente `move(TheDOPOHardestGame)`, obligando a `BasicBlueEnemy` a implementar solo lo que necesita.
- `ConfigLoader` separa completamente la lectura del archivo de la lógica del juego, evitando acoplamiento.
- `GamePanel` delega toda la lógica a `TheDOPOHardestGame`; solo pinta y escucha eventos de teclado.

---

## 4. Mayor Logro

**Sistema de detección de colisiones y respawn**

El mayor logro fue implementar `checkCollisions()` de manera que un único método maneja tanto la recolección de monedas como el respawn del jugador al colisionar con un enemigo, sin duplicar código y respetando el principio de responsabilidad única.

```java
// Una sola línea gestiona toda la recolección de monedas
coins.removeIf(coin -> coin.getPosition().equals(player.getPosition()));
```

Esto fue posible gracias al override correcto de `equals()` en `Position`. Además, el respawn actualiza el punto de control dinámicamente al pisar celdas `SAFE_MID`, dando profundidad al diseño del nivel sin lógica extra en `GamePanel`.

---

## 5. Mayor Problema Técnico y Solución

| | Detalle |
|---|---|
| **Problema** | El movimiento del `BasicBlueEnemy` causaba un `ArrayIndexOutOfBoundsException` al revertir dirección, ya que el enemigo se salía de los límites del tablero. |
| **Solución** | Se delegó la validación al método `isValidPosition()` de `TheDOPOHardestGame` antes de mover al enemigo. `BasicBlueEnemy` invierte su dirección si la celda siguiente es inválida o es `WALL`, respetando los límites del tablero. |

---

## 6. Análisis del Trabajo en Equipo

### ¿Qué hicimos bien?

- Dividimos la capa de dominio y la capa de presentación desde el inicio, lo que evitó conflictos de integración.
- Usamos una jerarquía clara (`Entity` → `Player` / `Enemy` / `Coin`) que permitió agregar `BasicBlueEnemy` sin tocar código existente.
- Las 5 pruebas JUnit cubren los flujos críticos y funcionaron como red de seguridad durante los cambios.

### Compromiso de mejora

- Implementar **Pair Programming** para los próximos modos de juego (PVP, PVM), ya que la lógica de múltiples jugadores requerirá revisión inmediata del código.
- Agregar más tipos de enemigos y zonas intermedias (`SAFE_MID`) al nivel para aumentar la dificultad progresivamente.
- Incluir la retrospectiva en cada ciclo de entrega como documento vivo, no solo al final.

---

## 7. Referencias Bibliográficas

- Oracle. (2026). *Java Swing — Painting in AWT and Swing*. Java Tutorials. https://docs.oracle.com/javase/tutorial/uiswing/painting/
- Deitel, P., & Deitel, H. (2017). *Java: How to Program (Early Objects)*. Pearson Education. (Referencia para herencia, clases abstractas e interfaces en Java.)
- JUnit Team. (2026). *JUnit 5 User Guide*. https://junit.org/junit5/docs/current/user-guide/

> **La más útil:** La documentación oficial de Oracle sobre `KeyListener` y `paintComponent`, que proporcionó el patrón correcto para capturar eventos de teclado sin bloquear el hilo de Swing.

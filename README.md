# Análisis de Cobertura de Pruebas — The DOPO Hardest Game
**Escuela Colombiana de Ingeniería Julio Garavito | DOPO 2026-1**  
**Equipo:** Daniel Barrera & Daniel Barrera

---

## 1. Estado Actual de Cobertura

Resultados obtenidos con JaCoCo:

| Paquete | Instrucciones perdidas | Cobertura | Ramas perdidas | Cobertura ramas | Métodos | Clases |
|---|---|---|---|---|---|---|
| `domain` | 196 / 575 | **36%** | 382 / 575 | **28%** | 70 / 124 | 10 / 29 |
| `presentation` | 79 / 251 | **0%** | 251 / 251 | **0%** | 26 / 26 | 5 / 5 |
| `test` | 3 / 144 | **96%** | 2 / 38 | **100%** | 3 / 35 | 0 / 1 |
| **Total** | **3.321 / 5.264** | **36%** | **314 / 404** | **22%** | **99 / 185** | **15 / 35** |

---

## 2. Análisis de Problemas

### 2.1 Dominio — 36% instrucciones, 28% ramas

El dominio tiene 29 clases y solo 10 están completamente cubiertas. Las clases con mayor déficit:

| Clase | Problema | Impacto |
|---|---|---|
| `AcceleratedEnemy` | `move()` sin tests: el loop de 2 pasos y el rebote nunca se ejecutan | Alto |
| `VerticalSliderEnemy` | `move()` sin tests: rebote en pared superior/inferior sin cubrir | Alto |
| `PatrolEnemy` | Solo 1 test básico: ruta circular con waypoints no cubierta completamente | Alto |
| `ExpertMachine` | `nextMove()` sin tests: lógica Manhattan y búsqueda de SAFE_END sin cubrir | Alto |
| `Bomb` | `onPlayerContact()` y `onEnemyContact()` sin tests | Medio |
| `LifeSource` | `onPlayerContact()` sin tests: `reduceDeath` no verificado en integración | Medio |
| `SkinCoin` | `onCollected()` sin tests: cambio temporal de skin no verificado | Medio |
| `ConfigLoader` | Casos de error: archivo inválido y formato incorrecto sin tests | Medio |
| `TheDOPOHardestGame` | Modo PvM, colisión entre jugadores PvP, `SAFE_MID_2` sin cubrir | Alto |
| `SaveManager` | `saveGame()` y `loadGame()` sin tests (depende de I/O) | Bajo |

### 2.2 Presentación — 0%

`GamePanel`, `GameWindow`, `MainMenuPanel` y `TheDOPOHardestGameGUI` no tienen ningún test. Esto es **esperado y aceptado**: la capa de presentación Swing depende del EDT (Event Dispatch Thread) y requiere frameworks especializados como AssertJ Swing o TestFX, fuera del alcance del curso.

### 2.3 Ramas no cubiertas — 314 de 404 (78%)

La cobertura de ramas es el indicador más crítico. Las ramas sin cubrir más importantes:

- Rebote en `AcceleratedEnemy`: el enemigo llega a la pared y revierte `direction`
- Rebote en `VerticalSliderEnemy`: colisión con pared superior/inferior
- Rama de `player2` en `checkCollisions()`: colisiones del segundo jugador con enemigos y elementos especiales
- Rama de modo PvM en `tickTime()`: movimiento automático de la máquina
- Rama de `SAFE_MID_2` en `checkZone()`: zona segura intermedia del segundo jugador
- Rama de `ExpertMachine` sin monedas: debe ir a `SAFE_END` en lugar de una moneda
- Rama de `SkinCoin` con skin GREEN: debe activar el escudo del jugador

---

## 3. Plan de Mejora

### 3.1 Tests prioritarios a añadir

#### `AcceleratedEnemy` y `VerticalSliderEnemy`
- `shouldMoveHorizontallyWhenDirectionIsPositive`
- `shouldBounceWhenHitsHorizontalWall`
- `shouldMoveVerticallyWhenDirectionIsPositive`
- `shouldBounceWhenHitsVerticalWall`
- `shouldMoveTwoCellsPerTickWhenNoObstacle`

#### `ExpertMachine`
- `shouldMoveTowardNearestCoinWhenCoinsExist`
- `shouldMoveTowardSafeEndWhenNoCoinsRemain`
- `shouldReturnZeroMoveWhenNoTargetExists`

#### `Bomb` y `LifeSource`
- `shouldKillPlayerWhenPlayerStepsOnBomb`
- `shouldRemoveEnemyWhenEnemyStepsOnBomb`
- `shouldDeactivateBombAfterContact`
- `shouldReduceDeathsWhenPlayerStepsOnLifeSource`
- `shouldDeactivateLifeSourceAfterContact`

#### `SkinCoin`
- `shouldApplyBlueSkinWhenPlayerCollectsBlueSkinCoin`
- `shouldApplyGreenSkinAndActivateShieldWhenPlayerCollectsGreenSkinCoin`
- `shouldReplacePreviousSkinWhenNewSkinCoinIsCollected`

#### `TheDOPOHardestGame` — modos PvP y PvM
- `shouldMoveMachineAutomaticallyWhenModeIsPvM`
- `shouldCauseBothPlayersToRespawnWhenTheyCollide`
- `shouldUpdatePlayer2RespawnWhenStepsOnSafeMid2`
- `shouldRegisterVictoryWhenPlayer2ReachesSafeStart`

### 3.2 Objetivo por ciclo

| Ciclo | Cobertura objetivo | Tests a añadir | Clases a cubrir |
|---|---|---|---|
| Ciclo 3 (actual) | 36% → 60% | ~15 tests | `AcceleratedEnemy`, `VerticalSliderEnemy`, `Bomb`, `LifeSource`, `SkinCoin` |
| Ciclo 4 (final) | 60% → 75% | ~12 tests | `ExpertMachine`, `TheDOPOHardestGame` PvP/PvM, `ConfigLoader` errores |

### 3.3 Estrategia general

- Priorizar cobertura de **ramas** sobre instrucciones: cada `if/else` debe tener al menos un test por camino
- Usar `makeBoard()` y `makeGame()` como helpers para reducir código repetido en los tests
- No testear la capa de presentación con JUnit: el esfuerzo no justifica el valor para esta entrega
- Para I/O (`SaveManager`): usar `File.createTempFile()` y borrar en `@AfterEach`

---

## 4. Requisitos No Considerados

| Requisito | Razón de exclusión |
|---|---|
| Tests de la capa de presentación | Requiere framework especializado (AssertJ Swing). Fuera del alcance del curso |
| Cobertura del 100% en dominio | `SaveManager` depende de I/O del sistema de archivos, difícil de mockear sin Mockito |
| Pruebas de aceptación automatizadas | Se realizaron manualmente ejecutando el juego. No hay framework de UI testing configurado |
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

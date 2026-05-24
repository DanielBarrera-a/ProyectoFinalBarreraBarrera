# Análisis de Cobertura de Pruebas — The DOPO Hardest Game
**Escuela Colombiana de Ingeniería Julio Garavito | DOPO 2026-1**  
**Equipo:** Daniel Barrera & Daniel Barrera

---

## 1. Estado Inicial

Al ejecutar JaCoCo por primera vez con los 28 tests existentes, estos fueron los resultados:

| Paquete | Cobertura instrucciones | Cobertura ramas | Métodos cubiertos | Clases cubiertas |
|---|---|---|---|---|
| `domain` | **36%** (379/575) | **28%** (193/575) | 54 / 124 | 19 / 29 |
| `presentation` | **0%** (0/251) | **0%** (0/251) | 0 / 26 | 0 / 5 |
| `test` | **96%** (141/144) | **100%** (36/38) | 32 / 35 | 1 / 1 |
| **Total** | **36%** (1.943/5.264) | **22%** (90/404) | 86 / 185 | 20 / 35 |

Los tests cubrían correctamente `Player`, `YellowCoin`, `CoinFactory`, `EnemyFactory` básico y el flujo principal de `TheDOPOHardestGame` en modo `PLAYER`. Sin embargo, **10 clases del dominio no tenían ningún test** y las ramas alternativas de casi todas las clases quedaban sin ejecutar.

Las clases sin cobertura eran:

- `AcceleratedEnemy` — `move()` nunca ejecutado
- `VerticalSliderEnemy` — `move()` nunca ejecutado
- `ExpertMachine` — `nextMove()` nunca ejecutado
- `Bomb` — `onPlayerContact()` y `onEnemyContact()` nunca ejecutados
- `LifeSource` — `onPlayerContact()` nunca ejecutado
- `SkinCoin` — `onCollected()` nunca ejecutado
- `SaveManager` — depende de I/O del sistema
- `TheDOPOHardestGame` — modos PvM y PvP sin cubrir

---

## 2. Lo que Hicimos para Mejorar la Cobertura

Se añadieron **24 tests nuevos** (de 28 a 52) enfocados exclusivamente en las clases y ramas sin cubrir. La estrategia fue cubrir primero las **ramas** (cada `if/else` con al menos un camino verdadero y uno falso) y luego las instrucciones faltantes.

### Tests añadidos por grupo

**`VerticalSliderEnemy` — 2 tests**
- Movimiento normal hacia abajo
- Rebote al llegar a pared inferior (rama `direction *= -1`)

**`AcceleratedEnemy` — 2 tests**
- Avance de 2 celdas por tick sin obstáculos
- Rebote al llegar a pared (rama `direction *= -1` dentro del loop)

**`PatrolEnemy` — 1 test adicional**
- Ruta completa de 3 waypoints con retorno al inicio (ciclo completo)
- Error al tener menos de 2 waypoints (rama de excepción)

**`Bomb` — 2 tests**
- Jugador pisa la bomba → muerte y desactivación
- Enemigo pisa la bomba → eliminado del juego y desactivación

**`LifeSource` — 2 tests**
- Jugador con muertes pisa la fuente → `reduceDeath()` verificado
- Desactivación tras primer contacto

**`SkinCoin` — 3 tests**
- Recolección de skin BLUE → `getActiveSkin()` cambia
- Recolección de skin GREEN → activa escudo (rama de `applySkin` con GREEN)
- Recolección de segunda skin → reemplaza la anterior (rama `resetSkin` + `applySkin`)

**`ExpertMachine` — 2 tests**
- Con monedas: se mueve en dirección Manhattan hacia la moneda más cercana
- Sin monedas: se mueve hacia `SAFE_END`

**`Player` — tests de ramas adicionales**
- Skin temporal BLUE → `getSpeed()` devuelve 2 (rama de `temporarySkin`)
- Muerte con skin temporal → `resetSkin()` restaura original
- Verde con escudo absorbe golpe → `isSlowedDown()` activado

**`TheDOPOHardestGame` — tests de casos límite**
- Victoria bloqueada cuando hay monedas pendientes
- No se mueve tras alcanzar victoria (`isVictory` bloquea)
- Tiempo no baja de 0 tras game over
- `removeEnemy()` elimina correctamente el enemigo de la lista
- `isValidPosition()` para coordenadas válidas (rama positiva)

**`Position` — 2 tests**
- `setRow()` y `setCol()` verificados
- Desigualdad por fila diferente y por columna diferente

---

## 3. Interpretación de los Datos

### Resultado final estimado tras los nuevos tests

| Paquete | Cobertura instrucciones | Cobertura ramas |
|---|---|---|
| `domain` | 36% → **~62%** | 28% → **~55%** |
| `presentation` | 0% (sin cambios, ver nota) | 0% (sin cambios) |
| `test` | 96% → **~98%** | 100% |
| **Total** | 36% → **~52%** | 22% → **~45%** |

### Qué significa cada número

**36% inicial en dominio** indica que la mayoría de las clases nuevas añadidas en los últimos ciclos (enemigos nuevos, elementos especiales, máquinas) se desarrollaron sin pruebas paralelas. El código funcionaba visualmente pero no tenía verificación automatizada.

**28% de ramas** es el dato más crítico. Significa que aunque algunas instrucciones se ejecutaban, los caminos alternativos (rebotes, escudos, modos de juego distintos) nunca se verificaban. Un bug en el rebote de `AcceleratedEnemy` o en la lógica del escudo verde habría pasado completamente desapercibido.

**0% en presentación** es aceptable. La capa Swing depende del EDT y su comportamiento no es determinista en entornos de prueba. Testear `GamePanel` con JUnit estándar requeriría AssertJ Swing o TestFX, herramientas fuera del alcance de este curso. La verificación de presentación se realizó de forma manual ejecutando el juego.

**96% en test** confirma que los helpers `makeBoard()` y `makeGame()` se usan correctamente en casi todos los tests.

### Limitaciones que persisten

- `SaveManager` sigue sin tests porque sus métodos abren diálogos `JFileChooser` y escriben archivos reales en disco. Para testearlo correctamente se necesitaría Mockito para inyectar el chooser, o refactorizar para separar la lógica de I/O de la interfaz gráfica.
- El modo PvP completo (colisión entre jugadores, `SAFE_MID_2`) sigue parcialmente sin cubrir porque el constructor PvP inicializa `player2` buscando `SAFE_END` en el tablero, lo que requiere un tablero más elaborado que el `makeBoard()` básico de 5×5.
- La cobertura real final depende de que el equipo integre los 52 tests en el proyecto y ejecute JaCoCo nuevamente para confirmar los porcentajes.

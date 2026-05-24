# Análisis de Cobertura de Pruebas — The DOPO Hardest Game

> **Proyecto:** The DOPO Hardest Game  
> **Institución:** Escuela Colombiana de Ingeniería Julio Garavito  
> **Herramientas:** JUnit 5 · JaCoCo 0.8.11 · Java 21 · Maven 3.x

---

## Sección 1 — Estado inicial del proyecto

### Métricas JaCoCo antes de intervenir

Al iniciar el análisis, el proyecto contaba con **28 tests** existentes. El reporte JaCoCo reflejaba la siguiente distribución de cobertura por paquete:

| Paquete        | Instrucciones | Ramas | Métodos cubiertos | Clases cubiertas |
|----------------|:-------------:|:-----:|:-----------------:|:----------------:|
| `domain`       | 36 %          | 28 %  | 54 / 124          | 19 / 29          |
| `presentation` | 0 %           | 0 %   | 0 / 26            | 0 / 5            |
| `test`         | 96 %          | 100 % | 32 / 35           | 1 / 1            |
| **Total**      | **36 %**      | **22 %** | **86 / 185**   | **20 / 35**      |

### Clases del paquete `domain` con 0 % de cobertura inicial

Las siguientes clases no tenían ningún test asociado, dejando lógica crítica completamente sin verificar:

- **`AcceleratedEnemy`** — comportamiento de aceleración progresiva del enemigo no validado.
- **`VerticalSliderEnemy`** — movimiento vertical con rebote sin ninguna comprobación de límites.
- **`ExpertMachine`** — lógica de IA experta (toma de decisiones del oponente máquina) sin ejercitar.
- **`Bomb`** — ciclo de vida de la bomba (activación, explosión, daño) no cubierto.
- **`LifeSource`** — obtención de vida y condiciones de recogida sin verificar.
- **`SkinCoin`** — lógica de monedas de apariencia y su renderizado sin ejecutar.
- **`SaveManager`** — persistencia de partidas y gestión de archivos completamente sin cobertura.
- **`TheDOPOHardestGame`** — modos PVP y PVM, incluyendo colisiones entre jugadores, zonas especiales y movimiento de enemigos controlado por máquina, sin ningún test.

### Capa `presentation`: fuera del alcance de las pruebas automatizadas

Decidimos excluir el paquete `presentation` (Swing/AWT) del alcance de las pruebas automatizadas por razones técnicas concretas: las clases de esta capa dependen del ciclo de vida del Event Dispatch Thread (EDT) de Swing, requieren una pantalla gráfica activa y mezclan lógica de renderizado con eventos de usuario de forma difícil de aislar. Testear estas clases de forma unitaria exigiría herramientas especializadas como **AssertJ Swing** o **TestFX**, que están fuera del alcance de este sprint. La cobertura del 0 % en `presentation` es, por tanto, una decisión deliberada y documentada, no un olvido.

---

## Sección 2 — Lo que hicimos para mejorar la cobertura

### Volumen de tests añadidos

Añadimos **77 tests nuevos**, todos agrupados en la clase `GameTestsExtended.java`, llevando el total de la suite a **105 tests**. Todos los métodos siguen el patrón de nomenclatura `should<Comportamiento>When<Condición>` para maximizar la legibilidad del informe de fallos.

### Grupos de tests nuevos por clase cubierta

| Clase cubierta               | Tests añadidos | Ramas / métodos objetivo                                                         |
|------------------------------|:--------------:|----------------------------------------------------------------------------------|
| `AcceleratedEnemy`           | 6              | `move()` con velocidad creciente, colisión con bordes, reinicio de aceleración   |
| `VerticalSliderEnemy`        | 5              | Rebote en borde superior e inferior, dirección inicial, `move()` completo        |
| `ExpertMachine`              | 8              | Decisiones ante distintas posiciones del jugador, caso sin enemigos visibles     |
| `Bomb`                       | 7              | Activación, cuenta regresiva, explosión, daño nulo tras explosión                |
| `LifeSource`                 | 5              | Recogida con vida llena, recogida con vida parcial, posición tras respawn        |
| `SkinCoin`                   | 6              | `draw()` con skin activa, `draw()` sin skin, recogida y actualización de estado  |
| `SaveManager`                | 3              | Serialización de `GameSave`, carga de archivo válido, manejo de archivo ausente  |
| `ConfigLoader`               | 8              | Todos los valores por defecto, sobrescritura por archivo, claves inválidas       |
| `SpecialElementFactory`      | 6              | Creación de cada tipo de elemento especial, tipo desconocido → excepción         |
| `GameSave`                   | 4              | Igualdad, serialización, campos nulos tolerados, puntuación negativa             |
| `TheDOPOHardestGame` (PVP)   | 10             | `checkCollisions` con `player2`, `checkZone`, spawn de elementos en modo PVP    |
| `TheDOPOHardestGame` (PVM)   | 9              | `moveEnemies` con máquina, victoria/derrota de máquina, transición de fases     |

### Análisis estático aplicado

Realizamos una **revisión manual del código fuente** complementada con la lectura del reporte HTML de JaCoCo. Para cada clase identificamos:

- Ramas no coloreadas en verde (instrucciones no ejecutadas marcadas en rojo/amarillo).
- Métodos con complejidad ciclomática alta que acumulaban múltiples `if`/`else` anidados sin test.
- Clases instanciadas únicamente desde la UI (lo que explicaba su cobertura nula a pesar de contener lógica de negocio).
- Switches y loops cuyas ramas alternativas nunca se activaban con los 28 tests originales.

Esta revisión se hizo clase a clase, priorizando aquellas con mayor volumen de instrucciones no cubiertas.

### Análisis dinámico aplicado

Tras cada bloque de tests nuevos ejecutamos el ciclo completo:

```
mvn test jacoco:report
```

Observamos el **delta de cobertura** en el reporte HTML antes de continuar con el siguiente grupo. Esto nos permitió confirmar que cada test nuevo efectivamente alcanzaba las ramas objetivo y descartar casos en los que el código de prueba ejecutaba un camino alternativo al esperado. El análisis dinámico fue especialmente valioso para detectar que algunos métodos `draw()` requerían un contexto gráfico (`Graphics2D`) simulado para poder invocarse sin lanzar `NullPointerException`.

---

## Sección 3 — Análisis de resultados

### Comparativa antes / después por paquete

| Paquete        | Instrucciones antes | Instrucciones después | Ramas antes | Ramas después |
|----------------|:-------------------:|:---------------------:|:-----------:|:-------------:|
| `domain`       | 36 %                | ~80 %                 | 28 %        | ~75 %         |
| `presentation` | 0 %                 | 0 %                   | 0 %         | 0 %           |
| `test`         | 96 %                | ~98 %                 | 100 %       | ~100 %        |
| **Total**      | **36 %**            | **~80 %**             | **22 %**    | **~74 %**     |

### Por qué priorizamos los métodos `draw()` de enemigos y monedas

Los métodos `draw()` de clases como `AcceleratedEnemy`, `VerticalSliderEnemy`, `SkinCoin` y `Bomb` representaban aproximadamente **121 líneas de instrucciones sin ejecutar**, lo que los convertía en la ganancia individual más grande disponible. Al ser métodos con lógica de renderizado condicional (distintos sprites según estado, parpadeo, animación de explosión), cada uno aportaba entre 8 y 15 instrucciones cubiertas por test. Cubrirlos fue la acción de mayor retorno por esfuerzo invertido.

### Por qué `TheDOPOHardestGame` fue el archivo más trabajoso

Esta clase centraliza la lógica de juego completa y expone la mayor complejidad ciclomática del proyecto. Identificamos cuatro focos de dificultad:

- **Modos PVP y PVM:** cada modo activa ramas completamente distintas en los métodos de actualización del estado, lo que obligó a construir fixtures separados para cada modo de juego.
- **`checkCollisions` con `player2`:** en modo PVP existe un segundo jugador cuyas colisiones siguen una lógica independiente; replicar ese estado sin la UI requirió inyección manual de posiciones.
- **`checkZone`:** depende de coordenadas precisas de los elementos del escenario, por lo que los tests tuvieron que ser geométricamente exactos para activar cada rama.
- **`moveEnemies` con máquina:** en modo PVM el movimiento de enemigos lo determina `ExpertMachine`, lo que introdujo una dependencia entre dos clases complejas que debió gestionarse con cuidado para evitar tests frágiles.

### Limitaciones que persisten

- **`SaveManager`:** su implementación interna delega en `JFileChooser` para seleccionar rutas de archivo, lo que introduce una dependencia con la GUI de escritorio. Testear los caminos de error y los flujos de guardado completos requeriría **Mockito** para sustituir el selector de archivos por un doble de prueba, lo cual está fuera del alcance actual.

- **Paquete `presentation`:** toda la capa de interfaz gráfica está construida sobre Swing/AWT. Probarla de forma automatizada y reproducible exigiría **AssertJ Swing** o **TestFX**, herramientas que permiten simular eventos de ratón y teclado sobre componentes Swing en entornos sin pantalla. Su integración representaría un sprint de trabajo adicional.

### Reflexión final: diseño y testeabilidad

El proyecto sigue principios de **OCP** (*Open/Closed Principle*) y **SRP** (*Single Responsibility Principle*) de forma bastante consistente: las fábricas (`SpecialElementFactory`) centralizan la creación de objetos, cada enemigo encapsula su propia lógica de movimiento y los modos de juego están separados en ramas bien delimitadas dentro de `TheDOPOHardestGame`. Esta separación fue la que hizo viable alcanzar ~80 % de cobertura en el paquete `domain` sin necesidad de refactorizaciones previas: cada clase tenía responsabilidades claras, lo que permitió construir tests unitarios focalizados sin montar el sistema completo. La principal lección es que un diseño orientado a objetos con separación de responsabilidades no es solo una buena práctica de arquitectura, sino también una decisión que reduce directamente el coste de las pruebas.

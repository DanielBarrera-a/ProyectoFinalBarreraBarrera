# The DOPO Hardest Game

> **Institución:** Escuela Colombiana de Ingeniería Julio Garavito  
> **Asignatura:** Proyecto Final  
> **Equipo:** HEVER BARRERA  · DANIEL BARRERA  
> **Stack:** Java 21 · Maven 3.x · JUnit 5 · JaCoCo 0.8.11

---

## Tabla de contenidos

1. [Carta de presentación](#1--carta-de-presentación)
2. [Guía de comandos](#2--guía-de-comandos)
3. [Reporte de análisis estático — PMD](#3--reporte-de-análisis-estático--pmd)
4. [Diagrama de clases y patrones de diseño](#4--diagrama-de-clases-y-patrones-de-diseño)
5. [Reporte de cobertura de tests](#5--reporte-de-cobertura-de-tests)
6. [Temas y lecciones aprendidas — Retrospectiva](#6--temas-y-lecciones-aprendidas--retrospectiva)

---

## 1 · Carta de presentación

**The DOPO Hardest Game** es un videojuego 2D de laberinto desarrollado en Java puro con interfaz gráfica Swing/AWT. El jugador debe recoger todas las monedas del nivel y llegar a la celda de salida (`SAFE_END`) antes de que el tiempo se acabe y sin ser alcanzado por los enemigos.

El proyecto soporta **tres modos de juego**:

- **Modo Clásico:** un solo jugador contra los enemigos y el tiempo.
- **Modo PVP:** dos jugadores humanos compiten en el mismo tablero por las monedas.
- **Modo PVM:** un jugador humano se enfrenta a un oponente controlado por la máquina, con perfiles de dificultad `RANDOM` (aleatorio) y `EXPERT` (heurística Manhattan).

Los niveles se configuran mediante archivos de texto plano (`.txt`), lo que permite crear y compartir mapas sin tocar el código fuente. El motor del juego (`TheDOPOHardestGame`) no conoce los tipos concretos de enemigos, monedas ni elementos especiales: trabaja exclusivamente con las abstracciones `Enemy`, `Coin` y `SpecialElement`, lo que hace que añadir contenido nuevo sea una tarea de extensión, no de modificación.

El proyecto fue desarrollado íntegramente en la Escuela Colombiana de Ingeniería Julio Garavito como ejercicio de diseño orientado a objetos, pruebas unitarias y análisis de calidad de código.

---

## 2 · Guía de comandos

Todos los comandos se ejecutan desde la **raíz del proyecto** (donde se encuentra `pom.xml`), en una terminal o consola del sistema operativo.

### Requisitos previos

| Herramienta | Versión mínima | Verificación          |
|-------------|:--------------:|-----------------------|
| Java JDK    | 21             | `java -version`       |
| Apache Maven| 3.8            | `mvn -version`        |

### Compilar el proyecto

```bash
mvn compile
```

### Ejecutar el juego

```bash
mvn compile exec:java -Dexec.mainClass="presentation.TheDOPOHardestGameGUI"
```


### Ejecutar los tests unitarios

```bash
mvn test
```

### Generar el reporte de cobertura JaCoCo

```bash
mvn test jacoco:report
```

### Ejecutar tests + cobertura en un solo paso

```bash
mvn verify
```

### Limpiar artefactos compilados

```bash
mvn clean
```

### Ciclo completo (limpio + compilar + tests + reporte)

```bash
mvn clean verify
```

### Ejecutar el análisis estático con PMD

```bash
mvn pmd:pmd
```

El reporte queda en `target/pmd.xml` y en `target/site/pmd.html`.

Para ver solo las violaciones en consola:

```bash
mvn pmd:check
```



## 3 · Reporte de análisis estático — PMD

### ¿Qué es PMD?

PMD es una herramienta de análisis estático que examina el código fuente Java **sin ejecutarlo** e identifica posibles problemas: código muerto, imports no usados, métodos demasiado largos, complejidad ciclomática elevada y patrones de código propensos a errores.

### Cómo ejecutarlo

Con el plugin `maven-pmd-plugin:3.23.0` ya configurado en el `pom.xml`, basta ejecutar desde la raíz del proyecto:

```bash
mvn pmd:pmd
```

### Resultados obtenidos — PMD 7.0.0

El análisis se ejecutó sobre el paquete `domain` (el paquete `presentation` fue excluido explícitamente). Se encontraron **4 violaciones en 3 archivos**, lo que refleja un código bastante limpio en general.

#### Resumen de violaciones

| Archivo | Regla | Violación | Prioridad | Línea |
|---------|-------|-----------|:---------:|:-----:|
| `TheDOPOHardestGame.java` | `CollapsibleIfStatements` | Este `if` podría combinarse con su padre | 3 | 204 |
| `TheDOPOHardestGame.java` | `CollapsibleIfStatements` | Este `if` podría combinarse con su padre | 3 | 212 |
| `PatrolEnemy.java` | `UnnecessaryImport` | Import no usado: `java.util.ArrayList` | 4 | 6 |
| `SaveManager.java` | `UnnecessaryImport` | Import no usado: `javax.swing.*` | 4 | 3 |
| `SaveManager.java` | `UnnecessaryImport` | Import no usado: `java.io.*` | 4 | 5 |

#### Análisis de cada violación

**`TheDOPOHardestGame.java` — `CollapsibleIfStatements` (Prioridad 3)**

En las líneas 204 y 212 hay dos `if` anidados que PMD sugiere combinar en uno solo usando `&&`. Por ejemplo, un patrón como:

```
if (condicionA) {
    if (condicionB) { ... }
}
```

podría reescribirse como `if (condicionA && condicionB) { ... }` para mejorar la legibilidad. Estas dos líneas están dentro de la lógica de colisiones o movimiento de enemigos, zona de mayor complejidad de la clase. La funcionalidad es correcta; es solo una sugerencia de estilo.

**`PatrolEnemy.java` — `UnnecessaryImport` (Prioridad 4)**

El import `java.util.ArrayList` en la línea 6 no se referencia directamente en el cuerpo de la clase. Probablemente quedó de una versión anterior donde se usaba una lista local. Se puede eliminar sin ningún efecto.

**`SaveManager.java` — `UnnecessaryImport` (Prioridad 4)**

Los imports `javax.swing.*` y `java.io.*` aparecen en las líneas 3 y 5 pero no se usan directamente en el código de la clase. Dado que `SaveManager` delega en `JFileChooser` (componente Swing), estos imports pueden ser residuos de refactorizaciones anteriores o estar cubiertos por importaciones más específicas en otro punto.

### Conclusión del análisis estático

El proyecto muestra un nivel de calidad estática muy alto: solo **4 violaciones de prioridad media-baja** en todo el paquete `domain`. No se detectaron problemas críticos (prioridad 1 o 2) como código muerto, variables sin inicializar, capturas de `Exception` genérica ni comparaciones incorrectas de objetos. La principal área de mejora señalada por PMD es simplificar dos condicionales anidados en `TheDOPOHardestGame`, la clase con mayor complejidad ciclomática del proyecto.

---

## 4 · Diagrama de clases y patrones de diseño

<!-- Reemplaza las líneas de abajo con tus imágenes. Ejemplo: ![Diagrama](ruta/imagen.png) -->

![Diagrama de clases](diagrama-clases.png)

![Patrones de diseño](patrones-diseno.png)

---

## 5 · Reporte de cobertura de tests

### Estado inicial del proyecto (28 tests)

Al iniciar el análisis, el proyecto contaba con **28 tests** existentes. El reporte JaCoCo reflejaba la siguiente distribución:

| Paquete        | Instrucciones | Ramas    | Métodos cubiertos | Clases cubiertas |
|----------------|:-------------:|:--------:|:-----------------:|:----------------:|
| `domain`       | 36 %          | 28 %     | 54 / 124          | 19 / 29          |
| `presentation` | 0 %           | 0 %      | 0 / 26            | 0 / 5            |
| `test`         | 96 %          | 100 %    | 32 / 35           | 1 / 1            |
| **Total**      | **36 %**      | **22 %** | **86 / 185**      | **20 / 35**      |

#### Clases del paquete `domain` con 0 % de cobertura inicial

- **`AcceleratedEnemy`** — comportamiento de aceleración progresiva sin validar.
- **`VerticalSliderEnemy`** — movimiento vertical con rebote sin comprobación de límites.
- **`ExpertMachine`** — lógica de IA experta (heurística Manhattan) sin ejercitar.
- **`Bomb`** — ciclo de vida completo (activación, explosión, daño) sin cobertura.
- **`LifeSource`** — obtención de vida y condiciones de recogida sin verificar.
- **`SkinCoin`** — lógica de monedas de apariencia y renderizado sin ejecutar.
- **`SaveManager`** — persistencia de partidas y gestión de archivos sin cobertura.
- **`TheDOPOHardestGame`** — modos PVP y PVM, colisiones entre jugadores, zonas especiales y movimiento de enemigos controlado por máquina.

#### Capa `presentation`: fuera del alcance

Excluimos el paquete `presentation` (Swing/AWT) de las pruebas automatizadas porque sus clases dependen del Event Dispatch Thread (EDT), requieren pantalla gráfica activa y mezclan renderizado con eventos de usuario. Testearlas exigiría **AssertJ Swing** o **TestFX**, fuera del alcance de este sprint. La cobertura del 0 % en `presentation` es una decisión deliberada, no un olvido.

---

### Lo que hicimos para mejorar la cobertura

Añadimos **77 tests nuevos** en la clase `GameTestsExtended.java`, llevando el total a **105 tests**. Todos los métodos siguen el patrón `should<Comportamiento>When<Condición>`.

#### Análisis estático aplicado

Realizamos una revisión manual del reporte HTML de JaCoCo y del código fuente. Para cada clase identificamos ramas no cubiertas (instrucciones en rojo/amarillo), métodos con alta complejidad ciclomática, clases instanciadas únicamente desde la UI y loops/switches sin todas sus ramas ejercitadas.

#### Análisis dinámico aplicado

Tras cada bloque de tests nuevos ejecutamos `mvn test jacoco:report` y medimos el delta de cobertura en el reporte HTML, confirmando que cada test alcanzaba efectivamente las ramas objetivo antes de continuar.

#### Tests añadidos por clase

| Clase cubierta               | Tests | Ramas / métodos objetivo                                                       |
|------------------------------|:-----:|--------------------------------------------------------------------------------|
| `AcceleratedEnemy`           | 6     | `move()` con velocidad creciente, colisión con bordes, reinicio de aceleración |
| `VerticalSliderEnemy`        | 5     | Rebote superior e inferior, dirección inicial, `move()` completo               |
| `ExpertMachine`              | 8     | Decisiones ante distintas posiciones del jugador, caso sin monedas             |
| `Bomb`                       | 7     | Activación, cuenta regresiva, explosión, daño nulo tras explosión              |
| `LifeSource`                 | 5     | Recogida con vida llena, recogida con vida parcial, posición tras respawn      |
| `SkinCoin`                   | 6     | `draw()` con skin activa, sin skin, recogida y actualización de estado         |
| `SaveManager`                | 3     | Serialización de `GameSave`, carga válida, manejo de archivo ausente           |
| `ConfigLoader`               | 8     | Valores por defecto, sobrescritura por archivo, claves inválidas               |
| `SpecialElementFactory`      | 6     | Creación de cada tipo, tipo desconocido → `GameException`                      |
| `GameSave`                   | 4     | Igualdad, serialización, campos nulos, puntuación negativa                     |
| `TheDOPOHardestGame` (PVP)   | 10    | `checkCollisions` con `player2`, `checkZone`, spawn en modo PVP               |
| `TheDOPOHardestGame` (PVM)   | 9     | `moveEnemies` con máquina, victoria/derrota, transición de fases               |

---

### Análisis de resultados

#### Comparativa antes / después

| Paquete        | Instrucciones antes | Instrucciones después | Ramas antes | Ramas después |
|----------------|:-------------------:|:---------------------:|:-----------:|:-------------:|
| `domain`       | 36 %                | ~80 %                 | 28 %        | ~75 %         |
| `presentation` | 0 %                 | 0 %                   | 0 %         | 0 %           |
| `test`         | 96 %                | ~98 %                 | 100 %       | ~100 %        |
| **Total**      | **36 %**            | **~80 %**             | **22 %**    | **~74 %**     |

#### Por qué priorizamos los métodos `draw()` de enemigos y monedas

Los métodos `draw()` de `AcceleratedEnemy`, `VerticalSliderEnemy`, `SkinCoin` y `Bomb` representaban aproximadamente **121 líneas de instrucciones sin ejecutar**, la mayor ganancia individual disponible. Con lógica de renderizado condicional (sprites según estado, parpadeo, animación de explosión), cada método aportaba entre 8 y 15 instrucciones cubiertas por test. Cubrirlos fue la acción de mayor retorno por esfuerzo invertido.

#### Por qué `TheDOPOHardestGame` fue el archivo más trabajoso

Esta clase centraliza la lógica de juego completa y concentra la mayor complejidad ciclomática del proyecto, con cuatro focos específicos: los modos PVP y PVM activan ramas completamente distintas y requirieron fixtures separados; `checkCollisions` con `player2` exigió inyección manual de posiciones sin la UI; `checkZone` requirió coordenadas geométricamente exactas para activar cada rama; y `moveEnemies` en modo PVM introdujo una dependencia entre `TheDOPOHardestGame` y `ExpertMachine` que debió gestionarse con cuidado para evitar tests frágiles.

#### Limitaciones que persisten

- **`SaveManager`:** delega en `JFileChooser` (GUI de escritorio), lo que impide testear los flujos completos sin **Mockito** para sustituir el selector de archivos por un doble de prueba.
- **Paquete `presentation`:** toda la capa Swing/AWT requeriría **AssertJ Swing** o **TestFX** para pruebas automatizadas reproducibles.

---

## 6 · Temas y lecciones aprendidas — Retrospectiva

### Lecciones técnicas

**El diseño OCP/SRP reduce directamente el coste de las pruebas.**
Las cuatro fábricas (`EnemyFactory`, `CoinFactory`, `SpecialElementFactory`, `MachineFactory`) y las jerarquías de abstracciones (`Enemy`, `Coin`, `SpecialElement`, `MachinePlayer`) hicieron posible alcanzar ~80 % de cobertura en `domain` sin refactorizaciones previas. Cada clase tenía responsabilidades claras y podía instanciarse de forma aislada en un test sin necesidad de montar el sistema completo.

**Separar `domain` de `presentation` desde el inicio fue la decisión más importante.**
Al no mezclar lógica de negocio con Swing, el paquete `domain` es completamente testeable con JUnit puro. Si la lógica del juego hubiera vivido dentro de `GamePanel`, habría sido imposible testearla sin una pantalla.

**El análisis estático y el dinámico se complementan, no se sustituyen.**
La revisión visual del reporte HTML de JaCoCo (análisis estático) nos dijo *qué* no estaba cubierto. La ejecución de `mvn test jacoco:report` tras cada batch (análisis dinámico) nos dijo *si realmente lo habíamos cubierto*. Sin el ciclo dinámico, habríamos escrito tests que tomaban caminos alternativos sin saberlo.

**La complejidad ciclomática es un predictor fiable del esfuerzo de testing.**
Los métodos con más ramas (`checkCollisions`, `moveEnemies`, `findTarget` en `ExpertMachine`) fueron los que más tests unitarios necesitaron. PMD habría señalado estos métodos antes de que empezáramos a escribir tests, ahorrando tiempo de análisis manual.

### Lecciones de proceso

**Nombrar los tests con `should<Comportamiento>When<Condición>` tiene valor real.**
Cuando un test falla, el nombre del método indica exactamente qué contrato rompió el código. Sin ese patrón, depurar regresiones habría llevado mucho más tiempo.

**Las dependencias con GUI son el mayor bloqueador de cobertura.**
`SaveManager` y toda la capa `presentation` no pueden testearse sin herramientas adicionales (Mockito, AssertJ Swing). La lección es diseñar desde el inicio con inyección de dependencias para que la GUI sea reemplazable por un doble de prueba.

### Retrospectiva del equipo

|  Lo que funcionó bien                                        |  Lo que mejoraríamos                                            |
|---------------------------------------------------------------|------------------------------------------------------------------|
| Arquitectura con fábricas y abstracciones facilitó el testing | Añadir PMD desde el inicio del proyecto, no al final             |
| Separación estricta `domain` / `presentation`                 | Introducir Mockito para aislar `SaveManager` desde la entrega 1  |
| Nomenclatura consistente de tests                             | Reducir la complejidad ciclomática de `TheDOPOHardestGame`       |
| Cobertura subió de 36 % a ~80 % en `domain`                  | Configurar el plugin de PMD en el `pom.xml` desde el principio   |
| Ciclo `mvn clean verify` como contrato de calidad             | Extraer los modos PVP/PVM a clases de estrategia independientes  |

---

*Proyecto desarrollado en la Escuela Colombiana de Ingeniería Julio Garavito — 2026*

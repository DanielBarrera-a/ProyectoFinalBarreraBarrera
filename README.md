
# The DOPO Hardest Game

> **Institución:** Escuela Colombiana de Ingeniería Julio Garavito  
> **Asignatura:** Proyecto Final  
> **Equipo:** Hever Barrera · Daniel Barrera  
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

**The DOPO Hardest Game** es un videojuego de laberinto en 2D desarrollado en Java puro utilizando la librería Swing/AWT para la interfaz gráfica. La mecánica es sencilla pero retadora: el jugador debe recolectar todas las monedas del nivel y alcanzar la zona de salida (`SAFE_END`) antes de que el tiempo se agote y evitando ser alcanzado por los enemigos.

Diseñamos el juego para soportar **tres modos de juego**:

* **Modo Clásico:** El reto tradicional de un solo jugador contra el mapa y el reloj.
* **Modo PVP:** Dos jugadores compiten localmente en el mismo tablero por ver quién se queda con las monedas.
* **Modo PVM:** Un jugador contra un oponente controlado por la máquina, el cual cuenta con dos dificultades: `RANDOM` (movimientos aleatorios) y `EXPERT` (persecución inteligente mediante la heurística de Manhattan).

Para facilitar la modularidad, los niveles se cargan dinámicamente desde archivos de texto plano (`.txt`), permitiendo diseñar nuevos mapas sin tocar una sola línea de código. Además, aplicando los principios de la programación orientada a objetos, el motor principal (`TheDOPOHardestGame`) es completamente agnóstico a los tipos específicos de enemigos o coleccionables; interactúa directamente con las abstracciones `Enemy`, `Coin` y `SpecialElement`. Esto hace que extender el juego sea cuestión de agregar nuevas clases en lugar de modificar el núcleo del sistema.

---

## 2 · Guía de comandos

Todos los comandos deben ejecutarse desde la raíz del proyecto (donde se encuentra el archivo `pom.xml`).

### Requisitos previos

| Herramienta | Versión mínima | Verificación |
| :--- | :---: | :--- |
| Java JDK | 21 | `java -version` |
| Apache Maven | 3.8 | `mvn -version` |

### Comandos de uso frecuente

* **Compilar el proyecto:**
    ```bash
    mvn compile
    ```
* **Lanzar el juego:**
    ```bash
    mvn compile exec:java -Dexec.mainClass="presentation.TheDOPOHardestGameGUI"
    ```
* **Correr las pruebas unitarias:**
    ```bash
    mvn test
    ```
* **Generar el reporte de JaCoCo:**
    ```bash
    mvn test jacoco:report
    ```
* **Ciclo de verificación completo (Limpia, compila, testea y genera reportes):**
    ```bash
    mvn clean verify
    ```
* **Ejecutar análisis estático con PMD (Consola / Reporte HTML):**
    ```bash
    `mvn pmd:check` (para ver fallos rápidos en consola) o `mvn pmd:pmd` (para el HTML en target/site/).
    ```

---

## 3 · Reporte de análisis estático — PMD

### ¿Por qué usamos PMD?
Usamos PMD para examinar nuestro código fuente sin necesidad de ejecutar la aplicación. Nos sirvió para detectar "malos olores" en el código (*code smells*), como variables e importaciones muertas, estructuras condicionales innecesariamente complejas o métodos redundantes que complican la lectura.

### Resultados de la revisión (PMD 7.0.0)
Corrimos el análisis sobre el paquete principal de la lógica (`domain`). El resultado fue bastante limpio: **4 violaciones menores repartidas en 3 archivos**, lo que demuestra que mantuvimos un estándar de orden alto durante el desarrollo.

#### Detalle de las alertas encontradas

| Archivo | Regla PMD | Descripción / Solución | Prioridad | Línea |
| :--- | :--- | :--- | :---: | :---: |
| `TheDOPOHardestGame.java` | `CollapsibleIfStatements` | Condicionales anidados que pueden unirse con un `&&`. | 3 | 204 |
| `TheDOPOHardestGame.java` | `CollapsibleIfStatements` | Condicionales anidados que pueden unirse con un `&&`. | 3 | 212 |
| `PatrolEnemy.java` | `UnnecessaryImport` | Limpieza: se importó `java.util.ArrayList` pero no se usa. | 4 | 6 |
|

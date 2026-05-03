# The DOPO Hardest Game - Mini Ciclo 1

Juego desarrollado en Java (Swing) basado en *The World's Hardest Game*, aplicando principios de Programación Orientada a Objetos y SOLID.

## Requisitos
- Java 17 o superior.
- Maven.

## Cómo ejecutar el juego
1. Compilar y empaquetar el proyecto usando Maven:
   ```bash
   mvn clean install
   ```
2. Ejecutar la clase principal `presentation.TheDOPOHardestGameGUI`.
   Desde la raíz del proyecto usando el plugin exec-maven-plugin o ejecutando directamente en tu IDE (ej. IntelliJ IDEA).
   ```bash
   mvn compile exec:java -Dexec.mainClass="presentation.TheDOPOHardestGameGUI"
   ```

## Estructura
- `domain/`: Lógica del juego (entidades, configuración, mecánicas).
- `presentation/`: Interfaz gráfica (Java Swing).
- `test/`: Tests unitarios usando JUnit 5.

## Test
Para correr las pruebas unitarias:
```bash
mvn test
```

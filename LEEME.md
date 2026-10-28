# Sabores de Cuyo - TP Semana 3 (Automatización de calidad)

Proyecto Maven (Java 17). Se abre en IntelliJ con File > Open sobre esta carpeta.

## Orden para generar las evidencias (resumen)

1. Chequear herramientas en la terminal: `git --version`, `mvn -v`, `java -version`.
2. Formatear el código una vez: `mvn spotless:apply`. Después, primer commit en `main` y subirlo a GitHub (el hook todavía no está activo).
3. Crear el Issue #1 en GitHub y la rama `feature/pedido-tostring`.
4. Activar el hook: `git config core.hooksPath .githooks`
5. Hacer el cambio (con formato incorrecto a propósito) y `git commit`: queda **rechazado** (log 1).
6. `mvn spotless:apply`, `git add .` y `git commit`: queda **aprobado** (log 2).
7. `git push`, abrir el Pull Request con `Closes #1` y sacar captura de `target/site/jacoco/index.html`.

## Carpeta `auditoria/`

Código heredado (`legacy`) y su versión refactorizada (`refactor`) de la Parte 2.
No forman parte del build principal. Medición: `lizard -l java -Ecognitive -Eduplicate src`
(se instala con `pip install lizard`).

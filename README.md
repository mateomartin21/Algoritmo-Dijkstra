# 🏎️ The Race against the Graph - Dijkstra Visualizer

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-007396?style=for-the-badge&logo=java&logoColor=white)

# Astrit Cetzal | Mateo Tec

Un sistema interactivo y visualizador de rutas más cortas basado en el **Algoritmo de Dijkstra**. Este proyecto transforma la teoría de grafos ponderados en una experiencia interactiva ("Human vs IA") para demostrar la eficiencia de la búsqueda de caminos óptimos en redes complejas.

## 📌 Sobre el Proyecto

Desarrollado como proyecto universitario, este software permite modelar problemas reales (como logística, redes de telecomunicaciones o mapas de ciudades) mediante grafos ponderados. A diferencia de algoritmos de búsqueda no ponderados como BFS, este sistema toma en cuenta el *costo* (peso) de cada conexión para encontrar la ruta verdaderamente óptima.

### 🎮 La Dinámica: "Human vs IA"
El sistema cuenta con un minijuego integrado donde el usuario humano intenta adivinar visualmente la ruta más corta (y barata) entre un nodo de origen y un destino. Al finalizar, la Inteligencia Artificial (Dijkstra) entra en acción animando su propio recorrido perfecto para demostrar matemáticamente si el humano tomó la mejor decisión.

## ✨ Características Principales

* **Editor de Mapas en Tiempo Real:** Interfaz gráfica para crear nodos, moverlos y establecer conexiones (aristas) con pesos personalizados usando el ratón.
* **Competencia Interactiva:** Contadores de "Costo Humano" vs "Costo IA" en pantalla.
* **Animación de Algoritmo:** Visualización de la IA recorriendo el grafo paso a paso.
* **Modo Debug (Paso a Paso):** Herramienta educativa que muestra la tabla de distancias y cómo la Cola de Prioridad relaja las aristas.
* **Mapas Predefinidos:** Carga rápida de escenarios de prueba (Red de Servidores y Mapa de Ciudades).
* **Exportación de Datos:** Capacidad de exportar el modelo del grafo a texto plano.

## 🛠️ Tecnologías y Estructuras de Datos

* **Lenguaje:** Java puro (JDK 17+ recomendado).
* **Frontend UI:** Java Swing (`JPanel`, `Graphics2D` para el canvas interactivo).
* **Core Lógico:**
  * Implementación orientada a objetos (`Nodo`, `Arista`, `Grafo`).
  * `HashMap` para acceso eficiente a los nodos por ID en memoria $\mathcal{O}(1)$.
  * `PriorityQueue` de Java para la extracción del nodo con menor costo acumulado durante la ejecución de Dijkstra.

## 🧠 Fundamento Teórico y Complejidad Computacional

El algoritmo implementado garantiza encontrar la ruta más corta desde un nodo origen a todos los demás en un grafo dirigido/no dirigido con pesos no negativos. 

Gracias al uso de una Cola de Prioridad (`PriorityQueue`) para gestionar los nodos pendientes por explorar, la complejidad computacional de nuestra implementación se mantiene óptima:

**Complejidad de Tiempo:** $O((V + E) \log V)$
*(Donde $V$ es el número de vértices/nodos y $E$ es el número de aristas/conexiones).*

## 🚀 Cómo ejecutarlo

1. Clona este repositorio:
   ```bash
   git clone https://github.com/mateomartin21/Algoritmo-Dijkstra.git


2. Abre el proyecto en tu IDE favorito (IntelliJ IDEA, Eclipse, Apache NetBeans, o VS Code).
3. Asegúrate de tener configurado el JDK en tu entorno.
4. Ejecuta la clase principal Main.java ubicada en el paquete correspondiente.

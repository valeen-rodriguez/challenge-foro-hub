package principal;

import model.Autor;
import model.Idioma;
import model.Libro;
import service.AutorService;
import service.LibroService;
import service.GutendexService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import org.json.JSONObject;

public class Principal {

    private final Scanner inputScanner = new Scanner(System.in);
    private final AutorService autorService;
    private final LibroService libroService;
    private final GutendexService gutendexService;

    public Principal(AutorService autorService, LibroService libroService, GutendexService gutendexService) {
        this.autorService = autorService;
        this.libroService = libroService;
        this.gutendexService = gutendexService;
    }

    public void iniciarMenu() {
        int opcionSeleccionada = -1;

        while (opcionSeleccionada != 0) {
            mostrarOpcionesMenu();

            System.out.print("Por favor, elija una opción: ");
            String entrada = inputScanner.nextLine();

            try {
                opcionSeleccionada = Integer.parseInt(entrada);
                procesarOpcion(opcionSeleccionada);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
            }
        }
    }

    private void mostrarOpcionesMenu() {
        String menu = """
                |--------------------------------------------|
                |              Menú de Opciones              |
                |--------------------------------------------|
                | 1 | Buscar libro por título                |
                | 2 | Mostrar lista de libros registrados    |
                | 3 | Mostrar autores registrados            |
                | 4 | Autores vivos en un año específico     |
                | 5 | Listar libros por idioma               |
                | 6 | Estadísticas de libros por idioma      |
                | 7 | Top 10 libros más populares            |
                | 8 | Estadísticas de descargas por autor    |
                | 9 | Obtener libros de Gutendex             |
                |--------------------------------------------|
                | 0 | Salir                                  |
                |--------------------------------------------|
                """;
        System.out.println(menu);
    }

    private void procesarOpcion(int opcion) {
        switch (opcion) {
            case 1 -> buscarLibroPorTitulo();
            case 2 -> mostrarLibrosRegistrados();
            case 3 -> listarAutores();
            case 4 -> autoresVivosPorAno();
            case 5 -> filtrarLibrosPorIdioma();
            case 6 -> estadisticasPorIdioma();
            case 7 -> mostrarTopLibros();
            case 8 -> estadisticasPorAutor();
            case 9 -> obtenerLibrosDesdeGutendex();
            case 0 -> System.out.println("Saliendo de la aplicación. ¡Gracias!");
            default -> System.out.println("Opción no válida. Intente nuevamente.");
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.print("Ingrese el título del libro: ");
        String titulo = inputScanner.nextLine();

        Optional<Libro> libroExistente = libroService.buscarLibroPorTitulo(titulo);
        if (libroExistente.isPresent()) {
            System.out.printf("El libro ya está registrado:\n%s\n", libroExistente.get());
        } else {
            System.out.println("El libro no está registrado.");
        }
    }

    private void mostrarLibrosRegistrados() {
        List<Libro> libros = libroService.obtenerTodosLosLibros();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            System.out.println("Lista de libros registrados:");
            libros.forEach(libro -> System.out.printf("- %s\n", libro));
        }
    }

    private void listarAutores() {
        List<Autor> autores = autorService.obtenerTodosLosAutores();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            System.out.println("Lista de autores registrados:");
            autores.forEach(autor -> System.out.printf("- %s\n", autor.getNombre()));
        }
    }

    private void autoresVivosPorAno() {
        System.out.print("Ingrese el año para verificar autores vivos: ");
        int anio;
        try {
            anio = Integer.parseInt(inputScanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Año inválido. Intente nuevamente.");
            return;
        }

        List<Autor> autoresVivos = autorService.obtenerAutoresVivosEnAnio(anio);
        if (autoresVivos.isEmpty()) {
            System.out.printf("No se encontraron autores vivos en el año %d.\n", anio);
        } else {
            System.out.printf("Autores vivos en %d:\n", anio);
            autoresVivos.forEach(autor -> System.out.printf("- %s\n", autor.getNombre()));
        }
    }

    private void filtrarLibrosPorIdioma() {
        System.out.print("Ingrese el idioma (ej: 'en' para inglés, 'es' para español): ");
        String codigoIdioma = inputScanner.nextLine();
        try {
            Idioma idioma = Idioma.obtenerPorCodigo(codigoIdioma);
            List<Libro> librosPorIdioma = libroService.obtenerLibrosPorIdioma(idioma);
            if (librosPorIdioma.isEmpty()) {
                System.out.printf("No se encontraron libros en el idioma %s.\n", codigoIdioma);
            } else {
                System.out.printf("Libros en idioma %s:\n", codigoIdioma);
                librosPorIdioma.forEach(libro -> System.out.printf("- %s\n", libro));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Idioma no válido. Intente nuevamente.");
        }
    }

    private void estadisticasPorIdioma() {
        List<Object[]> estadisticas = libroService.obtenerEstadisticasPorIdioma();
        if (estadisticas.isEmpty()) {
            System.out.println("No hay estadísticas disponibles.");
        } else {
            System.out.println("Estadísticas de libros por idioma:");
            for (Object[] estadistica : estadisticas) {
                System.out.printf("Idioma: %s, Libros: %d, Descargas totales: %d\n",
                        estadistica[0], estadistica[1], estadistica[2]);
            }
        }
    }

    private void mostrarTopLibros() {
        List<Libro> topLibros = libroService.buscarTop10librosDescargados();
        if (topLibros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            System.out.println("Top 10 libros más populares:");
            topLibros.forEach(libro -> System.out.printf("- %s (Descargas: %d)\n", libro, libro.getNumeroDeDescargas()));
        }
    }

    private void estadisticasPorAutor() {
        Map<Autor, DoubleSummaryStatistics> estadisticas = libroService.obtenerEstadisticasDescargasPorAutor();
        if (estadisticas.isEmpty()) {
            System.out.println("No hay estadísticas disponibles.");
        } else {
            System.out.println("Estadísticas de descargas por autor:");
            estadisticas.forEach((autor, stats) -> System.out.printf(
                    "- Autor: %s, Total Descargas: %.0f, Promedio: %.2f, Mínimo: %.0f, Máximo: %.0f\n",
                    autor.getNombre(),
                    stats.getSum(),
                    stats.getAverage(),
                    stats.getMin(),
                    stats.getMax()));
        }
    }

    private void obtenerLibrosDesdeGutendex() {
        String url = "https://gutendex.com/books/?languages=es";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                System.out.println("Libros obtenidos de Gutendex:");
                jsonResponse.getJSONArray("results").forEach(libro -> {
                    JSONObject libroJson = (JSONObject) libro;
                    String titulo = libroJson.getString("title");
                    System.out.println(titulo);

                    libroService.guardarLibro(new Libro(titulo, Idioma.ESPANOL));
                });
            } else {
                System.out.println("Error al obtener los libros de Gutendex.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void mostrarLibrosDesdeGutendex() {
        obtenerLibrosDesdeGutendex();
    }
}

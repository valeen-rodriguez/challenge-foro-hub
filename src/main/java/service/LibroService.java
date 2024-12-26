package service;

import model.Autor;
import model.Libro;
import model.Idioma;
import repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public List<Libro> obtenerLibrosPorIdioma(Idioma idioma) {
        return libroRepository.findByIdioma(idioma);
    }

    public List<Libro> obtenerTodosLosLibros() {
        return libroRepository.findAll();
    }

    public Libro guardarLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    public List<Object[]> obtenerEstadisticasPorIdioma() {
        return libroRepository.obtenerEstadisticasPorIdioma();
    }

    public Optional<Libro> buscarLibroPorTitulo(String titulo) {
        return libroRepository.findByTituloIgnoreCase(titulo);
    }

    public List<Libro> buscarTop10librosDescargados(){
        return libroRepository.findTop10ByOrderByNumeroDeDescargasDesc();
    }


    public Map<Autor, DoubleSummaryStatistics> obtenerEstadisticasDescargasPorAutor() {
        List<Libro> libros = libroRepository.findAll();

        if (libros.isEmpty()) {
            return Collections.emptyMap();
        }

        return libros.stream()
                .filter(libro -> libro.getAutor() != null)
                .collect(Collectors.groupingBy(
                        Libro::getAutor,
                        Collectors.summarizingDouble(Libro::getNumeroDeDescargas)
                ));
    }

}

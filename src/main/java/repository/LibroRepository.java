package repository;

import model.Libro;
import model.Idioma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByIdioma(Idioma idioma);

    @Query("SELECT l.idioma, COUNT(l) FROM Libro l GROUP BY l.idioma ORDER BY COUNT(l) DESC")
    List<Object[]> obtenerEstadisticasPorIdioma();

    Optional<Libro> findByTituloIgnoreCase(String titulo);

    List<Libro> findTop10ByOrderByNumeroDeDescargasDesc();
}
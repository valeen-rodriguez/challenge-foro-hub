package service;

import model.Libro;
import model.Idioma;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GutendexService {

    private static final String URL_API = "https://gutendex.com/books/?languages=es";
    private final ConsumoAPI consumoAPI;

    public GutendexService(ConsumoAPI consumoAPI) {
        this.consumoAPI = consumoAPI;
    }

    public List<Libro> obtenerLibrosDesdeGutendex() {
        List<Libro> libros = new ArrayList<>();

        String jsonResponse = consumoAPI.obtenerDatos(URL_API);

        if (jsonResponse != null && !jsonResponse.isEmpty()) {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject libroJson = results.getJSONObject(i);
                String titulo = libroJson.getString("title");
                Idioma idioma = Idioma.ESPANOL;

                Libro libro = new Libro(titulo, idioma);
                libros.add(libro);
            }
        } else {
            System.out.println("No se obtuvieron datos de la API.");
        }

        return libros;
    }
}
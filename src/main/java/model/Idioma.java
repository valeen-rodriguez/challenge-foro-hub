package model;

public enum Idioma {
    ESPANOL("es", "Español"),
    INGLES("en", "Inglés"),
    FRANCES("fr", "Francés"),
    PORTUGUES("pt", "Portugués"),
    LATIN("la", "Latín"),
    ALEMAN("de", "Alemán"),
    ITALIANO("it", "Italiano");

    private final String codigo;
    private final String nombre;

    Idioma(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    public static Idioma obtenerPorCodigo(String codigo) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.codigo.equalsIgnoreCase(codigo)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Idioma no encontrado para el código: " + codigo);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public static String[] obtenerTodosLosNombres() {
        String[] nombres = new String[Idioma.values().length];
        for (int i = 0; i < Idioma.values().length; i++) {
            nombres[i] = Idioma.values()[i].getNombre();
        }
        return nombres;
    }
}
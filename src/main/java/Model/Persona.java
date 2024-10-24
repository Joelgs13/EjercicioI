package Model;

/**
 * Clase que representa una persona con atributos básicos como id, nombre, apellido y edad.
 */
public class Persona {
    private int id;
    private String nombre;
    private String apellido;
    private int edad;

    /**
     * Constructor de la clase Persona.
     *
     * @param id       El identificador único de la persona.
     * @param nombre   El nombre de la persona.
     * @param apellido El apellido de la persona.
     * @param edad     La edad de la persona.
     */
    public Persona(int id, String nombre, String apellido, int edad) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
    }

    // Métodos de acceso (getters)

    /**
     * Obtiene el id de la persona.
     *
     * @return El id de la persona.
     */
    public int getId() {
        return id;
    }

    /**
     * Obtiene el nombre de la persona.
     *
     * @return El nombre de la persona.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el apellido de la persona.
     *
     * @return El apellido de la persona.
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Obtiene la edad de la persona.
     *
     * @return La edad de la persona.
     */
    public int getEdad() {
        return edad;
    }

    // Métodos de modificación (setters)

    /**
     * Establece el id de la persona.
     *
     * @param id El nuevo id de la persona.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Establece el nombre de la persona.
     *
     * @param nombre El nuevo nombre de la persona.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Establece el apellido de la persona.
     *
     * @param apellido El nuevo apellido de la persona.
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Establece la edad de la persona.
     *
     * @param edad La nueva edad de la persona.
     */
    public void setEdad(int edad) {
        this.edad = edad;
    }

    /**
     * Compara si este objeto es igual a otro.
     *
     * @param obj El objeto a comparar.
     * @return true si son iguales, false de lo contrario.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Persona persona = (Persona) obj;
        return id == persona.id;
    }

    /**
     * Devuelve el código hash de la persona basado en su id.
     *
     * @return El código hash de la persona.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}

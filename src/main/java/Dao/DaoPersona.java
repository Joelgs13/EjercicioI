package Dao;

import BBDD.ConexionBBDD;
import Model.Persona;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos para la entidad {@link Persona}.
 * Proporciona métodos para realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar)
 * en la tabla "Persona" de la base de datos.
 */
public class DaoPersona {

    /**
     * Obtiene todas las personas de la base de datos.
     *
     * @return Una lista de objetos {@link Persona} que representan todas las personas en la base de datos.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public List<Persona> obtenerTodas() throws SQLException {
        Connection conexion = new ConexionBBDD().getConnection();
        List<Persona> personas = new ArrayList<>();
        String query = "SELECT * FROM Persona";

        try (Statement statement = conexion.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                Persona persona = new Persona(
                        resultSet.getInt("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellidos"),
                        resultSet.getInt("edad")
                );
                personas.add(persona);
            }
        } finally {
            new ConexionBBDD().CloseConexion();
        }

        return personas;
    }

    /**
     * Agrega una nueva persona a la base de datos.
     *
     * @param persona La persona a agregar.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public void agregar(Persona persona) throws SQLException {
        Connection conexion = new ConexionBBDD().getConnection();
        String query = "INSERT INTO Persona (nombre, apellidos, edad) VALUES (?, ?, ?)";

        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            statement.setString(1, persona.getNombre());
            statement.setString(2, persona.getApellido());
            statement.setInt(3, persona.getEdad());
            statement.executeUpdate();
        } finally {
            new ConexionBBDD().CloseConexion();
        }
    }

    /**
     * Modifica los datos de una persona existente en la base de datos.
     *
     * @param persona La persona con los datos actualizados.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public void modificar(Persona persona) throws SQLException {
        Connection conexion = new ConexionBBDD().getConnection();
        String query = "UPDATE Persona SET nombre = ?, apellidos = ?, edad = ? WHERE id = ?";

        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            statement.setString(1, persona.getNombre());
            statement.setString(2, persona.getApellido());
            statement.setInt(3, persona.getEdad());
            statement.setInt(4, persona.getId());
            statement.executeUpdate();
        } finally {
            new ConexionBBDD().CloseConexion();
        }
    }

    /**
     * Elimina una persona de la base de datos según su ID.
     *
     * @param id El ID de la persona a eliminar.
     * @throws SQLException si ocurre un error al acceder a la base de datos.
     */
    public void eliminar(int id) throws SQLException {
        Connection conexion = new ConexionBBDD().getConnection();
        String query = "DELETE FROM Persona WHERE id = ?";

        try (PreparedStatement statement = conexion.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } finally {
            new ConexionBBDD().CloseConexion();
        }
    }
}

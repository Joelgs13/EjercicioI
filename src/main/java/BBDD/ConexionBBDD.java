package BBDD;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase para gestionar la conexión a la base de datos MariaDB.
 * Esta clase es responsable de establecer, mantener y cerrar
 * la conexión con la base de datos especificada.
 */
public class ConexionBBDD {

    /** Conexión activa a la base de datos. */
    private final Connection connection;

    /**
     * Constructor que establece la conexión con la base de datos.
     * Configura las propiedades de usuario y contraseña, y
     * realiza la conexión a la base de datos MariaDB en la dirección
     * y parámetros especificados.
     *
     * @throws SQLException si ocurre un error al establecer la conexión
     *                     (por ejemplo, si la base de datos no está disponible
     *                     o las credenciales son incorrectas).
     */
    public ConexionBBDD() throws SQLException {
        Properties connConfig = new Properties();
        connConfig.setProperty("user", "root");
        connConfig.setProperty("password", "mypass");

        // Establecer la conexión a la base de datos con los parámetros especificados
        connection = DriverManager.getConnection(
                "jdbc:mariadb://127.0.0.1:33066/personas?serverTimezone=Europe/Madrid",
                connConfig
        );
        connection.setAutoCommit(true); // Configura el modo de autocommit para la conexión

        // Obtener información sobre la base de datos para fines de depuración
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        /*
         System.out.println();
         System.out.println("--- Datos de conexión ------------------------------------------");
         System.out.printf("Base de datos: %s%n", databaseMetaData.getDatabaseProductName());
         System.out.printf("  Versión: %s%n", databaseMetaData.getDatabaseProductVersion());
         System.out.printf("Driver: %s%n", databaseMetaData.getDriverName());
         System.out.printf("  Versión: %s%n", databaseMetaData.getDriverVersion());
         System.out.println("----------------------------------------------------------------");
         System.out.println();
         */
    }

    /**
     * Devuelve la conexión activa a la base de datos.
     *
     * @return la conexión activa a la base de datos
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Cierra la conexión activa con la base de datos.
     *
     * @return la conexión cerrada (si es necesario para otros procesos,
     *         aunque generalmente no se devuelve un objeto cerrado)
     * @throws SQLException si ocurre un error al cerrar la conexión,
     *                     por ejemplo, si la conexión ya está cerrada.
     */
    public Connection CloseConexion() throws SQLException {
        connection.close(); // Cierra la conexión
        return connection;   // Retorna la conexión cerrada (opcional)
    }
}

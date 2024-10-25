package com.example.ejercicioi;

import BBDD.ConexionBBDD;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Clase principal de la aplicación que extiende {@link Application}.
 * Se encarga de inicializar la interfaz gráfica, configurar el idioma y mostrar la ventana principal.
 * La configuración del idioma se obtiene desde un archivo de propiedades de la base de datos.
 */
public class HelloApplication extends Application {
    private static ResourceBundle bundle;

    /**
     * Método que se llama al iniciar la aplicación.
     * Carga el archivo FXML, establece el idioma de la interfaz, configura la escena y el escenario, y muestra la ventana principal.
     * La configuración de idioma se carga desde las propiedades de conexión de la base de datos, permitiendo
     * seleccionar el idioma de la interfaz según las preferencias definidas.
     *
     * @param stage El escenario principal de la aplicación.
     * @throws IOException si ocurre un error al cargar el archivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Configuración de idioma desde propiedades de conexión
        Properties connConfig = ConexionBBDD.loadProperties();
        String lang = connConfig.getProperty("language");
        Locale locale = new Locale.Builder().setLanguage(lang).build();
        bundle = ResourceBundle.getBundle("properties/lang", locale);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("EjercicioI.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/cuaderno.png")));
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/estilos/styles.css")).toExternalForm());
        stage.getIcons().add(icon);
        stage.setMaxWidth(840);
        stage.setMinWidth(565);
        stage.setMinHeight(325);
        stage.setTitle("Ejercicio I!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Método estático que proporciona el recurso de internacionalización (ResourceBundle)
     * para los controladores que necesitan acceso a las cadenas de texto traducidas.
     *
     * @return El objeto {@link ResourceBundle} con las traducciones de texto según el idioma configurado.
     */
    public static ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * Método principal de la aplicación.
     * Llama al método {@link #start(Stage)} para iniciar la aplicación JavaFX.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        launch();
    }
}

package com.example.ejercicioi;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Clase principal de la aplicación que extiende la clase {@link Application}.
 * Se encarga de inicializar la interfaz gráfica y mostrar la ventana principal.
 */
public class HelloApplication extends Application {

    /**
     * Método que se llama al iniciar la aplicación.
     * Carga el archivo FXML, configura la escena y el escenario, y muestra la ventana.
     *
     * @param stage El escenario principal de la aplicación.
     * @throws IOException si ocurre un error al cargar el archivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("EjercicioI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        Image icon = new Image(getClass().getResourceAsStream("/iconos/cuaderno.png"));
        stage.getIcons().add(icon);
        stage.setMaxWidth(840);
        stage.setMinWidth(565);
        stage.setMinHeight(325);
        stage.setTitle("Ejercicio I!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Método principal de la aplicación.
     * Llama al método {@link #start(Stage)} para iniciar la aplicación.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        launch();
    }
}

package com.example.ejercicioi;

import Dao.DaoPersona;
import Model.Persona;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class ejercicioIController {

    @FXML
    private ImageView ivMas;

    @FXML
    private ImageView ivEditar;

    @FXML
    private ImageView ivMenos;

    @FXML
    private TableView<Persona> personTable;

    @FXML
    private TextField filtrarField;

    @FXML
    private TableColumn<Persona, String> nombreColumn;

    @FXML
    private TableColumn<Persona, String> apellidosColumn;

    @FXML
    private TableColumn<Persona, Integer> edadColumn;

    @FXML
    private Button agregarButton;

    @FXML
    private Button modificarButton;

    @FXML
    private Button eliminarButton;

    @FXML
    private ImageView imagenPersonas;

    @FXML
    private Label filterLabel;  // Añadido para el label del filtro

    @FXML
    private MenuItem modifyMenuItem, deleteMenuItem; // Añadido para los items del menú contextual

    private ObservableList<Persona> personasList = FXCollections.observableArrayList();
    private DaoPersona daoPersona = new DaoPersona();

    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        nombreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        apellidosColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellido()));
        edadColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEdad()).asObject());

        cargarPersonasDesdeBD();
        // tooltips
        agregarButton.setTooltip(new Tooltip("Agregar una nueva persona"));
        modificarButton.setTooltip(new Tooltip("Modificar una persona"));
        eliminarButton.setTooltip(new Tooltip("Eliminar una persona"));
        filtrarField.setTooltip(new Tooltip("Filtrar personas por su nombre"));

        // Cargar imágenes
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/contactos.jpeg")));
        imagenPersonas.setImage(image);
        Image image2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/mas.png")));
        ivMas.setImage(image2);
        Image image3 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/editar.png")));
        ivEditar.setImage(image3);
        Image image4 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/menos.png")));
        ivMenos.setImage(image4);

        // Inicializar multidioma
        Locale locale = new Locale("eu");
        bundle = ResourceBundle.getBundle("properties.lang", locale);

        // Aplicar traducciones
        updateUI();
    }

    private void updateUI() {
        agregarButton.setText(bundle.getString("add_person"));
        modificarButton.setText(bundle.getString("modify_person"));
        eliminarButton.setText(bundle.getString("delete_person"));
        filterLabel.setText(bundle.getString("filter_by_name"));

        nombreColumn.setText(bundle.getString("name"));
        apellidosColumn.setText(bundle.getString("surname"));
        edadColumn.setText(bundle.getString("age"));

        modifyMenuItem.setText(bundle.getString("modify"));
        deleteMenuItem.setText(bundle.getString("delete"));
    }

    private void cargarPersonasDesdeBD() {
        try {
            List<Persona> personas = daoPersona.obtenerTodas();
            personasList.setAll(personas);
            personTable.setItems(personasList);
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos desde la base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void abrirVentanaAgregar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ejercicioi/ejercicioImodal.fxml"));
            Parent modalRoot = loader.load();
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(modalRoot));
            modalStage.showAndWait();
            cargarPersonasDesdeBD();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + e.getMessage());
        }
    }

    @FXML
    private void eliminarPersona(ActionEvent event) {
        Persona personaSeleccionada = personTable.getSelectionModel().getSelectedItem();
        if (personaSeleccionada != null) {
            try {
                daoPersona.eliminar(personaSeleccionada.getId());
                personasList.remove(personaSeleccionada);
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo eliminar la persona: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Advertencia", "Seleccione una persona para eliminar.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void manejarMenuContextual(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        if (menuItem == modifyMenuItem) {
            abrirVentanaAgregar(event); // reutilizar el método
        } else if (menuItem == deleteMenuItem) {
            eliminarPersona(event);
        }
    }

    @FXML
    private void filtrar(ActionEvent event) {
        String filtro = filtrarField.getText().toLowerCase();
        if (!filtro.isEmpty()) {
            ObservableList<Persona> filtrado = FXCollections.observableArrayList();
            for (Persona persona : personasList) {
                if (persona.getNombre().toLowerCase().contains(filtro)) {
                    filtrado.add(persona);
                }
            }
            personTable.setItems(filtrado);
        } else {
            personTable.setItems(personasList);
        }
    }
}

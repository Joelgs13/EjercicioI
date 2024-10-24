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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

    private ObservableList<Persona> personasList = FXCollections.observableArrayList();
    private DaoPersona daoPersona = new DaoPersona();


    private ResourceBundle bundle;


    @FXML
    public void initialize() {
        nombreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        apellidosColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellido()));
        edadColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEdad()).asObject());

        cargarPersonasDesdeBD();
        //tooltips
        agregarButton.setTooltip(new Tooltip("Agregar una nueva persona"));
        modificarButton.setTooltip(new Tooltip("Modificar una persona"));
        eliminarButton.setTooltip(new Tooltip("Eliminar una persona"));
        filtrarField.setTooltip(new Tooltip("Filtrar personas por su nombre"));
        //imagenes
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/contactos.jpeg")));
        imagenPersonas.setImage(image);
        Image image2 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/mas.png")));
        ivMas.setImage(image2);
        Image image3 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/editar.png")));
        ivEditar.setImage(image3);
        Image image4 = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/menos.png")));
        ivMenos.setImage(image4);
        //multiidioma
        Locale locale = new Locale("en"); // Cambiar aquí según el idioma deseado
        bundle = ResourceBundle.getBundle("properties.lang", locale);

        // Aplicar traducciones
        updateUI();
    }

    private void updateUI() {
        agregarButton.setText(bundle.getString("add_person"));
        modificarButton.setText(bundle.getString("modify_person"));
        eliminarButton.setText(bundle.getString("delete_person"));
        // Aquí puedes actualizar otros elementos de la UI usando el ResourceBundle.
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
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(agregarButton.getScene().getWindow());

            ejercicioIModalController modalController = loader.getController();
            modalController.setPersonasList(personasList);
            modalController.setDaoPersona(daoPersona);

            if (event.getSource() == agregarButton) {
                modalStage.setTitle("Agregar Persona");
            } else if (event.getSource() == modificarButton || event.getSource() instanceof MenuItem) {
                Persona personaSeleccionada = personTable.getSelectionModel().getSelectedItem();
                if (personaSeleccionada == null) {
                    mostrarAlerta("No hay ninguna persona seleccionada", "Por favor, seleccione una persona para editar.");
                    return;
                }
                modalStage.setTitle("Editar Persona");
                modalController.setPersonaAEditar(personaSeleccionada);
            }

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
        if (personaSeleccionada == null) {
            mostrarAlerta("No hay ninguna persona seleccionada", "Por favor, seleccione una persona para eliminar.");
        } else {
            try {
                daoPersona.eliminar(personaSeleccionada.getId());
                personasList.remove(personaSeleccionada);
                mostrarAlerta("Persona eliminada", "La persona ha sido eliminada con éxito.");
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo eliminar la persona: " + e.getMessage());
            }
        }
    }

    // Nuevo método para manejar acciones del menú contextual
    @FXML
    private void manejarMenuContextual(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        if ("Modificar".equals(menuItem.getText())) {
            abrirVentanaAgregar(new ActionEvent(modificarButton, null));
        } else if ("Eliminar".equals(menuItem.getText())) {
            eliminarPersona(new ActionEvent(eliminarButton, null));
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void filtrar() {
        String textoFiltro = filtrarField.getText().toLowerCase();
        ObservableList<Persona> personasFiltradas = FXCollections.observableArrayList();

        for (Persona persona : personasList) {
            if (persona.getNombre().toLowerCase().contains(textoFiltro)) {
                personasFiltradas.add(persona);
            }
        }

        personTable.setItems(personasFiltradas);
    }
}

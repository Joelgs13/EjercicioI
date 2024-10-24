package com.example.ejercicioi;

import Dao.DaoPersona;
import Model.Persona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador que maneja la interfaz de usuario de la aplicación.
 * Permite la gestión de una lista de personas, incluyendo operaciones
 * para agregar, modificar, eliminar y filtrar personas, utilizando
 * datos obtenidos de una base de datos.
 */
public class ejercicioIController {

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

    private ObservableList<Persona> personasList = FXCollections.observableArrayList();
    private DaoPersona daoPersona = new DaoPersona();

    /**
     * Inicializa el controlador, configurando las columnas de la tabla
     * y cargando los datos de las personas desde la base de datos.
     */
    @FXML
    public void initialize() {
        nombreColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        apellidosColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getApellido()));
        edadColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getEdad()).asObject());

        cargarPersonasDesdeBD();
    }

    /**
     * Carga las personas desde la base de datos y las añade a la lista observable.
     * En caso de error durante la carga, muestra un mensaje de alerta.
     */
    private void cargarPersonasDesdeBD() {
        try {
            List<Persona> personas = daoPersona.obtenerTodas();
            personasList.setAll(personas); // Actualiza la lista observable con los datos obtenidos
            personTable.setItems(personasList); // Asigna la lista a la tabla
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos desde la base de datos: " + e.getMessage());
        }
    }

    /**
     * Abre una ventana modal para agregar o modificar una persona.
     * Dependiendo del botón que se haya pulsado (Agregar o Modificar),
     * se configura la ventana modal adecuadamente.
     *
     * @param event Evento disparado por los botones "Agregar Persona" o "Modificar Persona".
     */
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
            modalController.setDaoPersona(daoPersona);  // Pasamos el DAO al modal

            if (event.getSource() == agregarButton) {
                modalStage.setTitle("Agregar Persona");
            } else if (event.getSource() == modificarButton) {
                Persona personaSeleccionada = personTable.getSelectionModel().getSelectedItem();
                if (personaSeleccionada == null) {
                    mostrarAlerta("No hay ninguna persona seleccionada", "Por favor, seleccione una persona para editar.");
                    return;
                }
                modalStage.setTitle("Editar Persona");
                modalController.setPersonaAEditar(personaSeleccionada); // Configura la persona a editar
            }

            modalStage.setScene(new Scene(modalRoot));
            modalStage.showAndWait();

            cargarPersonasDesdeBD();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana: " + e.getMessage());
        }
    }

    /**
     * Elimina la persona seleccionada de la tabla.
     * Si no hay una persona seleccionada, muestra un mensaje de alerta.
     *
     * @param event Evento disparado por el botón "Eliminar Persona".
     */
    @FXML
    private void eliminarPersona(ActionEvent event) {
        Persona personaSeleccionada = personTable.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            mostrarAlerta("No hay ninguna persona seleccionada", "Por favor, seleccione una persona para eliminar.");
        } else {
            try {
                daoPersona.eliminar(personaSeleccionada.getId());
                personasList.remove(personaSeleccionada); // Actualiza la lista observable eliminando la persona
                mostrarAlerta("Persona eliminada", "La persona ha sido eliminada con éxito.");
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo eliminar la persona: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra una alerta con el título y el mensaje especificado.
     *
     * @param titulo El título de la alerta.
     * @param mensaje El mensaje a mostrar en la alerta.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait(); // Muestra la alerta y espera a que el usuario la cierre
    }

    /**
     * Filtra las personas en la tabla según el texto ingresado en el campo de filtro.
     * Actualiza la vista de la tabla para mostrar solo las personas que coincidan
     * con el criterio de búsqueda.
     */
    public void filtrar() {
        String textoFiltro = filtrarField.getText().toLowerCase();

        ObservableList<Persona> personasFiltradas = FXCollections.observableArrayList();

        // Filtrar la lista de personas según el nombre
        for (Persona persona : personasList) {
            if (persona.getNombre().toLowerCase().contains(textoFiltro)) {
                personasFiltradas.add(persona);
            }
        }

        personTable.setItems(personasFiltradas); // Actualiza la tabla con la lista filtrada
    }
}

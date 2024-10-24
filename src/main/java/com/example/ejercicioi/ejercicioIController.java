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

    @FXML
    public void initialize() {
        nombreColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        apellidosColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getApellido()));
        edadColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getEdad()).asObject());

        cargarPersonasDesdeBD();
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

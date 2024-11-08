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
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controlador principal de la aplicación que maneja la interfaz gráfica
 * y las interacciones con el usuario en el TableView de personas.
 */
public class ejercicioIController {

    @FXML private Label filtrarLabel;
    @FXML private ImageView ivMas;
    @FXML private ImageView ivEditar;
    @FXML private ImageView ivMenos;
    @FXML private TableView<Persona> personTable;
    @FXML private TextField filtrarField;
    @FXML private TableColumn<Persona, String> nombreColumn;
    @FXML private TableColumn<Persona, String> apellidosColumn;
    @FXML private TableColumn<Persona, Integer> edadColumn;
    @FXML private Button agregarButton;
    @FXML private Button modificarButton;
    @FXML private Button eliminarButton;
    @FXML private ImageView imagenPersonas;
    @FXML private MenuItem miModificar;
    @FXML private MenuItem miEliminar;
    private ObservableList<Persona> personasList = FXCollections.observableArrayList();
    private DaoPersona daoPersona = new DaoPersona();
    @FXML private ResourceBundle bundle;

    /**
     * Metodo de inicialización que configura los elementos de la interfaz
     * y carga las personas desde la base de datos al iniciar la aplicación.
     * También aplica el ResourceBundle para traducciones y configura
     * los tooltips y las imágenes para los botones.
     */
    @FXML
    public void initialize() {
        bundle = HelloApplication.getBundle();
        updateUI();
        cargarPersonasDesdeBD();

        // Configuración de columnas
        nombreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        apellidosColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellido()));
        edadColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getEdad()).asObject());

        // Tooltips para los botones
        agregarButton.setTooltip(new Tooltip(bundle.getString("ttaddperson")));
        modificarButton.setTooltip(new Tooltip(bundle.getString("ttmodifyperson")));
        eliminarButton.setTooltip(new Tooltip(bundle.getString("tteliminateperson")));
        filtrarField.setTooltip(new Tooltip(bundle.getString("ttfilterbyperson")));

        // Configuración de imágenes en botones
        imagenPersonas.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/contactos.jpeg"))));
        ivMas.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/mas.png"))));
        ivEditar.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/editar.png"))));
        ivMenos.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iconos/menos.png"))));

        updateUI();  // Aplicar traducciones
    }

    /**
     * Actualiza los textos de la interfaz utilizando el ResourceBundle
     * para adaptarse a configuraciones de idioma.
     */
    private void updateUI() {
        agregarButton.setText(bundle.getString("add_person"));
        modificarButton.setText(bundle.getString("modify_person"));
        eliminarButton.setText(bundle.getString("delete_person"));
        filtrarLabel.setText(bundle.getString("filter_by_name"));
        miModificar.setText(bundle.getString("modify_person"));
        miEliminar.setText(bundle.getString("delete_person"));
        nombreColumn.setText(bundle.getString("name"));
        edadColumn.setText(bundle.getString("age"));
        apellidosColumn.setText(bundle.getString("surname"));
    }

    /**
     * Carga todas las personas desde la base de datos utilizando el DaoPersona
     * y actualiza la lista observable para reflejar los datos en el TableView.
     */
    private void cargarPersonasDesdeBD() {
        try {
            List<Persona> personas = daoPersona.obtenerTodas();
            personasList.setAll(personas);
            personTable.setItems(personasList);
        } catch (SQLException e) {
            mostrarAlerta("Error", bundle.getString("errdatadownload") + e.getMessage());
        }
    }

    /**
     * Abre una ventana modal para agregar o modificar una persona.
     * Si se llama desde el botón de agregar, abre la ventana en modo de adición.
     * Si se llama desde el botón de modificar o el menú contextual, abre en modo edición.
     *
     * @param event Evento de acción que puede ser de agregar o modificar.
     */
    @FXML
    private void abrirVentanaAgregar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ejercicioi/ejercicioImodal.fxml"));
            loader.setResources(bundle);
            Parent modalRoot = loader.load();

            ejercicioIModalController modalController = loader.getController();
            modalController.setBundle(bundle);
            modalController.setPersonasList(personasList);
            modalController.setDaoPersona(daoPersona);

            Stage modalStage = new Stage();
            modalStage.setResizable(false);
            modalStage.initModality(Modality.WINDOW_MODAL);
            modalStage.initOwner(agregarButton.getScene().getWindow());

            if (event.getSource() == agregarButton) {
                modalStage.setTitle(bundle.getString("add_person"));
            } else if (event.getSource() == modificarButton || event.getSource() instanceof MenuItem) {
                Persona personaSeleccionada = personTable.getSelectionModel().getSelectedItem();
                if (personaSeleccionada == null) {
                    mostrarAlerta(bundle.getString("nopersonselected"), bundle.getString("selecttoedit"));
                    return;
                }
                modalStage.setTitle(bundle.getString("modify_person"));
                modalController.setPersonaAEditar(personaSeleccionada);
            }

            modalStage.setScene(new Scene(modalRoot));
            modalStage.showAndWait();

            cargarPersonasDesdeBD();

        } catch (IOException e) {
            mostrarAlerta("Error", bundle.getString("notable2open") + e.getMessage());
        }
    }

    /**
     * Elimina la persona seleccionada en el TableView. Muestra una alerta
     * si no hay ninguna persona seleccionada o si ocurre un error durante
     * el proceso de eliminación.
     *
     * @param event Evento de acción que puede ser de eliminar.
     */
    @FXML
    private void eliminarPersona(ActionEvent event) {
        Persona personaSeleccionada = personTable.getSelectionModel().getSelectedItem();
        if (personaSeleccionada == null) {
            mostrarAlerta(bundle.getString("nopersonselected"), bundle.getString("selecttoedit"));
        } else {
            try {
                daoPersona.eliminar(personaSeleccionada.getId());
                personasList.remove(personaSeleccionada);
                mostrarAlerta(bundle.getString("deleted"), bundle.getString("successdeleting"));
            } catch (SQLException e) {
                mostrarAlerta("Error", bundle.getString("notable2delete")+ e.getMessage());
            }
        }
    }

    /**
     * Maneja las acciones de modificar y eliminar del menú contextual,
     * llamando a los métodos correspondientes según la acción seleccionada.
     *
     * @param event Evento de acción del menú contextual.
     */
    @FXML
    private void manejarMenuContextual(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        if (bundle.getString("modify").equals(menuItem.getText())) {
            abrirVentanaAgregar(new ActionEvent(modificarButton, null));
        } else if (bundle.getString("delete").equals(menuItem.getText())) {
            eliminarPersona(new ActionEvent(eliminarButton, null));
        }
    }

    /**
     * Muestra una alerta informativa con un título y mensaje especificados.
     *
     * @param titulo  Título de la alerta.
     * @param mensaje Mensaje a mostrar en el contenido de la alerta.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Filtra las personas en el TableView según el texto ingresado en el
     * campo de filtrado, mostrando solo las que coincidan parcialmente
     * en su nombre.
     */
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

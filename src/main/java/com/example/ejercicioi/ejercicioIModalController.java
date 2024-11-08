package com.example.ejercicioi;

import Dao.DaoPersona;
import Model.Persona;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controlador para la ventana modal en la aplicación de gestión de personas.
 * Permite agregar y modificar personas, además de realizar validaciones y mostrar mensajes de éxito o error.
 */
public class ejercicioIModalController {

    @FXML public Label labelApellidos;
    @FXML public Label labelNombre;
    @FXML public Label labelEdad;
    @FXML public Button agregarButton;
    @FXML public Button cancelarButton;
    @FXML private TextField nombreField;
    @FXML private TextField apellidosField;
    @FXML private TextField edadField;

    private ObservableList<Persona> personasList;
    private Persona personaAEditar = null;
    private DaoPersona daoPersona;
    private boolean esModificacion = false;
    private static ResourceBundle bundle;

    /**
     * Configura el ResourceBundle para el controlador y aplica las traducciones
     * a los textos de la interfaz.
     *
     * @param bundle El ResourceBundle con las traducciones de la interfaz.
     */
    public void setBundle(ResourceBundle bundle) {
        ejercicioIModalController.bundle = bundle;
        updateUI();
    }

    /**
     * Aplica las traducciones de interfaz usando el ResourceBundle.
     * Cambia los textos de los botones y etiquetas.
     */
    private void updateUI() {
        labelApellidos.setText(bundle.getString("surname"));
        labelNombre.setText(bundle.getString("name"));
        labelEdad.setText(bundle.getString("age"));
        agregarButton.setText(bundle.getString("save"));
        cancelarButton.setText(bundle.getString("delete"));
    }

    /**
     * Asigna la lista observable de personas para que el controlador pueda actualizar
     * la lista en la tabla principal de personas.
     *
     * @param personasList La lista observable de personas.
     */
    public void setPersonasList(ObservableList<Persona> personasList) {
        this.personasList = personasList;
    }

    /**
     * Asigna el objeto DaoPersona, que permite realizar operaciones CRUD en la base de datos.
     *
     * @param daoPersona El DAO para manejar las operaciones de la base de datos de Persona.
     */
    public void setDaoPersona(DaoPersona daoPersona) {
        this.daoPersona = daoPersona;
    }

    /**
     * Configura la persona que será editada, permitiendo modificar
     * los datos de una persona existente.
     *
     * @param persona La persona seleccionada para editar.
     */
    public void setPersonaAEditar(Persona persona) {
        this.personaAEditar = persona;
        this.esModificacion = true;
        rellenarCampos(persona);
    }

    /**
     * Rellena los campos de la interfaz con los datos de una persona para
     * que puedan ser editados.
     *
     * @param persona La persona cuyos datos se mostrarán en los campos.
     */
    public void rellenarCampos(Persona persona) {
        nombreField.setText(persona.getNombre());
        apellidosField.setText(persona.getApellido());
        edadField.setText(String.valueOf(persona.getEdad()));
    }

    /**
     * Agrega una nueva persona o modifica una existente, realizando validaciones
     * de campos y manejo de errores. Al finalizar, muestra un mensaje de éxito o error.
     */
    @FXML
    private void aniadirPersona() {
        String nombre = nombreField.getText().trim();
        String apellidos = apellidosField.getText().trim();
        String edadText = edadField.getText().trim();
        StringBuilder errores = new StringBuilder();

        // Validación de campos
        if (nombre.isEmpty()) {
            errores.append(bundle.getString("namenotnull")+"\n");
        }
        if (apellidos.isEmpty()) {
            errores.append(bundle.getString("surnamenotnull")+"\n");
        }

        int edad = -1;
        try {
            edad = Integer.parseInt(edadText);
            if (edad < 0) {
                errores.append(bundle.getString("agepositive")+"\n");
            }
        } catch (NumberFormatException e) {
            errores.append(bundle.getString("agevalid")+"\n");
        }

        if (errores.length() > 0) {
            mostrarError(errores.toString());
            return;
        }

        try {
            if (esModificacion && personaAEditar != null) {
                // Modificación de persona existente
                personaAEditar.setNombre(nombre);
                personaAEditar.setApellido(apellidos);
                personaAEditar.setEdad(edad);
                daoPersona.modificar(personaAEditar);
                mostrarInformacion(bundle.getString("modified"));
            } else {
                // Agregar nueva persona, comprobando duplicados
                Persona nuevaPersona = new Persona(0, nombre, apellidos, edad);
                for (Persona persona : personasList) {
                    if (persona.equals(nuevaPersona)) {
                        mostrarError(bundle.getString("duplicatedperson"));
                        return;
                    }
                }
                daoPersona.agregar(nuevaPersona);
                personasList.add(nuevaPersona);
                mostrarInformacion(bundle.getString("addsuccesfull"));
            }
        } catch (SQLException e) {
            mostrarError(bundle.getString("databaseerror")+ e.getMessage());
        }

        cerrarVentana(); // Cierra la ventana al finalizar la operación
    }

    /**
     * Muestra un mensaje informativo en una ventana emergente.
     *
     * @param mensaje El mensaje a mostrar.
     */
    private void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("information"));
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de error en una ventana emergente.
     *
     * @param mensaje El mensaje de error a mostrar.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Cierra la ventana modal, generalmente después de realizar una operación de éxito
     * o cancelar la operación actual.
     */
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }
}

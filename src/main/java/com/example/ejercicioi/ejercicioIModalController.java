package com.example.ejercicioi;

import Dao.DaoPersona;
import Model.Persona;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ejercicioIModalController {

    @FXML
    private TextField nombreField;

    @FXML
    private TextField apellidosField;

    @FXML
    private TextField edadField;

    private ObservableList<Persona> personasList;
    private Persona personaAEditar = null;
    private DaoPersona daoPersona;
    private boolean esModificacion = false;

    public void setPersonasList(ObservableList<Persona> personasList) {
        this.personasList = personasList;
    }

    public void setDaoPersona(DaoPersona daoPersona) {
        this.daoPersona = daoPersona;
    }

    public void setPersonaAEditar(Persona persona) {
        this.personaAEditar = persona;
        this.esModificacion = true;
        rellenarCampos(persona);
    }

    public void rellenarCampos(Persona persona) {
        nombreField.setText(persona.getNombre());
        apellidosField.setText(persona.getApellido());
        edadField.setText(String.valueOf(persona.getEdad()));
    }

    @FXML
    private void aniadirPersona() {
        String nombre = nombreField.getText().trim();
        String apellidos = apellidosField.getText().trim();
        String edadText = edadField.getText().trim();
        StringBuilder errores = new StringBuilder();

        if (nombre.isEmpty()) {
            errores.append("El campo 'Nombre' no puede estar vacío.\n");
        }
        if (apellidos.isEmpty()) {
            errores.append("El campo 'Apellidos' no puede estar vacío.\n");
        }

        int edad = -1;
        try {
            edad = Integer.parseInt(edadText);
            if (edad < 0) {
                errores.append("La edad debe ser un número positivo.\n");
            }
        } catch (NumberFormatException e) {
            errores.append("El campo 'Edad' debe ser un número entero válido.\n");
        }

        if (errores.length() > 0) {
            mostrarError(errores.toString());
            return;
        }

        try {
            if (esModificacion && personaAEditar != null) {
                personaAEditar.setNombre(nombre);
                personaAEditar.setApellido(apellidos);
                personaAEditar.setEdad(edad);
                daoPersona.modificar(personaAEditar);
                mostrarInformacion("Persona modificada con éxito.");
            } else {
                Persona nuevaPersona = new Persona(0, nombre, apellidos, edad);
                for (Persona persona : personasList) {
                    if (persona.equals(nuevaPersona)) {
                        mostrarError("Persona duplicada: Ya existe una persona con los mismos datos.");
                        return;
                    }
                }
                daoPersona.agregar(nuevaPersona);
                personasList.add(nuevaPersona);
                mostrarInformacion("Persona agregada con éxito.");
            }
        } catch (SQLException e) {
            mostrarError("Error al interactuar con la base de datos: " + e.getMessage());
        }

        cerrarVentana(); // Cerrar la ventana al finalizar la operación
    }

    private void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }
}

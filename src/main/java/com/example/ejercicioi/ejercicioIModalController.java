package com.example.ejercicioi;

import Dao.DaoPersona;
import Model.Persona;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Controlador para la ventana modal que permite agregar o editar una persona.
 * Maneja la entrada del usuario y las interacciones con la base de datos
 * para realizar operaciones sobre los datos de las personas.
 */
public class ejercicioIModalController {

    @FXML
    private TextField nombreField;

    @FXML
    private TextField apellidosField;

    @FXML
    private TextField edadField;

    private ObservableList<Persona> personasList; // Lista de personas a modificar o agregar
    private Persona personaAEditar = null; // Referencia a la persona a editar, si existe
    private DaoPersona daoPersona; // Objeto DAO para realizar operaciones de base de datos
    private boolean esModificacion = false; // Indica si se está modificando una persona existente

    /**
     * Establece la lista de personas a la que se agregará o modificará la persona.
     *
     * @param personasList Lista de personas que se están gestionando.
     */
    public void setPersonasList(ObservableList<Persona> personasList) {
        this.personasList = personasList;
    }

    /**
     * Establece el DAO para las operaciones de base de datos.
     *
     * @param daoPersona El DAO que gestiona las operaciones sobre la entidad Persona.
     */
    public void setDaoPersona(DaoPersona daoPersona) {
        this.daoPersona = daoPersona;
    }

    /**
     * Establece la persona que se va a editar.
     * Si se llama a este método, se sobreescribe la persona seleccionada
     * y el modal se comportará como un editor en lugar de un formulario de creación.
     *
     * @param persona Persona cuyos datos se van a editar.
     */
    public void setPersonaAEditar(Persona persona) {
        this.personaAEditar = persona;
        this.esModificacion = true; // Indicador de que se está en modo edición
        rellenarCampos(persona); // Rellenar los campos con los datos de la persona a editar
    }

    /**
     * Rellena los campos de texto con los datos de una persona existente para permitir su edición.
     *
     * @param persona Persona cuyos datos se van a editar.
     */
    public void rellenarCampos(Persona persona) {
        nombreField.setText(persona.getNombre());
        apellidosField.setText(persona.getApellido());
        edadField.setText(String.valueOf(persona.getEdad()));
    }

    /**
     * Método que maneja el evento de agregar o editar una persona.
     * Este método es llamado cuando se pulsa el botón "Guardar" en la ventana modal.
     * Valida los campos de entrada y realiza la operación correspondiente en la base de datos.
     */
    @FXML
    private void aniadirPersona() {
        String nombre = nombreField.getText().trim();
        String apellidos = apellidosField.getText().trim();
        String edadText = edadField.getText().trim();
        StringBuilder errores = new StringBuilder(); // Acumula los mensajes de error de validación

        // Validaciones de entrada
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

        // Si hay errores, se muestran y se aborta la operación
        if (errores.length() > 0) {
            mostrarError(errores.toString());
            return;
        }

        try {
            if (esModificacion && personaAEditar != null) {
                // Si estamos modificando, actualizamos los datos de la persona existente en la base de datos
                personaAEditar.setNombre(nombre);
                personaAEditar.setApellido(apellidos);
                personaAEditar.setEdad(edad);
                daoPersona.modificar(personaAEditar);

                // Mostrar el mensaje de éxito
                mostrarInformacion("Persona modificada con éxito.");
            } else {
                // Verificar que la nueva persona no sea duplicada antes de agregarla
                Persona nuevaPersona = new Persona(0, nombre, apellidos, edad);
                for (Persona persona : personasList) {
                    if (persona.equals(nuevaPersona)) {
                        mostrarError("Persona duplicada: Ya existe una persona con los mismos datos.");
                        return;
                    }
                }

                // Agregar la nueva persona a la base de datos
                daoPersona.agregar(nuevaPersona);
                // Agregar la nueva persona a la lista observable
                personasList.add(nuevaPersona);
                mostrarInformacion("Persona agregada con éxito.");
            }
        } catch (SQLException e) {
            mostrarError("Error al interactuar con la base de datos: " + e.getMessage());
        }

        // Cerrar la ventana modal después de completar la operación
        cerrarVentana();
    }

    /**
     * Muestra un mensaje de éxito en una alerta emergente.
     *
     * @param mensaje Mensaje de éxito a mostrar en la alerta.
     */
    private void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait(); // Muestra la alerta y espera a que el usuario la cierre
    }

    /**
     * Muestra un mensaje de error en una alerta emergente.
     *
     * @param mensaje Mensaje de error a mostrar en la alerta.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error en los datos");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait(); // Muestra la alerta y espera a que el usuario la cierre
    }

    /**
     * Cierra la ventana modal.
     */
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) nombreField.getScene().getWindow(); // Obtiene la ventana actual
        stage.close(); // Cierra la ventana
    }
}

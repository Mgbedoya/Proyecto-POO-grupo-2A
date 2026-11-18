/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import java.awt.GridLayout;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import modelo.Cliente;
import modelo.Contacto;
import modelo.Empleado;
import modelo.Empresa;
import modelo.PersonaNatural;
import modelo.Proyecto;

/**
 *
 * @author mari
 */
public class UsaProyecto extends javax.swing.JFrame {

    /**
     * Creates new form UsaProyecto
     */
    public UsaProyecto() {
        initComponents();
        setLocationRelativeTo(null);         
        jTextFieldCodigo.setEditable(false);  
        jTextAreaResultados.setEditable(false);
        actualizarCodigoSugerido();
        habilitarCamposContacto();
    }
    
    private static ArrayList<Proyecto> losProyectos = new ArrayList<>();
    private static ArrayList<Cliente> losClientes = new ArrayList<>();
 
    private static final String SEPARADOR = "------------------------------------------------------------\n";
 
    private void mostrarResultado(String texto) {
        jTextAreaResultados.setText(texto);
        jTextAreaResultados.setCaretPosition(0);
    }
 
    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
 
    private boolean esDuracionValida(String unidad, int duracion) {
        switch (unidad) {
            case "semanas": return duracion >= 1 && duracion <= 3;
            case "meses":   return (duracion >= 1 && duracion <= 11)
                                || (duracion >= 13 && duracion <= 23)
                                || (duracion >= 25 && duracion <= 35);
            case "años":    return duracion >= 1 && duracion <= 3;
            default:        return false;
        }
    }
 
    private Proyecto buscarProyectoDadoCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        for (Proyecto objeto : datos) {
            if (objeto.getCodigo().equals(codigoProyecto)) {
                return objeto;
            }
        }
        return null;
    }
 
    private Cliente buscarClienteDadoId(ArrayList<Cliente> datos, String numeroIdentificacion) {
        for (Cliente objeto : datos) {
            if (objeto.getNumeroIdentificacion().equals(numeroIdentificacion)) {
                return objeto;
            }
        }
        return null;
    }
 
    private boolean existeEmpleado(ArrayList<Empleado> datos, String numeroIdentificacion) {
        for (Empleado objeto : datos) {
            if (objeto.getNumeroIdentificacion().equals(numeroIdentificacion)) {
                return true;
            }
        }
        return false;
    }
 
    private String describirProyecto(Proyecto objeto) {
        return objeto.toString()
             + "\nFecha de finalización: " + objeto.determinarFechaFinalizacion()
             + "\n" + SEPARADOR;
    }
 
    private void actualizarCodigoSugerido() {
        jTextFieldCodigo.setText(generarCodigoProyecto(losProyectos));
    }
 
    private void habilitarCamposContacto() {
        boolean esEmpresa = "Empresa".equals(jComboBoxTipoCliente.getSelectedItem());
        jTextFieldContactoNombre.setEnabled(esEmpresa);
        jTextFieldContactoCargo.setEnabled(esEmpresa);
        if (!esEmpresa) {
            jTextFieldContactoNombre.setText("");
            jTextFieldContactoCargo.setText("");
        }
    }
 
    private void limpiarFormularioRegistro() {
        jTextFieldTitulo.setText("");
        jTextFieldFechaInicio.setText("");
        jComboBoxUnidad.setSelectedIndex(0);
        jTextFieldDuracion.setText("");
        jComboBoxTipoCliente.setSelectedIndex(0);
        jTextFieldClienteId.setText("");
        jTextFieldClienteNombre.setText("");
        jTextFieldClienteCiudad.setText("");
        jTextFieldClienteTelefono.setText("");
        jTextFieldClienteCorreo.setText("");
        jTextFieldGerenteId.setText("");
        jTextFieldGerenteNombre.setText("");
        jTextFieldGerenteTitulo.setText("");
        jTextFieldGerenteAnios.setText("");
        habilitarCamposContacto();
        actualizarCodigoSugerido();
    }
 
    private String obtenerFiltro() {
        String filtro = jTextFieldFiltro.getText().trim();
        if (esVacio(filtro)) {
            JOptionPane.showMessageDialog(this, "Escriba el dato de búsqueda (código o identificación).",
                    "CONSULTAR", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return filtro;
    }
 
    private String obtenerCodigoGestion() {
        String codigo = jTextFieldCodigoGestion.getText().trim();
        if (esVacio(codigo)) {
            JOptionPane.showMessageDialog(this, "Escriba el código del proyecto (ej. 2026-001).",
                    "GESTIONAR PROYECTO", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return codigo;
    }
 
    private void reconstruirListaClientes() {
        losClientes = new ArrayList<>();
        for (Proyecto objeto : losProyectos) {
            if (buscarClienteDadoId(losClientes, objeto.getSuCliente().getNumeroIdentificacion()) == null) {
                losClientes.add(objeto.getSuCliente());
            }
        }
    }
 
    private Empleado solicitarDatosEmpleado(String titulo) {
        JTextField campoId = new JTextField(18);
        JTextField campoNombre = new JTextField(18);
        JTextField campoTitulo = new JTextField(18);
        JTextField campoAnios = new JTextField(18);
        JPanel panel = new JPanel(new GridLayout(4, 2, 6, 6));
        panel.add(new JLabel("Número de identificación:"));
        panel.add(campoId);
        panel.add(new JLabel("Nombre completo:"));
        panel.add(campoNombre);
        panel.add(new JLabel("Título profesional:"));
        panel.add(campoTitulo);
        panel.add(new JLabel("Años de experiencia:"));
        panel.add(campoAnios);
 
        while (true) {
            int opcion = JOptionPane.showConfirmDialog(this, panel, titulo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (opcion != JOptionPane.OK_OPTION) {
                return null;
            }
            String errores = "";
            String id = campoId.getText().trim();
            String nombre = campoNombre.getText().trim();
            String tituloProf = campoTitulo.getText().trim();
            byte anios = 0;
            if (esVacio(id)) { errores += "- Número de identificación: es obligatorio.\n"; }
            if (esVacio(nombre)) { errores += "- Nombre completo: es obligatorio.\n"; }
            if (esVacio(tituloProf)) { errores += "- Título profesional: es obligatorio.\n"; }
            try {
                anios = Byte.parseByte(campoAnios.getText().trim());
                if (anios < 0 || anios > 60) { errores += "- Años de experiencia: debe estar entre 0 y 60.\n"; }
            } catch (NumberFormatException ex) {
                errores += "- Años de experiencia: debe ser un número entero.\n";
            }
            if (errores.isEmpty()) {
                return new Empleado(id, nombre, tituloProf, anios);
            }
            JOptionPane.showMessageDialog(this, "Corrija los siguientes datos:\n" + errores,
                    titulo, JOptionPane.ERROR_MESSAGE);
        }
    }
 
 
    public String generarCodigoProyecto(ArrayList<Proyecto> datos) {
        int anio = LocalDate.now().getYear();
        int consecutivo = 1;
        for (Proyecto objeto : datos) {
            if (objeto.getCodigo().startsWith(anio + "-")) {
                consecutivo++;
            }
        }
        return anio + "-" + String.format("%03d", consecutivo);
    }
 
    public ArrayList<Proyecto> registrarProyecto(ArrayList<Proyecto> datos) {
        String errores = "";
 
        String codigo = generarCodigoProyecto(datos);
 
        String titulo = jTextFieldTitulo.getText().trim();
        if (titulo.length() < 20 || titulo.length() > 100) {
            errores += "- Título: debe tener entre 20 y 100 caracteres (tiene " + titulo.length() + ").\n";
        }
 
        LocalDate fechaInicio = null;
        try {
            fechaInicio = LocalDate.parse(jTextFieldFechaInicio.getText().trim());
        } catch (DateTimeParseException ex) {
            errores += "- Fecha de inicio: use el formato aaaa-mm-dd, ej. 2026-02-15.\n";
        }
 
        String unidad = (String) jComboBoxUnidad.getSelectedItem();
        byte duracion = 0;
        try {
            duracion = Byte.parseByte(jTextFieldDuracion.getText().trim());
            if (!esDuracionValida(unidad, duracion)) {
                errores += "- Duración: valor fuera de rango para " + unidad
                         + " (semanas 1-3; meses 1-11, 13-23, 25-35; años 1-3).\n";
            }
        } catch (NumberFormatException ex) {
            errores += "- Duración: debe ser un número entero.\n";
        }
 
        String tipoCliente = (String) jComboBoxTipoCliente.getSelectedItem();
        String idCliente = jTextFieldClienteId.getText().trim();
        String nombreCliente = jTextFieldClienteNombre.getText().trim();
        String ciudadCliente = jTextFieldClienteCiudad.getText().trim();
        String telefono = jTextFieldClienteTelefono.getText().trim();
        String correo = jTextFieldClienteCorreo.getText().trim();
        String nombreContacto = jTextFieldContactoNombre.getText().trim();
        String cargoContacto = jTextFieldContactoCargo.getText().trim();
 
        if (esVacio(idCliente)) { errores += "- Cliente: la identificación es obligatoria.\n"; }
        if (esVacio(nombreCliente)) { errores += "- Cliente: el nombre es obligatorio.\n"; }
        if (esVacio(ciudadCliente)) { errores += "- Cliente: la ciudad es obligatoria.\n"; }
        if (esVacio(telefono)) { errores += "- Cliente: el teléfono es obligatorio.\n"; }
        if (esVacio(correo) || !correo.contains("@")) { errores += "- Cliente: el correo es obligatorio y debe contener @.\n"; }
        if (tipoCliente.equals("Empresa") && (esVacio(nombreContacto) || esVacio(cargoContacto))) {
            errores += "- Contacto: nombre y cargo son obligatorios para una empresa.\n";
        }
 
        String idGerente = jTextFieldGerenteId.getText().trim();
        String nombreGerente = jTextFieldGerenteNombre.getText().trim();
        String tituloGerente = jTextFieldGerenteTitulo.getText().trim();
        byte aniosGerente = 0;
        if (esVacio(idGerente)) { errores += "- Gerente: la identificación es obligatoria.\n"; }
        if (esVacio(nombreGerente)) { errores += "- Gerente: el nombre es obligatorio.\n"; }
        if (esVacio(tituloGerente)) { errores += "- Gerente: el título profesional es obligatorio.\n"; }
        try {
            aniosGerente = Byte.parseByte(jTextFieldGerenteAnios.getText().trim());
            if (aniosGerente < 0 || aniosGerente > 60) { errores += "- Gerente: los años de experiencia deben estar entre 0 y 60.\n"; }
        } catch (NumberFormatException ex) {
            errores += "- Gerente: los años de experiencia deben ser un número entero.\n";
        }
 
        if (!errores.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se registró el proyecto. Corrija:\n\n" + errores,
                    "REGISTRAR PROYECTO", JOptionPane.ERROR_MESSAGE);
            return datos;
        }
 
        Cliente objCliente = buscarClienteDadoId(losClientes, idCliente);
        if (objCliente == null) {
            if (tipoCliente.equals("Empresa")) {
                Contacto objContacto = new Contacto(nombreContacto, cargoContacto, telefono, correo);
                objCliente = new Empresa(idCliente, nombreCliente, ciudadCliente, objContacto);
            } else {
                objCliente = new PersonaNatural(idCliente, nombreCliente, ciudadCliente, telefono, correo);
            }
            losClientes.add(objCliente);
        } else {
            JOptionPane.showMessageDialog(this, "El cliente " + objCliente.getNombre()
                    + " ya estaba registrado: el proyecto se asocia a ese cliente.",
                    "REGISTRAR PROYECTO", JOptionPane.INFORMATION_MESSAGE);
        }
 
        Empleado objGerente = new Empleado(idGerente, nombreGerente, tituloGerente, aniosGerente);
        Proyecto objProyecto = new Proyecto(codigo, titulo, fechaInicio, unidad, duracion, objCliente, objGerente);
        datos.add(objProyecto);
 
        return datos;
    }
 
    public String consultarTodosProyectos(ArrayList<Proyecto> datos) {
        String resultado = "CONSULTAR TODOS LOS PROYECTOS\n" + SEPARADOR;
        if (datos.isEmpty()) {
            return resultado + "No hay proyectos registrados.";
        }
        for (Proyecto objeto : datos) {
            resultado += describirProyecto(objeto);
        }
        return resultado;
    }
 
    public String consultarProyectosEnCurso(ArrayList<Proyecto> datos) {
        String resultado = "CONSULTAR PROYECTOS EN CURSO\n" + SEPARADOR;
        if (datos.isEmpty()) {
            return resultado + "No hay proyectos registrados.";
        }
        boolean hayCoincidencias = false;
        for (Proyecto objeto : datos) {
            if (objeto.getEstado().equals("EN CURSO")) {
                resultado += describirProyecto(objeto);
                hayCoincidencias = true;
            }
        }
        if (!hayCoincidencias) {
            resultado += "No hay proyectos en estado EN CURSO.";
        }
        return resultado;
    }
 
    public String consultarPrimerProyecto(ArrayList<Proyecto> datos) {
        String resultado = "CONSULTAR PRIMER PROYECTO REGISTRADO\n" + SEPARADOR;
        if (datos.isEmpty()) {
            return resultado + "No hay proyectos registrados.";
        }
        return resultado + describirProyecto(datos.get(0));
    }
 
    public String consultarUltimoProyecto(ArrayList<Proyecto> datos) {
        String resultado = "CONSULTAR ÚLTIMO PROYECTO REGISTRADO\n" + SEPARADOR;
        if (datos.isEmpty()) {
            return resultado + "No hay proyectos registrados.";
        }
        return resultado + describirProyecto(datos.get(datos.size() - 1));
    }
 
    public String consultarProyectoDadoCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        String resultado = "CONSULTAR PROYECTO DADO CÓDIGO " + codigoProyecto + "\n" + SEPARADOR;
        if (datos.isEmpty()) {
            return resultado + "No hay proyectos registrados.";
        }
        Proyecto objeto = buscarProyectoDadoCodigo(datos, codigoProyecto);
        if (objeto == null) {
            return resultado + "No existe un proyecto con el código " + codigoProyecto + ".";
        }
        return resultado + describirProyecto(objeto);
    }
 
    public String consultarGerenteProyectoDadoCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        String resultado = "GERENTE DEL PROYECTO " + codigoProyecto + "\n" + SEPARADOR;
        if (datos.isEmpty()) {
            return resultado + "No hay proyectos registrados.";
        }
        Proyecto objeto = buscarProyectoDadoCodigo(datos, codigoProyecto);
        if (objeto == null) {
            return resultado + "No existe un proyecto con el código " + codigoProyecto + ".";
        }
        return resultado + objeto.getSuGerente().toString();
    }
 
    public String consultarEquipoProyectoDadoCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        String resultado = "EQUIPO DEL PROYECTO " + codigoProyecto + "\n" + SEPARADOR;
        if (datos.isEmpty()) {
            return resultado + "No hay proyectos registrados.";
        }
        Proyecto objeto = buscarProyectoDadoCodigo(datos, codigoProyecto);
        if (objeto == null) {
            return resultado + "No existe un proyecto con el código " + codigoProyecto + ".";
        }
        if (objeto.getSuEquipo().isEmpty()) {
            return resultado + "El proyecto aún no tiene integrantes registrados.";
        }
        int numero = 1;
        for (Empleado integrante : objeto.getSuEquipo()) {
            resultado += "Integrante " + numero + "\n" + integrante + "\n" + SEPARADOR;
            numero++;
        }
        return resultado;
    }
 
    public String registrarIntegranteProyectoDadoCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        if (datos.isEmpty()) {
            return "No hay proyectos registrados.";
        }
        Proyecto objeto = buscarProyectoDadoCodigo(datos, codigoProyecto);
        if (objeto == null) {
            return "No existe un proyecto con el código " + codigoProyecto + ".";
        }
        Empleado nuevo = solicitarDatosEmpleado("Registrar integrante - proyecto " + codigoProyecto);
        if (nuevo == null) {
            return "Operación cancelada: no se registró el integrante.";
        }
        if (existeEmpleado(objeto.getSuEquipo(), nuevo.getNumeroIdentificacion())) {
            return "El empleado " + nuevo.getNumeroIdentificacion() + " ya hace parte del equipo del proyecto " + codigoProyecto + ".";
        }
        objeto.agregarIntegrante(nuevo);
        return "Integrante registrado con éxito en el proyecto " + codigoProyecto + ".\n" + SEPARADOR + nuevo;
    }
 
    public String registrarEquipoProyectoDadoCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        if (datos.isEmpty()) {
            return "No hay proyectos registrados.";
        }
        Proyecto objeto = buscarProyectoDadoCodigo(datos, codigoProyecto);
        if (objeto == null) {
            return "No existe un proyecto con el código " + codigoProyecto + ".";
        }
        String entrada = JOptionPane.showInputDialog(this, "¿Cuántos integrantes va a registrar?",
                "Registrar equipo - proyecto " + codigoProyecto, JOptionPane.QUESTION_MESSAGE);
        if (entrada == null) {
            return "Operación cancelada: el equipo no fue modificado.";
        }
        int cantidad;
        try {
            cantidad = Integer.parseInt(entrada.trim());
        } catch (NumberFormatException ex) {
            return "Cantidad inválida: debe ser un número entero. El equipo no fue modificado.";
        }
        if (cantidad < 1) {
            return "La cantidad debe ser mayor que cero. El equipo no fue modificado.";
        }
        ArrayList<Empleado> equipo = new ArrayList<>();
        int i = 1;
        while (i <= cantidad) {
            Empleado integrante = solicitarDatosEmpleado("Integrante " + i + " de " + cantidad);
            if (integrante == null) {
                return "Operación cancelada: el equipo no fue modificado.";
            }
            if (existeEmpleado(equipo, integrante.getNumeroIdentificacion())) {
                JOptionPane.showMessageDialog(this, "Ese empleado ya fue ingresado en este equipo. Registre otro.",
                        "Registrar equipo", JOptionPane.WARNING_MESSAGE);
            } else {
                equipo.add(integrante);
                i++;
            }
        }
        objeto.setSuEquipo(equipo);
        return "Equipo registrado con éxito en el proyecto " + codigoProyecto + " (" + cantidad + " integrantes).\n"
             + "Nota: reemplaza al equipo anterior.";
    }
 
    public String actualizarEstadoProyectoDadoCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        if (datos.isEmpty()) {
            return "No hay proyectos registrados.";
        }
        Proyecto objeto = buscarProyectoDadoCodigo(datos, codigoProyecto);
        if (objeto == null) {
            return "No existe un proyecto con el código " + codigoProyecto + ".";
        }
        String estadoNuevo = (String) jComboBoxEstado.getSelectedItem();
        String estadoAnterior = objeto.getEstado();
        objeto.setEstado(estadoNuevo);
        return "Proyecto " + codigoProyecto + ": estado actualizado de " + estadoAnterior + " a " + estadoNuevo + ".";
    }
 
    public String eliminarIntegranteProyectoDadoCodigo(ArrayList<Proyecto> datos, String codigoProyecto) {
        if (datos.isEmpty()) {
            return "No hay proyectos registrados.";
        }
        Proyecto objeto = buscarProyectoDadoCodigo(datos, codigoProyecto);
        if (objeto == null) {
            return "No existe un proyecto con el código " + codigoProyecto + ".";
        }
        String id = JOptionPane.showInputDialog(this, "Número de identificación del integrante a retirar:",
                "Eliminar integrante - proyecto " + codigoProyecto, JOptionPane.QUESTION_MESSAGE);
        if (esVacio(id)) {
            return "Operación cancelada: no se eliminó ningún integrante.";
        }
        Empleado retirado = objeto.eliminarIntegrante(id.trim());
        if (retirado == null) {
            return "El proyecto " + codigoProyecto + " no tiene un integrante con identificación " + id.trim() + ".";
        }
        return "Integrante retirado del proyecto " + codigoProyecto + ".\n" + SEPARADOR + retirado;
    }
 
    public String consultarProyectosDadoNumIDEGerente(ArrayList<Proyecto> datos, String numIDEGerente) {
        String resultado = "PROYECTOS DEL GERENTE " + numIDEGerente + "\n" + SEPARADOR;
        if (datos.isEmpty()) {
            return resultado + "No hay proyectos registrados.";
        }
        boolean hayCoincidencias = false;
        for (Proyecto objeto : datos) {
            if (objeto.getSuGerente().getNumeroIdentificacion().equals(numIDEGerente)) {
                resultado += describirProyecto(objeto);
                hayCoincidencias = true;
            }
        }
        if (!hayCoincidencias) {
            resultado += "No hay proyectos con ese gerente.";
        }
        return resultado;
    }
 
    public String consultarProyectosDadoNumIDECliente(ArrayList<Proyecto> datos, String numIDECliente) {
        String resultado = "PROYECTOS DEL CLIENTE " + numIDECliente + "\n" + SEPARADOR;
        if (datos.isEmpty()) {
            return resultado + "No hay proyectos registrados.";
        }
        boolean hayCoincidencias = false;
        for (Proyecto objeto : datos) {
            if (objeto.getSuCliente().getNumeroIdentificacion().equals(numIDECliente)) {
                resultado += describirProyecto(objeto);
                hayCoincidencias = true;
            }
        }
        if (!hayCoincidencias) {
            resultado += "No hay proyectos con ese cliente.";
        }
        return resultado;
    }
 
    public String consultarProyectosDadoTipoCliente(ArrayList<Proyecto> datos, String tipoCliente) {
        String resultado = "PROYECTOS CON CLIENTE TIPO " + tipoCliente.toUpperCase() + "\n" + SEPARADOR;
        if (datos.isEmpty()) {
            return resultado + "No hay proyectos registrados.";
        }
        boolean hayCoincidencias = false;
        for (Proyecto objeto : datos) {
            if (objeto.getSuCliente().getClass().getSimpleName().equalsIgnoreCase(tipoCliente)) {
                resultado += describirProyecto(objeto);
                hayCoincidencias = true;
            }
        }
        if (!hayCoincidencias) {
            resultado += "No hay proyectos con clientes de ese tipo.";
        }
        return resultado;
    }
 
    public void generarArchivoDeObjetosProyectos(ArrayList<Proyecto> datos) {
        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay proyectos registrados: no se generó el archivo.",
                    "GENERAR ARCHIVO DE PROYECTOS", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        File archivo = new File("datosProyectos");
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo))) {
            for (Proyecto objeto : datos) {
                salida.writeObject(objeto);
            }
            JOptionPane.showMessageDialog(this, "Archivo generado con " + datos.size() + " proyecto(s).\n"
                    + archivo.getAbsolutePath(), "GENERAR ARCHIVO DE PROYECTOS", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el archivo:\n" + ex.getMessage(),
                    "GENERAR ARCHIVO DE PROYECTOS", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    public ArrayList<Proyecto> leerArchivoDeObjetosProyectos() {
        ArrayList<Proyecto> datos = new ArrayList<>();
        File archivo = new File("datosProyectos");
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(this, "No existe el archivo datosProyectos. Genérelo primero.",
                    "LEER ARCHIVO DE PROYECTOS", JOptionPane.WARNING_MESSAGE);
            return datos;
        }
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {
            while (true) {  
                datos.add((Proyecto) entrada.readObject());
            }
        } catch (EOFException eof) {
            
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo leer el archivo:\n" + ex.getMessage(),
                    "LEER ARCHIVO DE PROYECTOS", JOptionPane.ERROR_MESSAGE);
        }
        return datos;
    }
 
    public void generarArchivoDeObjetosClientes(ArrayList<Proyecto> datos) {
        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay proyectos registrados: no se generó el archivo.",
                    "GENERAR ARCHIVO DE CLIENTES", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        File archivo = new File("datosClientes");
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo))) {
            for (Proyecto objeto : datos) {
                salida.writeObject(objeto.getSuCliente());
            }
            JOptionPane.showMessageDialog(this, "Archivo generado.\n" + archivo.getAbsolutePath(),
                    "GENERAR ARCHIVO DE CLIENTES", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el archivo:\n" + ex.getMessage(),
                    "GENERAR ARCHIVO DE CLIENTES", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    public ArrayList<Cliente> leerArchivoDeObjetosClientes() {
        ArrayList<Cliente> datos = new ArrayList<>();
        File archivo = new File("datosClientes");
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(this, "No existe el archivo datosClientes. Genérelo primero.",
                    "LEER ARCHIVO DE CLIENTES", JOptionPane.WARNING_MESSAGE);
            return datos;
        }
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {
            while (true) {
                Cliente objeto = (Cliente) entrada.readObject();
                if (buscarClienteDadoId(datos, objeto.getNumeroIdentificacion()) == null) {
                    datos.add(objeto);
                }
            }
        } catch (EOFException eof) {
            
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo leer el archivo:\n" + ex.getMessage(),
                    "LEER ARCHIVO DE CLIENTES", JOptionPane.ERROR_MESSAGE);
        }
        return datos;
    }
 
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaResultados = new javax.swing.JTextArea();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelRegistro = new javax.swing.JPanel();
        jPanelProyecto = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldCodigo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldTitulo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldFechaInicio = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxUnidad = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jTextFieldDuracion = new javax.swing.JTextField();
        jPanelCliente = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jComboBoxTipoCliente = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldClienteId = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldClienteNombre = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldClienteCiudad = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldClienteTelefono = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextFieldClienteCorreo = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldContactoNombre = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTextFieldContactoCargo = new javax.swing.JTextField();
        jPanelGerente = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldGerenteId = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jTextFieldGerenteNombre = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextFieldGerenteTitulo = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextFieldGerenteAnios = new javax.swing.JTextField();
        jButtonRegistrarProyecto = new javax.swing.JButton();
        jButtonLimpiarFormulario = new javax.swing.JButton();
        jPanelConsultas = new javax.swing.JPanel();
        jPanelConsultasGenerales = new javax.swing.JPanel();
        jButtonConsultarTodos = new javax.swing.JButton();
        jButtonConsultarEnCurso = new javax.swing.JButton();
        jButtonConsultarPrimero = new javax.swing.JButton();
        jButtonConsultarUltimo = new javax.swing.JButton();
        jPanelConsultasFiltro = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jTextFieldFiltro = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jComboBoxTipoClienteFiltro = new javax.swing.JComboBox<>();
        jButtonConsultarProyectoCodigo = new javax.swing.JButton();
        jButtonConsultarEquipo = new javax.swing.JButton();
        jButtonConsultarGerente = new javax.swing.JButton();
        jButtonConsultarPorGerente = new javax.swing.JButton();
        jButtonConsultarPorCliente = new javax.swing.JButton();
        jButtonConsultarPorTipoCliente = new javax.swing.JButton();
        jPanelGestion = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextFieldCodigoGestion = new javax.swing.JTextField();
        jButtonRegistrarIntegrante = new javax.swing.JButton();
        jButtonRegistrarEquipo = new javax.swing.JButton();
        jButtonEliminarIntegrante = new javax.swing.JButton();
        jComboBoxEstado = new javax.swing.JComboBox<>();
        jButtonActualizarEstado = new javax.swing.JButton();
        jPanelArchivos = new javax.swing.JPanel();
        jButtonGenerarArchivoProyectos = new javax.swing.JButton();
        jButtonGenerarArchivoClientes = new javax.swing.JButton();
        jButtonLeerArchivoClientes = new javax.swing.JButton();
        jButtonLeerArchivoProyectos = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gestión de Proyectos");

        jTextAreaResultados.setEditable(false);
        jTextAreaResultados.setColumns(80);
        jTextAreaResultados.setRows(6);
        jScrollPane1.setViewportView(jTextAreaResultados);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.setName(""); // NOI18N

        jPanelProyecto.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos del proyecto"));

        jLabel1.setText("Código");

        jTextFieldCodigo.setEditable(false);
        jTextFieldCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldCodigoActionPerformed(evt);
            }
        });

        jLabel2.setText("Titulo");

        jLabel3.setText("Fecha inicio (aaaa-mm-dd)");

        jTextFieldFechaInicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldFechaInicioActionPerformed(evt);
            }
        });

        jLabel4.setText("Unidad");

        jComboBoxUnidad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "semanas", "meses", "años" }));
        jComboBoxUnidad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxUnidadActionPerformed(evt);
            }
        });

        jLabel5.setText("Duracion");

        javax.swing.GroupLayout jPanelProyectoLayout = new javax.swing.GroupLayout(jPanelProyecto);
        jPanelProyecto.setLayout(jPanelProyectoLayout);
        jPanelProyectoLayout.setHorizontalGroup(
            jPanelProyectoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProyectoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelProyectoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelProyectoLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxUnidad, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProyectoLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldDuracion))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProyectoLayout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelProyectoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanelProyectoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanelProyectoLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldCodigo))
                            .addGroup(jPanelProyectoLayout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanelProyectoLayout.setVerticalGroup(
            jPanelProyectoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProyectoLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanelProyectoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelProyectoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextFieldTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanelProyectoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldFechaInicio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelProyectoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jComboBoxUnidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelProyectoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextFieldDuracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelCliente.setBorder(javax.swing.BorderFactory.createTitledBorder("Cliente"));

        jLabel6.setText("Tipo Cliente");

        jComboBoxTipoCliente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Empresa", "PersonaNatural" }));
        jComboBoxTipoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTipoClienteActionPerformed(evt);
            }
        });

        jLabel7.setText("Identificacion");

        jLabel8.setText("Nombre");

        jLabel9.setText("Ciudad");

        jLabel10.setText("Telefono");

        jLabel11.setText("Correo");

        jLabel12.setText("Nombre contacto");

        jLabel13.setText("Cargo Contacto");

        javax.swing.GroupLayout jPanelClienteLayout = new javax.swing.GroupLayout(jPanelCliente);
        jPanelCliente.setLayout(jPanelClienteLayout);
        jPanelClienteLayout.setHorizontalGroup(
            jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelClienteLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxTipoCliente, 0, 353, Short.MAX_VALUE))
                    .addGroup(jPanelClienteLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldContactoCargo))
                    .addGroup(jPanelClienteLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldClienteId))
                    .addGroup(jPanelClienteLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldClienteNombre))
                    .addGroup(jPanelClienteLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldClienteCiudad))
                    .addGroup(jPanelClienteLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldClienteTelefono))
                    .addGroup(jPanelClienteLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldClienteCorreo))
                    .addGroup(jPanelClienteLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldContactoNombre)))
                .addContainerGap())
        );
        jPanelClienteLayout.setVerticalGroup(
            jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelClienteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBoxTipoCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldClienteId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextFieldClienteNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextFieldClienteCiudad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextFieldClienteTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jTextFieldClienteCorreo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jTextFieldContactoNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTextFieldContactoCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jPanelGerente.setBorder(javax.swing.BorderFactory.createTitledBorder("Gerente"));

        jLabel14.setText("Identificacion");

        jTextFieldGerenteId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldGerenteIdActionPerformed(evt);
            }
        });

        jLabel15.setText("Nombre completo");

        jLabel16.setText("Titulo profesional");

        jTextFieldGerenteTitulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldGerenteTituloActionPerformed(evt);
            }
        });

        jLabel17.setText("Años de experiencia");

        javax.swing.GroupLayout jPanelGerenteLayout = new javax.swing.GroupLayout(jPanelGerente);
        jPanelGerente.setLayout(jPanelGerenteLayout);
        jPanelGerenteLayout.setHorizontalGroup(
            jPanelGerenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGerenteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelGerenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGerenteLayout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldGerenteAnios, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))
                    .addGroup(jPanelGerenteLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldGerenteId))
                    .addGroup(jPanelGerenteLayout.createSequentialGroup()
                        .addGroup(jPanelGerenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelGerenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldGerenteTitulo)
                            .addComponent(jTextFieldGerenteNombre))))
                .addContainerGap())
        );
        jPanelGerenteLayout.setVerticalGroup(
            jPanelGerenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGerenteLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanelGerenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jTextFieldGerenteId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelGerenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jTextFieldGerenteNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanelGerenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldGerenteTitulo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelGerenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTextFieldGerenteAnios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jButtonRegistrarProyecto.setText("Registrar proyecto");
        jButtonRegistrarProyecto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegistrarProyectoActionPerformed(evt);
            }
        });

        jButtonLimpiarFormulario.setText("Limpiar formulario");
        jButtonLimpiarFormulario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLimpiarFormularioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelRegistroLayout = new javax.swing.GroupLayout(jPanelRegistro);
        jPanelRegistro.setLayout(jPanelRegistroLayout);
        jPanelRegistroLayout.setHorizontalGroup(
            jPanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRegistroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelProyecto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelGerente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRegistroLayout.createSequentialGroup()
                        .addComponent(jButtonRegistrarProyecto, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonLimpiarFormulario, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanelCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanelRegistroLayout.setVerticalGroup(
            jPanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRegistroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRegistroLayout.createSequentialGroup()
                        .addComponent(jPanelCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButtonRegistrarProyecto)
                            .addComponent(jButtonLimpiarFormulario)))
                    .addGroup(jPanelRegistroLayout.createSequentialGroup()
                        .addComponent(jPanelProyecto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanelGerente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(206, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Registrar proyecto", jPanelRegistro);

        jPanelConsultasGenerales.setBorder(javax.swing.BorderFactory.createTitledBorder("Consultas generales"));

        jButtonConsultarTodos.setText("Todos los proyectos");
        jButtonConsultarTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarTodosActionPerformed(evt);
            }
        });

        jButtonConsultarEnCurso.setText("Proyectos EN CURSO");
        jButtonConsultarEnCurso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarEnCursoActionPerformed(evt);
            }
        });

        jButtonConsultarPrimero.setText("Primer proyecto");
        jButtonConsultarPrimero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarPrimeroActionPerformed(evt);
            }
        });

        jButtonConsultarUltimo.setText("Ultimo proyecto");
        jButtonConsultarUltimo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarUltimoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelConsultasGeneralesLayout = new javax.swing.GroupLayout(jPanelConsultasGenerales);
        jPanelConsultasGenerales.setLayout(jPanelConsultasGeneralesLayout);
        jPanelConsultasGeneralesLayout.setHorizontalGroup(
            jPanelConsultasGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelConsultasGeneralesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelConsultasGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonConsultarTodos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonConsultarEnCurso, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                    .addComponent(jButtonConsultarPrimero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonConsultarUltimo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanelConsultasGeneralesLayout.setVerticalGroup(
            jPanelConsultasGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelConsultasGeneralesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonConsultarTodos)
                .addGap(18, 18, 18)
                .addComponent(jButtonConsultarEnCurso)
                .addGap(18, 18, 18)
                .addComponent(jButtonConsultarPrimero)
                .addGap(18, 18, 18)
                .addComponent(jButtonConsultarUltimo)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanelConsultasFiltro.setBorder(javax.swing.BorderFactory.createTitledBorder("Consultas con filtro"));

        jLabel18.setText("Dato de busqueda (codigo o identificacion");

        jLabel19.setText("Tipo de cliente");

        jComboBoxTipoClienteFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Empresa", "PersonaNatural" }));
        jComboBoxTipoClienteFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTipoClienteFiltroActionPerformed(evt);
            }
        });

        jButtonConsultarProyectoCodigo.setText("Proyecto por codigo");
        jButtonConsultarProyectoCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarProyectoCodigoActionPerformed(evt);
            }
        });

        jButtonConsultarEquipo.setText("Equipo del proyecto");
        jButtonConsultarEquipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarEquipoActionPerformed(evt);
            }
        });

        jButtonConsultarGerente.setText("Gerente del proyecto");
        jButtonConsultarGerente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarGerenteActionPerformed(evt);
            }
        });

        jButtonConsultarPorGerente.setText("Proyectos de un gerente");
        jButtonConsultarPorGerente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarPorGerenteActionPerformed(evt);
            }
        });

        jButtonConsultarPorCliente.setText("Proyectos de un cliente");
        jButtonConsultarPorCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarPorClienteActionPerformed(evt);
            }
        });

        jButtonConsultarPorTipoCliente.setText("Proyectos por tipo de cliente");
        jButtonConsultarPorTipoCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConsultarPorTipoClienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelConsultasFiltroLayout = new javax.swing.GroupLayout(jPanelConsultasFiltro);
        jPanelConsultasFiltro.setLayout(jPanelConsultasFiltroLayout);
        jPanelConsultasFiltroLayout.setHorizontalGroup(
            jPanelConsultasFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelConsultasFiltroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelConsultasFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelConsultasFiltroLayout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldFiltro, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                    .addGroup(jPanelConsultasFiltroLayout.createSequentialGroup()
                        .addGroup(jPanelConsultasFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButtonConsultarPorTipoCliente, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                            .addComponent(jButtonConsultarPorCliente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonConsultarPorGerente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonConsultarEquipo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonConsultarGerente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelConsultasFiltroLayout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBoxTipoClienteFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonConsultarProyectoCodigo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanelConsultasFiltroLayout.setVerticalGroup(
            jPanelConsultasFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelConsultasFiltroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelConsultasFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jTextFieldFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelConsultasFiltroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jComboBoxTipoClienteFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonConsultarProyectoCodigo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonConsultarGerente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonConsultarEquipo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonConsultarPorGerente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonConsultarPorCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonConsultarPorTipoCliente)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelConsultasLayout = new javax.swing.GroupLayout(jPanelConsultas);
        jPanelConsultas.setLayout(jPanelConsultasLayout);
        jPanelConsultasLayout.setHorizontalGroup(
            jPanelConsultasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelConsultasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelConsultasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelConsultasGenerales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelConsultasFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(407, Short.MAX_VALUE))
        );
        jPanelConsultasLayout.setVerticalGroup(
            jPanelConsultasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelConsultasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelConsultasGenerales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelConsultasFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(107, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Consultas", jPanelConsultas);

        jLabel21.setText("Código del proyecto:");

        jLabel22.setText("Nuevo estado:");

        jButtonRegistrarIntegrante.setText("Registrar integrante");
        jButtonRegistrarIntegrante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegistrarIntegranteActionPerformed(evt);
            }
        });

        jButtonRegistrarEquipo.setText("Registrar equipo");
        jButtonRegistrarEquipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRegistrarEquipoActionPerformed(evt);
            }
        });

        jButtonEliminarIntegrante.setText("Eliminar integrante");
        jButtonEliminarIntegrante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEliminarIntegranteActionPerformed(evt);
            }
        });

        jComboBoxEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "INICIADO", "EN CURSO", "CANCELADO", "FINALIZADO", "APLAZADO" }));

        jButtonActualizarEstado.setText("Actualizar Estado");
        jButtonActualizarEstado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActualizarEstadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelGestionLayout = new javax.swing.GroupLayout(jPanelGestion);
        jPanelGestion.setLayout(jPanelGestionLayout);
        jPanelGestionLayout.setHorizontalGroup(
            jPanelGestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGestionLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanelGestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelGestionLayout.createSequentialGroup()
                        .addComponent(jButtonRegistrarIntegrante)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonRegistrarEquipo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButtonEliminarIntegrante))
                    .addGroup(jPanelGestionLayout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBoxEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonActualizarEstado))
                    .addGroup(jPanelGestionLayout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldCodigoGestion, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(456, Short.MAX_VALUE))
        );
        jPanelGestionLayout.setVerticalGroup(
            jPanelGestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelGestionLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanelGestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jTextFieldCodigoGestion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanelGestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRegistrarIntegrante)
                    .addComponent(jButtonRegistrarEquipo)
                    .addComponent(jButtonEliminarIntegrante))
                .addGap(30, 30, 30)
                .addGroup(jPanelGestionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jComboBoxEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonActualizarEstado))
                .addContainerGap(451, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Gestión de proyecto", jPanelGestion);

        jButtonGenerarArchivoProyectos.setText("Generar archivo de proyectos\n");
        jButtonGenerarArchivoProyectos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenerarArchivoProyectosActionPerformed(evt);
            }
        });

        jButtonGenerarArchivoClientes.setText("Generar archivo de clientes ");
        jButtonGenerarArchivoClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGenerarArchivoClientesActionPerformed(evt);
            }
        });

        jButtonLeerArchivoClientes.setText("Leer archivo de clientes ");
        jButtonLeerArchivoClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLeerArchivoClientesActionPerformed(evt);
            }
        });

        jButtonLeerArchivoProyectos.setText("Leer archivo de proyectos ");
        jButtonLeerArchivoProyectos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLeerArchivoProyectosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelArchivosLayout = new javax.swing.GroupLayout(jPanelArchivos);
        jPanelArchivos.setLayout(jPanelArchivosLayout);
        jPanelArchivosLayout.setHorizontalGroup(
            jPanelArchivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArchivosLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanelArchivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonGenerarArchivoProyectos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonLeerArchivoProyectos, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelArchivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonLeerArchivoClientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonGenerarArchivoClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(481, Short.MAX_VALUE))
        );
        jPanelArchivosLayout.setVerticalGroup(
            jPanelArchivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelArchivosLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanelArchivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonGenerarArchivoProyectos, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonGenerarArchivoClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelArchivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonLeerArchivoProyectos, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonLeerArchivoClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(479, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Archivos", jPanelArchivos);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.PAGE_START);
        jTabbedPane1.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldFechaInicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFechaInicioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldFechaInicioActionPerformed

    private void jTextFieldCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCodigoActionPerformed

    private void jComboBoxUnidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxUnidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxUnidadActionPerformed

    private void jTextFieldGerenteIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldGerenteIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldGerenteIdActionPerformed

    private void jTextFieldGerenteTituloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldGerenteTituloActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldGerenteTituloActionPerformed

    private void jComboBoxTipoClienteFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTipoClienteFiltroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxTipoClienteFiltroActionPerformed

    private void jButtonConsultarTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarTodosActionPerformed
    mostrarResultado(consultarTodosProyectos(losProyectos));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonConsultarTodosActionPerformed

    private void jButtonRegistrarProyectoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistrarProyectoActionPerformed
     int cantidadAntes = losProyectos.size();
        losProyectos = registrarProyecto(losProyectos);
        if (losProyectos.size() > cantidadAntes) {            // se registró uno nuevo
            Proyecto nuevo = losProyectos.get(losProyectos.size() - 1);
            JOptionPane.showMessageDialog(this, "Proyecto " + nuevo.getCodigo() + " registrado con éxito.");
            limpiarFormularioRegistro();
            mostrarResultado(consultarUltimoProyecto(losProyectos));
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonRegistrarProyectoActionPerformed

    private void jButtonLimpiarFormularioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLimpiarFormularioActionPerformed
        limpiarFormularioRegistro();        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLimpiarFormularioActionPerformed

    private void jButtonConsultarEnCursoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarEnCursoActionPerformed
        mostrarResultado(consultarProyectosEnCurso(losProyectos));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonConsultarEnCursoActionPerformed

    private void jButtonConsultarPrimeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarPrimeroActionPerformed
        mostrarResultado(consultarPrimerProyecto(losProyectos));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonConsultarPrimeroActionPerformed

    private void jButtonConsultarUltimoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarUltimoActionPerformed
        mostrarResultado(consultarUltimoProyecto(losProyectos));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonConsultarUltimoActionPerformed

    private void jButtonConsultarProyectoCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarProyectoCodigoActionPerformed
        String filtro = obtenerFiltro();
        if (filtro == null) { return; }
        mostrarResultado(consultarProyectoDadoCodigo(losProyectos, filtro));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonConsultarProyectoCodigoActionPerformed

    private void jButtonConsultarGerenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarGerenteActionPerformed
        String filtro = obtenerFiltro();
        if (filtro == null) { return; }
        mostrarResultado(consultarGerenteProyectoDadoCodigo(losProyectos, filtro));
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonConsultarGerenteActionPerformed

    private void jButtonConsultarEquipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarEquipoActionPerformed
        String filtro = obtenerFiltro();
        if (filtro == null) { return; }
        mostrarResultado(consultarEquipoProyectoDadoCodigo(losProyectos, filtro));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonConsultarEquipoActionPerformed

    private void jButtonConsultarPorGerenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarPorGerenteActionPerformed
        String filtro = obtenerFiltro();
        if (filtro == null) { return; }
        mostrarResultado(consultarProyectosDadoNumIDEGerente(losProyectos, filtro));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonConsultarPorGerenteActionPerformed

    private void jButtonRegistrarIntegranteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistrarIntegranteActionPerformed
        String codigo = obtenerCodigoGestion();
        if (codigo == null) { return; }
        mostrarResultado(registrarIntegranteProyectoDadoCodigo(losProyectos, codigo));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonRegistrarIntegranteActionPerformed

    private void jButtonConsultarPorClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarPorClienteActionPerformed
        String filtro = obtenerFiltro();
        if (filtro == null) { return; }
        mostrarResultado(consultarProyectosDadoNumIDECliente(losProyectos, filtro));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonConsultarPorClienteActionPerformed

    private void jButtonConsultarPorTipoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConsultarPorTipoClienteActionPerformed
        String tipo = (String) jComboBoxTipoClienteFiltro.getSelectedItem();
        mostrarResultado(consultarProyectosDadoTipoCliente(losProyectos, tipo));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonConsultarPorTipoClienteActionPerformed

    private void jButtonRegistrarEquipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRegistrarEquipoActionPerformed
        String codigo = obtenerCodigoGestion();
        if (codigo == null) { return; }
        mostrarResultado(registrarEquipoProyectoDadoCodigo(losProyectos, codigo));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonRegistrarEquipoActionPerformed

    private void jButtonEliminarIntegranteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEliminarIntegranteActionPerformed
        String codigo = obtenerCodigoGestion();
        if (codigo == null) { return; }
        mostrarResultado(eliminarIntegranteProyectoDadoCodigo(losProyectos, codigo));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonEliminarIntegranteActionPerformed

    private void jButtonActualizarEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActualizarEstadoActionPerformed
        String codigo = obtenerCodigoGestion();
        if (codigo == null) { return; }
        mostrarResultado(actualizarEstadoProyectoDadoCodigo(losProyectos, codigo));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonActualizarEstadoActionPerformed

    private void jButtonGenerarArchivoProyectosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerarArchivoProyectosActionPerformed
        generarArchivoDeObjetosProyectos(losProyectos);        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonGenerarArchivoProyectosActionPerformed

    private void jButtonGenerarArchivoClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGenerarArchivoClientesActionPerformed
        generarArchivoDeObjetosClientes(losProyectos);        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonGenerarArchivoClientesActionPerformed

    private void jButtonLeerArchivoProyectosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeerArchivoProyectosActionPerformed
        ArrayList<Proyecto> leidos = leerArchivoDeObjetosProyectos();
        if (leidos.isEmpty()) { return; }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Se leyeron " + leidos.size() + " proyecto(s) del archivo.\n"
                + "¿Desea reemplazar los " + losProyectos.size() + " proyecto(s) que hay en memoria?",
                "LEER ARCHIVO DE PROYECTOS", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            losProyectos = leidos;
            reconstruirListaClientes();
            actualizarCodigoSugerido();
        }
        mostrarResultado("CONTENIDO DEL ARCHIVO datosProyectos\n" + SEPARADOR + consultarTodosProyectos(leidos));        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLeerArchivoProyectosActionPerformed

    private void jButtonLeerArchivoClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeerArchivoClientesActionPerformed
        ArrayList<Cliente> leidos = leerArchivoDeObjetosClientes();
        if (leidos.isEmpty()) { return; }
        String texto = "CLIENTES DEL ARCHIVO datosClientes (" + leidos.size() + ")\n" + SEPARADOR;
        for (Cliente objeto : leidos) {
            texto += objeto + "\n" + SEPARADOR;
        }
        mostrarResultado(texto);
 
    }//GEN-LAST:event_jButtonLeerArchivoClientesActionPerformed

    private void jComboBoxTipoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTipoClienteActionPerformed
        habilitarCamposContacto();        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxTipoClienteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UsaProyecto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UsaProyecto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UsaProyecto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UsaProyecto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UsaProyecto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonActualizarEstado;
    private javax.swing.JButton jButtonConsultarEnCurso;
    private javax.swing.JButton jButtonConsultarEquipo;
    private javax.swing.JButton jButtonConsultarGerente;
    private javax.swing.JButton jButtonConsultarPorCliente;
    private javax.swing.JButton jButtonConsultarPorGerente;
    private javax.swing.JButton jButtonConsultarPorTipoCliente;
    private javax.swing.JButton jButtonConsultarPrimero;
    private javax.swing.JButton jButtonConsultarProyectoCodigo;
    private javax.swing.JButton jButtonConsultarTodos;
    private javax.swing.JButton jButtonConsultarUltimo;
    private javax.swing.JButton jButtonEliminarIntegrante;
    private javax.swing.JButton jButtonGenerarArchivoClientes;
    private javax.swing.JButton jButtonGenerarArchivoProyectos;
    private javax.swing.JButton jButtonLeerArchivoClientes;
    private javax.swing.JButton jButtonLeerArchivoProyectos;
    private javax.swing.JButton jButtonLimpiarFormulario;
    private javax.swing.JButton jButtonRegistrarEquipo;
    private javax.swing.JButton jButtonRegistrarIntegrante;
    private javax.swing.JButton jButtonRegistrarProyecto;
    private javax.swing.JComboBox<String> jComboBoxEstado;
    private javax.swing.JComboBox<String> jComboBoxTipoCliente;
    private javax.swing.JComboBox<String> jComboBoxTipoClienteFiltro;
    private javax.swing.JComboBox<String> jComboBoxUnidad;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanelArchivos;
    private javax.swing.JPanel jPanelCliente;
    private javax.swing.JPanel jPanelConsultas;
    private javax.swing.JPanel jPanelConsultasFiltro;
    private javax.swing.JPanel jPanelConsultasGenerales;
    private javax.swing.JPanel jPanelGerente;
    private javax.swing.JPanel jPanelGestion;
    private javax.swing.JPanel jPanelProyecto;
    private javax.swing.JPanel jPanelRegistro;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextAreaResultados;
    private javax.swing.JTextField jTextFieldClienteCiudad;
    private javax.swing.JTextField jTextFieldClienteCorreo;
    private javax.swing.JTextField jTextFieldClienteId;
    private javax.swing.JTextField jTextFieldClienteNombre;
    private javax.swing.JTextField jTextFieldClienteTelefono;
    private javax.swing.JTextField jTextFieldCodigo;
    private javax.swing.JTextField jTextFieldCodigoGestion;
    private javax.swing.JTextField jTextFieldContactoCargo;
    private javax.swing.JTextField jTextFieldContactoNombre;
    private javax.swing.JTextField jTextFieldDuracion;
    private javax.swing.JTextField jTextFieldFechaInicio;
    private javax.swing.JTextField jTextFieldFiltro;
    private javax.swing.JTextField jTextFieldGerenteAnios;
    private javax.swing.JTextField jTextFieldGerenteId;
    private javax.swing.JTextField jTextFieldGerenteNombre;
    private javax.swing.JTextField jTextFieldGerenteTitulo;
    private javax.swing.JTextField jTextFieldTitulo;
    // End of variables declaration//GEN-END:variables
}

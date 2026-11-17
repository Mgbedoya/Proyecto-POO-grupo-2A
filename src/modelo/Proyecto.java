/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author dsant
 */

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

public class Proyecto implements Serializable {
    private String codigo;
    private String titulo;
    private LocalDate fechaInicio;
    private String unidadDuracion; // "semanas", "meses", "años"
    private byte duracionEstimada;
    private Cliente suCliente;
    private Empleado suGerente;
    private ArrayList<Empleado> suEquipo;
    private String estado; // Por defecto: "INICIADO"

    // Constructor completo
    public Proyecto(String codigo, String titulo, LocalDate fechaInicio, String unidadDuracion,
                    byte duracionEstimada, Cliente suCliente, Empleado suGerente,
                    ArrayList<Empleado> suEquipo, String estado) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.unidadDuracion = unidadDuracion;
        this.duracionEstimada = duracionEstimada;
        this.suCliente = suCliente;
        this.suGerente = suGerente;
        this.suEquipo = suEquipo;
        this.estado = "INICIADO";
    }

    // Constructor sobrecargado (sin equipo y con estado por defecto "INICIADO")
    public Proyecto(String codigo, String titulo, LocalDate fechaInicio, String unidadDuracion,
                    byte duracionEstimada, Cliente suCliente, Empleado suGerente) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.unidadDuracion = unidadDuracion;
        this.duracionEstimada = duracionEstimada;
        this.suCliente = suCliente;
        this.suGerente = suGerente;
        this.suEquipo = new ArrayList<>();
        this.estado = "INICIADO";
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getUnidadDuracion() {
        return unidadDuracion;
    }

    public void setUnidadDuracion(String unidadDuracion) {
        this.unidadDuracion = unidadDuracion;
    }

    public byte getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(byte duracionEstimada) {
        this.duracionEstimada = duracionEstimada;
    }

    public Cliente getSuCliente() {
        return suCliente;
    }

    public void setSuCliente(Cliente suCliente) {
        this.suCliente = suCliente;
    }

    public Empleado getSuGerente() {
        return suGerente;
    }

    public void setSuGerente(Empleado suGerente) {
        this.suGerente = suGerente;
    }

    public ArrayList<Empleado> getSuEquipo() {
        return suEquipo;
    }

    public void setSuEquipo(ArrayList<Empleado> suEquipo) {
        this.suEquipo = suEquipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Métodos de negocio definidos en el diagrama de clases
    public LocalDate determinarFechaFinalizacion() {
        LocalDate fin;
        switch (unidadDuracion) {
            case "semanas": fin = fechaInicio.plusWeeks(duracionEstimada); break;
            case "meses":   fin = fechaInicio.plusMonths(duracionEstimada); break;
            default:        fin = fechaInicio.plusYears(duracionEstimada); break; // años
        }
        return fin;
    }

    public void agregarIntegrante(Empleado nuevoIntegrante) {
        suEquipo.add(nuevoIntegrante);
    }

    /** Elimina y devuelve al integrante con ese número de identificación; null si no existe. */
    public Empleado eliminarIntegrante(String numeroIdentificacion) {
        for (int i = 0; i < suEquipo.size(); i++) {
            if (suEquipo.get(i).getNumeroIdentificacion().equals(numeroIdentificacion)) {
                return suEquipo.remove(i);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String resultado = "PROYECTO " + codigo
                + "\nTítulo: " + titulo
                + "\nFecha de inicio: " + fechaInicio
                + "\nDuración estimada: " + duracionEstimada + " " + unidadDuracion
                + "\nEstado: " + estado
                + "\nIntegrantes del equipo: " + suEquipo.size()
                + "\n" + suCliente
                + "\n--- Gerente ---\n" + suGerente;
        return resultado;
    }
}
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
        this.suEquipo = (suEquipo != null) ? suEquipo : new ArrayList<>();
        this.estado = (estado != null && !estado.isEmpty()) ? estado : "INICIADO";
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
        if (fechaInicio == null || unidadDuracion == null) {
            return fechaInicio;
        }
        switch (unidadDuracion.toLowerCase().trim()) {
            case "semanas":
                return fechaInicio.plusWeeks(duracionEstimada);
            case "meses":
                return fechaInicio.plusMonths(duracionEstimada);
            case "años":
            case "anios":
                return fechaInicio.plusYears(duracionEstimada);
            default:
                return fechaInicio;
        }
    }

    public void agregarIntegrante(Empleado nuevoIntegrante) {
        if (this.suEquipo == null) {
            this.suEquipo = new ArrayList<>();
        }
        if (nuevoIntegrante != null) {
            this.suEquipo.add(nuevoIntegrante);
        }
    }

    public Empleado eliminarIntegrante(String numeroIdentificacion) {
        if (this.suEquipo != null && numeroIdentificacion != null) {
            for (int i = 0; i < this.suEquipo.size(); i++) {
                Empleado emp = this.suEquipo.get(i);
                if (emp.getNumeroIdentificacion().equals(numeroIdentificacion)) {
                    return this.suEquipo.remove(i);
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("PROYECTO: ").append(codigo).append(" - ").append(titulo).append("\n");
        sb.append("Estado: ").append(estado).append("\n");
        sb.append("Fecha Inicio: ").append(fechaInicio).append("\n");
        sb.append("Duración: ").append(duracionEstimada).append(" ").append(unidadDuracion).append("\n");
        sb.append("Fecha Estimada Finalización: ").append(determinarFechaFinalizacion()).append("\n");
        sb.append("\n--- CLIENTE ---\n");
        sb.append(suCliente != null ? suCliente.toString() : "Sin cliente asignado").append("\n");
        sb.append("\n--- GERENTE ---\n");
        sb.append(suGerente != null ? suGerente.toString() : "Sin gerente asignado").append("\n");
        sb.append("\n--- EQUIPO DE TRABAJO ---\n");
        if (suEquipo == null || suEquipo.isEmpty()) {
            sb.append("Sin integrantes asignados aún.\n");
        } else {
            for (Empleado emp : suEquipo) {
                sb.append(emp.toString()).append("\n--------------------\n");
            }
        }
        sb.append("========================================");
        return sb.toString();
    }
}

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

public class Empleado implements Serializable {
    private static final long serialVersionUID = 1L;

    private String numeroIdentificacion;
    private String nombreCompleto;
    private String tituloProfesional;
    private byte aniosExperiencia;

    public Empleado(String numeroIdentificacion, String nombreCompleto,
                    String tituloProfesional, byte aniosExperiencia) {
        this.numeroIdentificacion = numeroIdentificacion;
        this.nombreCompleto = nombreCompleto;
        this.tituloProfesional = tituloProfesional;
        this.aniosExperiencia = aniosExperiencia;
    }

    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public void setNumeroIdentificacion(String numeroIdentificacion) { this.numeroIdentificacion = numeroIdentificacion; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getTituloProfesional() { return tituloProfesional; }
    public void setTituloProfesional(String tituloProfesional) { this.tituloProfesional = tituloProfesional; }

    public byte getAniosExperiencia() { return aniosExperiencia; }
    public void setAniosExperiencia(byte aniosExperiencia) { this.aniosExperiencia = aniosExperiencia; }

    @Override
    public String toString() {
        return "Identificación: " + numeroIdentificacion
             + "\nNombre: " + nombreCompleto
             + "\nTítulo profesional: " + tituloProfesional
             + "\nAños de experiencia: " + aniosExperiencia;
    }
}
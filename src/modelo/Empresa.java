/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author migue
 */
public final class Empresa extends Cliente {
    private Contacto suContacto;

    public Empresa(String numeroIdentificacion, String nombre, String nombreCiudad, Contacto suContacto) {
        super(numeroIdentificacion, nombre, nombreCiudad);
        this.suContacto = suContacto;
    }

    public Contacto getSuContacto() {
        return suContacto;
    }

    public void setSuContacto(Contacto suContacto) {
        this.suContacto = suContacto;
    }

    @Override
    public String toString() {
        return "--- CLIENTE: EMPRESA ---\n" +
               super.toString() +
               "\n-- Datos del Contacto --\n" +
               (suContacto != null ? suContacto.toString() : "Sin contacto asignado");
    }
}

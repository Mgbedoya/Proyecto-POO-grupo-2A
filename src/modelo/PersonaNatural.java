/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Windows
 */
public final class PersonaNatural extends Cliente {
    private String numeroTelefono;
    private String correoElectronico;

    public PersonaNatural(String numeroIdentificacion, String nombre, String nombreCiudad,
                          String numeroTelefono, String correoElectronico) {
        super(numeroIdentificacion, nombre, nombreCiudad);
        this.numeroTelefono = numeroTelefono;
        this.correoElectronico = correoElectronico;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    @Override
    public String toString() {
        return "CLIENTE: PERSONA NATURAL\n" +
               super.toString() +
               "\nTeléfono: " + numeroTelefono +
               "\nCorreo Electrónico: " + correoElectronico;
    }
}

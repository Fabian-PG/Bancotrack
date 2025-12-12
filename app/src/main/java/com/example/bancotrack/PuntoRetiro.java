package com.example.bancotrack;

public class PuntoRetiro {

    private String nombre;
    private String direccion;
    private String tipo;

    // Constructor
    public PuntoRetiro(String nombre, String direccion, String tipo) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.tipo = tipo;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getDireccion() { return direccion; }
    public String getTipo() { return tipo; }

    // Setters (útiles para edición)
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}

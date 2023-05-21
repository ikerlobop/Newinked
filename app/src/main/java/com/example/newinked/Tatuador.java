package com.example.newinked;

public class Tatuador {
    private String nombre;
    private String email;
    private String contrasena;
    private String ubicacion;


    public Tatuador(String nombre, String email, String contrasena, String ubicacion) {
    }

    public Tatuador(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.ubicacion = ubicacion;

    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {

        return email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getUbicacion() {
        return ubicacion;
    }

}

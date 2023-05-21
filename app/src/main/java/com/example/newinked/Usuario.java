package com.example.newinked;

public class Usuario {
    private String nombre;
    private String email;
    private String contrasena;

    private String ubicacion;


    public Usuario() {
    }

    public Usuario(String nombre, String email, String contrasena, String ubicacion) {
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

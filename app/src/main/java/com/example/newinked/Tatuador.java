package com.example.newinked;

public class Tatuador {

    private String nombre;
    private String email;
    private String contrasena;
    private String ubicacion;
    private String biografia;

    public Tatuador(String nombre, String email, String contrasena, String ubicacion, String biografia) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.ubicacion = ubicacion;
        this.biografia = biografia;

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

    public String getBiografia() {
        return biografia;
    }
}





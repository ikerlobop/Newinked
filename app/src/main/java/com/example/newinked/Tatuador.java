package com.example.newinked;

public class Tatuador {
    private String nombre;
    private String email;

    public Tatuador() {
    }

    public Tatuador(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

}

package com.example.newinked;

public class Tatuador {

    private String nombre;
    private String email;
    private String contrasena;
    private String ubicacion;
    private String biografia;
    private String idtatuador;

    private String imagenes;

    public Tatuador() {
        // Constructor vacío requerido para Firebase
    }

    public Tatuador(String nombre, String email, String contrasena, String ubicacion) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.ubicacion = ubicacion;
    }

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

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public String getIdtatuador() {
        return idtatuador;
    }

    public void setIdtatuador(String idtatuador) {
        this.idtatuador = idtatuador;
    }

    public String getImagenes() {
        return imagenes;
    }

    public void setImagenes(String imagenes) {
        this.imagenes = imagenes;
    }



}






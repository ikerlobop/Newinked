package com.example.newinked;

public class Usuario {
    private String nombre;
    private String email;
    private String contrasena;

    private String ubicacion;

    private String fechaNacimiento;


    public Usuario() {

    }


    public Usuario(String nombre, String email, String contrasena, String ubicacion, String fechaNacimiento){
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.ubicacion = ubicacion;
        this.fechaNacimiento = fechaNacimiento;

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

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }


}

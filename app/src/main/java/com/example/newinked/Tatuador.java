package com.example.newinked;
import java.util.List;

public class Tatuador {

    private String nombre;
    private String email;
    private String contrasena;
    private String ubicacion;
    private String biografia;
    private String idtatuador;

    private String mensaje;
    private List<String> imagenes;

    public Tatuador(String nombre, String email, String contrasena, String ubicacion, String biografia, String mensaje) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.ubicacion = ubicacion;
        this.biografia = biografia;
        this.mensaje = mensaje;
    }

    public Tatuador(String nombre, String email, String contrasena, String ubicacion) {
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

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getIdtatuador() {
        return idtatuador;
    }

    public void setIdtatuador(String idtatuador) {
        this.idtatuador = idtatuador;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }

    @Override
    public String toString() {
        return "Tatuador{" +
                "nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", contrasena='" + contrasena + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", biografia='" + biografia + '\'' +
                ", idtatuador='" + idtatuador + '\'' +
                ", imagenes=" + imagenes +
                '}';
    }
}







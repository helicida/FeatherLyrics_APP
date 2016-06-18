package tk.sbarjola.pa.featherlyricsapp.Firebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by sergi on 20/03/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class Usuario {

    // String
    private String uid;                         // Identificador del usuario
    private String key;                         // key
    private String nombre = "Undefined";        // nombre del usuario
    private String edad = "Undefined";          // edad del usuario
    private String descripcion = "Undefined";   // Descripción usuario
    private String rutaImagen = "Undefined";    // Ruta en la que se alamcenará la ruta del usuario

    // Localización de donde vive el usuario
    private double latitud;
    private double longitud;

    // Constructores

    public Usuario(){}

    // Getters

    public String getUID() {
        return uid;
    }

    public String getKey() {
        return key;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEdad() {
        return edad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    // Setters

    public void setUID(String UID) {
        this.uid = UID;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
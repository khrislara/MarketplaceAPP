package com.example.myapplication.model;

public class Mensaje {

    private String id;
    private String remitenteId;
    private String contenido;
    private long timestamp;

    // NUEVO SEGÚN LA GUÍA: Campo para imagen
    private String imagenUri;

    public Mensaje() {
        // Inicialización por defecto (requerido por Firebase)
    }

    public Mensaje(String id, String remitenteId, String contenido, long timestamp) {
        this.id = id;
        this.remitenteId = remitenteId;
        this.contenido = contenido;
        this.timestamp = timestamp;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemitenteId() {
        return remitenteId;
    }

    public void setRemitenteId(String remitenteId) {
        this.remitenteId = remitenteId;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // -----------------------------
    // NUEVOS MÉTODOS (solo lo necesario según la guía)
    // -----------------------------

    public String getImagenUri() {
        return imagenUri;
    }

    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }
}

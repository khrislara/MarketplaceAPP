package com.example.myapplication.model;

public class Mensaje {

    private String id;
    private String remitenteId;
    private String texto;      // Texto del mensaje
    private String imagenUri;  // Imagen del mensaje (si existe)
    private long timestamp;

    public Mensaje() {
        // Necesario para Firebase
    }

    public Mensaje(String id, String remitenteId, String texto, long timestamp) {
        this.id = id;
        this.remitenteId = remitenteId;
        this.texto = texto;
        this.timestamp = timestamp;
    }

    // GETTERS & SETTERS

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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getImagenUri() {
        return imagenUri;
    }

    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

package com.example.myapplication;

// Clase que contiene todas las constantes y métodos estáticos de utilidad
public class Constantes {

    // --- ESTADO DEL ANUNCIO ---
    public static final String anuncio_disponible = "Disponible";

    // --- LISTA DE CATEGORÍAS ---
    public static final String[] categorias = {
            "Todos",
            "Móviles",
            "Ordenadores / Laptops",
            "Electrónica y electrodomésticos",
            "Vehículos",
            "Consolas y videojuegos",
            "Hogar y muebles",
            "Belleza y cuidado personal",
            "Libros",
            "Deportes",
            "Juguetes y figuras",
            "Mascotas"
    };

    // --- LISTA DE CONDICIONES ---
    public static final String[] condiciones = {
            "Nuevo",
            "Usado",
            "Renovado"
    };

    // Método para obtener el tiempo actual en milisegundos
    public static long obtenerTiempoDis() {
        return System.currentTimeMillis();
    }
}

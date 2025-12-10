package com.example.myapplication.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.List;

public class GaleriaImagenAdapter extends RecyclerView.Adapter<GaleriaImagenAdapter.ImageViewHolder> {

    private final Context context;
    private final List<Uri> listaImagenesUri;
    private final OnImageInteractionListener listener;

    // ------------------------------------------------------------
    // INTERFAZ con las dos funciones pedidas:
    // 1. onRemoveImage → Eliminar imagen
    // 2. onSelectMainImage → Seleccionar imagen principal
    // ------------------------------------------------------------
    public interface OnImageInteractionListener {
        void onRemoveImage(int position);
        void onSelectMainImage(int position);
    }

    // ------------------------------------------------------------
    // CONSTRUCTOR (Context, Lista, Listener)
    // ------------------------------------------------------------
    public GaleriaImagenAdapter(Context context, List<Uri> listaImagenesUri, OnImageInteractionListener listener) {
        this.context = context;
        this.listaImagenesUri = listaImagenesUri;
        this.listener = listener;
    }

    // ------------------------------------------------------------
    // INFLA EL ITEM item_galeria_imagen.xml
    // ------------------------------------------------------------
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.item_galeria_imagen, parent, false);

        return new ImageViewHolder(view);
    }

    // ------------------------------------------------------------
    // VINCULA LOS DATOS EN CADA ITEM DEL RECYCLER
    // ------------------------------------------------------------
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {

        Uri imageUri = listaImagenesUri.get(position);

        // Cargar imagen de manera eficiente con GLIDE
        Glide.with(context)
                .load(imageUri)
                .centerCrop()
                .placeholder(R.drawable.item_imagen) // Placeholder
                .into(holder.ivThumbnail);

        // 1. ELIMINAR IMAGEN
        holder.ivRemoveImage.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveImage(holder.getAdapterPosition());
            }
        });

        // 2. SELECCIONAR IMAGEN PRINCIPAL
        holder.ivThumbnail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSelectMainImage(holder.getAdapterPosition());
            }
        });
    }

    // ------------------------------------------------------------
    // CANTIDAD DE IMÁGENES EN LA LISTA
    // ------------------------------------------------------------
    @Override
    public int getItemCount() {
        return listaImagenesUri.size();
    }

    // ------------------------------------------------------------
    // VIEWHOLDER: conecta item_imagen + cerrar_item
    // ------------------------------------------------------------
    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        ImageView ivThumbnail;
        ImageView ivRemoveImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            ivThumbnail = itemView.findViewById(R.id.item_imagen);
            ivRemoveImage = itemView.findViewById(R.id.cerrar_item);
        }
    }
}

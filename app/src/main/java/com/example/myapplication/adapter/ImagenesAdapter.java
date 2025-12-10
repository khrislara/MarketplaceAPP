package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class ImagenesAdapter extends RecyclerView.Adapter<ImagenesAdapter.ImagenViewHolder> {

    private List<String> imagenes;

    public ImagenesAdapter(){
        imagenes = new ArrayList<>();
    }

    public ImagenesAdapter(List<String> lista){
        imagenes = lista;
    }

    public void addImagen(String url){
        imagenes.add(url);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImagenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imagen_producto,parent,false);
        return new ImagenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagenViewHolder holder, int position){
        String url = imagenes.get(position);
        Glide.with(holder.ivImagen.getContext())
                .load(url)
                .placeholder(R.drawable.agregar_img)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(holder.ivImagen);
    }

    @Override
    public int getItemCount(){
        return imagenes.size();
    }

    static class ImagenViewHolder extends RecyclerView.ViewHolder{
        ImageView ivImagen;
        public ImagenViewHolder(@NonNull View itemView){
            super(itemView);
            ivImagen = itemView.findViewById(R.id.iv_item_imagen);
        }
    }
}

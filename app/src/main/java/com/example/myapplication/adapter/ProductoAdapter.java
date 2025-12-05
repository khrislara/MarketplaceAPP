package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import com.example.myapplication.Detalle_Producto;
import com.example.myapplication.R;
import com.example.myapplication.model.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.widget.ImageView;
import com.bumptech.glide.Glide;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private static final String TAG = "ProductoAdapter";

    private final Context context;

    // Lista visible para el RecyclerView
    private List<Producto> listaProductos;

    // Copia de la lista original (para restaurar filtros)
    private final List<Producto> listaOriginal;

    public ProductoAdapter(Context context, List<Producto> lista) {
        this.context = context;

        // Inicializamos las listas como copias independientes
        this.listaProductos = new ArrayList<>(lista);
        this.listaOriginal = new ArrayList<>(lista);
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);

        // Nombre y precio
        holder.tvNombre.setText(producto.getNombre());
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "$%.2f", producto.getPrecio()));

        // Placeholder por defecto
        holder.ivImagenProducto.setImageResource(R.drawable.agregar_img);

        // CARGA DE IMAGEN SEGÚN GUÍA
        if (producto.getImageUrls() != null && !producto.getImageUrls().isEmpty()) {

            String imageUrl = producto.getImageUrls().get(0);

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {

                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.agregar_img)
                        .error(R.drawable.agregar_img)
                        .centerCrop()
                        .into(holder.ivImagenProducto);

            } else {
                Log.w(TAG, "URL de imagen vacía para producto ID: " + producto.getId());
            }

        } else {
            Log.d(TAG, "Producto sin imágenes: " + producto.getNombre());
        }

        // Click al producto → abre pantalla de detalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalle_Producto.class);
            intent.putExtra(Detalle_Producto.EXTRA_PRODUCTO_ID, producto.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    // Actualización al recibir datos nuevos desde Firebase
    public void actualizarProductos(List<Producto> nuevaLista) {
        this.listaProductos.clear();
        this.listaProductos.addAll(nuevaLista);

        this.listaOriginal.clear();
        this.listaOriginal.addAll(nuevaLista);

        notifyDataSetChanged();

        Log.d(TAG, "Productos actualizados. Total: " + nuevaLista.size());
    }

    // FILTRO de productos
    public void filtrar(String texto) {
        String query = texto.toLowerCase(Locale.getDefault()).trim();

        List<Producto> filtrados = new ArrayList<>();

        if (query.isEmpty()) {
            filtrados.addAll(listaOriginal);
        } else {
            for (Producto p : listaOriginal) {
                if (p.getNombre().toLowerCase(Locale.getDefault()).contains(query)) {
                    filtrados.add(p);
                }
            }
        }

        this.listaProductos = filtrados;
        notifyDataSetChanged();
    }

    // ViewHolder
    public static class ProductoViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImagenProducto;
        TextView tvNombre;
        TextView tvPrecio;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImagenProducto = itemView.findViewById(R.id.iv_producto_imagen);
            tvNombre = itemView.findViewById(R.id.tv_producto_titulo);
            tvPrecio = itemView.findViewById(R.id.tv_producto_precio);
        }
    }
}

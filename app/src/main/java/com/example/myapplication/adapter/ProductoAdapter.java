package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Detalle_Producto;
import com.example.myapplication.R;
import com.example.myapplication.model.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private static final String TAG = "ProductoAdapter";

    private final Context context;
    private List<Producto> listaProductos;
    private final List<Producto> listaOriginal;

    private OnProductoClickListener listener;

    public interface OnProductoClickListener {
        void onEditarClick(Producto producto);
        void onEliminarClick(Producto producto);
    }

    public void setOnProductoClickListener(OnProductoClickListener listener) {
        this.listener = listener;
    }

    public ProductoAdapter(Context context, List<Producto> lista) {
        this.context = context;
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

        holder.tvNombre.setText(producto.getNombre());
        holder.tvPrecio.setText(String.format(Locale.getDefault(), "$%.2f", producto.getPrecio()));
        holder.tvVendedor.setText("Vendido por: " + producto.getNombre());

        holder.ivImagenProducto.setImageResource(R.drawable.agregar_img);

        if (producto.getImageUrls() != null && !producto.getImageUrls().isEmpty()) {
            String imageUrl = producto.getImageUrls().get(0);
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.agregar_img)
                    .error(R.drawable.agregar_img)
                    .centerCrop()
                    .into(holder.ivImagenProducto);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Detalle_Producto.class);
            intent.putExtra(Detalle_Producto.EXTRA_PRODUCTO_ID, producto.getId());
            context.startActivity(intent);
        });

        holder.btnEditar.setOnClickListener(v -> {
            if(listener != null) listener.onEditarClick(producto);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if(listener != null) listener.onEliminarClick(producto);
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public void actualizarProductos(List<Producto> nuevaLista) {
        this.listaProductos.clear();
        this.listaProductos.addAll(nuevaLista);

        this.listaOriginal.clear();
        this.listaOriginal.addAll(nuevaLista);

        notifyDataSetChanged();
        Log.d(TAG, "Productos actualizados. Total: " + nuevaLista.size());
    }

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

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImagenProducto;
        TextView tvNombre, tvPrecio, tvVendedor;
        ImageButton btnEditar, btnEliminar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagenProducto = itemView.findViewById(R.id.iv_producto_imagen);
            tvNombre = itemView.findViewById(R.id.tv_producto_titulo);
            tvPrecio = itemView.findViewById(R.id.tv_producto_precio);
            tvVendedor = itemView.findViewById(R.id.tv_producto_vendedor);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}

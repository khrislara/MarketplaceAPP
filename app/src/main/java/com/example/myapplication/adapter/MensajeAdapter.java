package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Mensaje;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    private final Context context;
    private final List<Mensaje> listaMensajes;
    private final String currentUserId;

    private static final int VIEW_TYPE_EMISOR = 1;
    private static final int VIEW_TYPE_RECEPTOR = 2;

    public MensajeAdapter(Context context, List<Mensaje> listaMensajes, String currentUserId) {
        this.context = context;
        this.listaMensajes = listaMensajes;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Mensaje mensaje = listaMensajes.get(position);
        return mensaje.getRemitenteId().equals(currentUserId)
                ? VIEW_TYPE_EMISOR
                : VIEW_TYPE_RECEPTOR;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {

        Mensaje mensaje = listaMensajes.get(position);

        // --- MOSTRAR TEXTO ---
        if (mensaje.getTexto() != null && !mensaje.getTexto().isEmpty()) {
            holder.tvContenido.setVisibility(View.VISIBLE);
            holder.ivImagen.setVisibility(View.GONE);
            holder.tvContenido.setText(mensaje.getTexto());
        }
        // --- MOSTRAR IMAGEN ---
        else if (mensaje.getImagenUri() != null && !mensaje.getImagenUri().isEmpty()) {
            holder.tvContenido.setVisibility(View.GONE);
            holder.ivImagen.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(mensaje.getImagenUri())
                    .placeholder(R.drawable.ic_image_sample)
                    .into(holder.ivImagen);
        }

        // --- HORA ---
        holder.tvHora.setText(formatTimestamp(mensaje.getTimestamp()));

        // --- ALINEAR BURBUJA ---
        ConstraintLayout.LayoutParams layoutParams =
                (ConstraintLayout.LayoutParams) holder.cardMensaje.getLayoutParams();

        if (getItemViewType(position) == VIEW_TYPE_EMISOR) {
            layoutParams.horizontalBias = 1.0f;
            holder.cardMensaje.setLayoutParams(layoutParams);

            holder.llFondo.setBackgroundResource(R.drawable.bubble_sent_bg);
            holder.tvContenido.setTextColor(Color.WHITE);
            holder.tvHora.setTextColor(Color.parseColor("#DDDDDD"));

        } else {
            layoutParams.horizontalBias = 0.0f;
            holder.cardMensaje.setLayoutParams(layoutParams);

            holder.llFondo.setBackgroundResource(R.drawable.bubble_received_bg);
            holder.tvContenido.setTextColor(Color.BLACK);
            holder.tvHora.setTextColor(Color.parseColor("#555555"));
        }
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {

        public final TextView tvContenido;
        public final TextView tvHora;
        public final ImageView ivImagen;
        public final View cardMensaje;
        public final LinearLayout llFondo;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContenido = itemView.findViewById(R.id.tv_mensaje_contenido);
            tvHora = itemView.findViewById(R.id.tv_mensaje_hora);
            ivImagen = itemView.findViewById(R.id.iv_mensaje_imagen);
            cardMensaje = itemView.findViewById(R.id.card_mensaje);
            llFondo = itemView.findViewById(R.id.ll_bubble_background);
        }
    }
}

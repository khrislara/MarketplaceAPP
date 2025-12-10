package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.adapter.MensajeAdapter;
import com.example.myapplication.model.Mensaje;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etMensaje;
    private ImageButton btnEnviar;
    private ImageButton btnImagen;
    private TextView tvNombreContacto, tvEstadoContacto;

    private MensajeAdapter mensajeAdapter;
    private List<Mensaje> listaMensajes;

    private String currentUserId;
    private String otherUserId;
    private String chatId;

    private DatabaseReference chatRef;

    private ActivityResultLauncher<String> abrirGaleriaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inicializar();
        configurarChatId();

        // REGISTRO DEL LAUNCHER PARA ABRIR GALERÍA
        abrirGaleriaLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::enviarImagen
        );

        configurarRecycler();
        escucharMensajes();

        btnEnviar.setOnClickListener(v -> enviarMensajeTexto());
        btnImagen.setOnClickListener(v -> abrirGaleriaLauncher.launch("image/*"));
    }

    private void inicializar() {
        recyclerView = findViewById(R.id.recyclerViewChat);
        etMensaje = findViewById(R.id.editTextMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnImagen = findViewById(R.id.btnAttachImage);
        tvNombreContacto = findViewById(R.id.tvContactName);
        tvEstadoContacto = findViewById(R.id.tvContactStatus);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        otherUserId = getIntent().getStringExtra("otherUserId");

        // NOMBRE DEL CONTACTO EN LA PARTE SUPERIOR
        tvNombreContacto.setText(getIntent().getStringExtra("nombreContacto"));
        tvEstadoContacto.setText("online");

        listaMensajes = new ArrayList<>();
    }

    private void configurarChatId() {
        // Ordenar los UID para que el chat sea el mismo en ambos teléfonos
        if (currentUserId.compareTo(otherUserId) < 0)
            chatId = currentUserId + "_" + otherUserId;
        else
            chatId = otherUserId + "_" + currentUserId;

        chatRef = FirebaseDatabase.getInstance().getReference("chats")
                .child(chatId)
                .child("mensajes");
    }

    private void configurarRecycler() {
        mensajeAdapter = new MensajeAdapter(this, listaMensajes, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // BAJA AUTOMÁTICAMENTE AL FINAL
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(mensajeAdapter);
    }

    private void escucharMensajes() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaMensajes.clear();

                for (DataSnapshot msjSnap : snapshot.getChildren()) {
                    Mensaje mensaje = msjSnap.getValue(Mensaje.class);
                    if (mensaje != null)
                        listaMensajes.add(mensaje);
                }

                mensajeAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(listaMensajes.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void enviarMensajeTexto() {
        String texto = etMensaje.getText().toString().trim();

        if (texto.isEmpty()) {
            Toast.makeText(this, "Escribe un mensaje.", Toast.LENGTH_SHORT).show();
            return;
        }

        String mensajeId = chatRef.push().getKey();

        Map<String, Object> msj = new HashMap<>();
        msj.put("id", mensajeId);
        msj.put("remitenteId", currentUserId);
        msj.put("texto", texto);
        msj.put("imagenUri", "");
        msj.put("timestamp", System.currentTimeMillis());

        chatRef.child(mensajeId).setValue(msj);

        etMensaje.setText("");
    }

    private void enviarImagen(Uri uri) {
        if (uri == null) {
            Toast.makeText(this, "No seleccionaste imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        String mensajeId = chatRef.push().getKey();

        Map<String, Object> msj = new HashMap<>();
        msj.put("id", mensajeId);
        msj.put("remitenteId", currentUserId);
        msj.put("texto", "");
        msj.put("imagenUri", uri.toString());  // URI DIRECTA DE GALERÍA
        msj.put("timestamp", System.currentTimeMillis());

        chatRef.child(mensajeId).setValue(msj);
    }
}

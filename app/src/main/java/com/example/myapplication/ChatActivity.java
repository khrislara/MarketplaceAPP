package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.adapter.MensajeAdapter;
import com.example.myapplication.model.Mensaje;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String CURRENT_USER_ID = "usuario_propio";

    private RecyclerView recyclerView;
    private EditText etMensaje;
    private ImageButton btnEnviarMensaje;
    private ImageButton btnEnviarImagen;
    private TextView tvNombreContacto;
    private TextView tvEstadoContacto;

    private MensajeAdapter mensajeAdapter;
    private List<Mensaje> listaMensajes;

    // Lanzador para abrir la galería
    private ActivityResultLauncher<String> abrirGaleriaLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inicializarVistas();
        configurarCabecera();
        configurarRecyclerView();
        configurarListeners();

        // Registrar launcher para elegir imagen
        abrirGaleriaLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::procesarImagenSeleccionada
        );
    }

    private void inicializarVistas() {
        tvNombreContacto = findViewById(R.id.tvContactName);
        tvEstadoContacto = findViewById(R.id.tvContactStatus);

        recyclerView = findViewById(R.id.recyclerViewChat);

        etMensaje = findViewById(R.id.editTextMensaje);
        btnEnviarMensaje = findViewById(R.id.btnEnviar);
        btnEnviarImagen = findViewById(R.id.btnAttachImage);
    }

    private void configurarCabecera() {
        tvNombreContacto.setText("DiegoDev");
        tvEstadoContacto.setText("online");

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void configurarRecyclerView() {
        listaMensajes = cargarMensajesDePrueba();

        mensajeAdapter = new MensajeAdapter(this, listaMensajes, CURRENT_USER_ID);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mensajeAdapter);
    }

    private void configurarListeners() {

        btnEnviarMensaje.setOnClickListener(v -> enviarMensajeSimulado());

        // Enviar Imagen
        btnEnviarImagen.setOnClickListener(v -> {
            abrirGaleriaLauncher.launch("image/*");
        });
    }

    // Recibe la imagen seleccionada desde la galería
    private void procesarImagenSeleccionada(Uri uriImagen) {
        if (uriImagen == null) {
            Toast.makeText(this, "No seleccionaste ninguna imagen.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear mensaje tipo imagen
        Mensaje mensajeImagen = new Mensaje(
                "ID_IMG_" + (listaMensajes.size() + 1),
                CURRENT_USER_ID,
                null,               // texto vacío
                System.currentTimeMillis()
        );
        mensajeImagen.setImagenUri(uriImagen.toString()); // <--- IMPORTANTE

        listaMensajes.add(mensajeImagen);
        mensajeAdapter.notifyItemInserted(listaMensajes.size() - 1);
        recyclerView.scrollToPosition(listaMensajes.size() - 1);

        Toast.makeText(this, "Imagen enviada.", Toast.LENGTH_SHORT).show();

        // Simular respuesta con imagen
        simularRespuestaImagen();
    }

    private void enviarMensajeSimulado() {
        String texto = etMensaje.getText().toString().trim();

        if (!texto.isEmpty()) {
            Mensaje nuevoMensaje = new Mensaje(
                    "ID_SIMULADO_" + (listaMensajes.size() + 1),
                    CURRENT_USER_ID,
                    texto,
                    System.currentTimeMillis()
            );

            listaMensajes.add(nuevoMensaje);
            mensajeAdapter.notifyItemInserted(listaMensajes.size() - 1);
            recyclerView.scrollToPosition(listaMensajes.size() - 1);

            etMensaje.setText("");

            simularRespuesta();

        } else {
            Toast.makeText(this, "Escriba un mensaje.", Toast.LENGTH_SHORT).show();
        }
    }

    private void simularRespuesta() {
        String respuestaTexto = "¡Hola! Gracias por tu mensaje. El producto sigue disponible.";

        Mensaje respuesta = new Mensaje(
                "ID_RESPUESTA_" + (listaMensajes.size() + 1),
                "DiegoDev_id",
                respuestaTexto,
                System.currentTimeMillis() + 1000
        );

        recyclerView.postDelayed(() -> {
            listaMensajes.add(respuesta);
            mensajeAdapter.notifyItemInserted(listaMensajes.size() - 1);
            recyclerView.scrollToPosition(listaMensajes.size() - 1);
        }, 1000);
    }

    private void simularRespuestaImagen() {

        Mensaje respuestaImg = new Mensaje(
                "ID_RESP_IMG_" + (listaMensajes.size() + 1),
                "DiegoDev_id",
                null,
                System.currentTimeMillis()
        );

        respuestaImg.setImagenUri("android.resource://" + getPackageName() + "/" + R.drawable.ic_image_sample);

        recyclerView.postDelayed(() -> {
            listaMensajes.add(respuestaImg);
            mensajeAdapter.notifyItemInserted(listaMensajes.size() - 1);
            recyclerView.scrollToPosition(listaMensajes.size() - 1);
        }, 1500);
    }

    private List<Mensaje> cargarMensajesDePrueba() {
        List<Mensaje> mensajes = new ArrayList<>();

        mensajes.add(new Mensaje("m1", "DiegoDev_id",
                "¡Hola! ¿Aún tienes la bicicleta a la venta?",
                System.currentTimeMillis() - 600000));

        mensajes.add(new Mensaje("m2", CURRENT_USER_ID,
                "Sí, claro. Está en excelente estado.",
                System.currentTimeMillis() - 300000));

        mensajes.add(new Mensaje("m3", "DiegoDev_id",
                "¿Podrías enviarme más fotos?",
                System.currentTimeMillis() - 120000));

        return mensajes;
    }
}

package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.widget.AutoCompleteTextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import com.example.myapplication.adapter.GaleriaImagenAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Publicar extends AppCompatActivity implements GaleriaImagenAdapter.OnImageInteractionListener, List<Uri> {

    private static final String TAG = "PublicarActivity";
    private static final int MAX_IMAGES = 10;

    // Firebase
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    // Vistas
    private ImageView ivPublicarImagenPlaceholder;
    private RecyclerView rvGaleriaImagenes;
    private TextInputEditText etNombre;
    private TextInputEditText etPrecio;
    private TextInputEditText etDireccion;
    private TextInputEditText etDescripcion;
    private AutoCompleteTextView etCategoria;
    private AutoCompleteTextView etCondicion;
    private Button btnPublicar;

    // Lista y adaptador de imágenes
    private GaleriaImagenAdapter galeriaImagenAdapter;
    private List<Uri> listaImagenesUri;

    // Launcher para abrir galería
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        try {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                                boolean imagesAdded = false;
                                int takeFlags = result.getData().getFlags() &
                                        (Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                if (result.getData().getClipData() != null) {
                                    int count = result.getData().getClipData().getItemCount();
                                    Log.d(TAG, "ClipData detectado: " + count + " imágenes.");

                                    for (int i = 0; i < count; i++) {
                                        if (listaImagenesUri.size() < MAX_IMAGES) {
                                            Uri uri = result.getData().getClipData().getItemAt(i).getUri();
                                            getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                            listaImagenesUri.add(uri);
                                            imagesAdded = true;
                                        } else {
                                            Toast.makeText(this,
                                                    "Máximo " + MAX_IMAGES + " imágenes.",
                                                    Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                } else if (result.getData().getData() != null) {
                                    Uri uri = result.getData().getData();
                                    if (listaImagenesUri.size() < MAX_IMAGES) {
                                        getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                        listaImagenesUri.add(uri);
                                        imagesAdded = true;
                                    } else {
                                        Toast.makeText(this,
                                                "Máximo " + MAX_IMAGES + " imágenes.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                if (imagesAdded) {
                                    galeriaImagenAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Error al cargar imágenes", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "ERROR: ", e);
                        }
                    });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("productos");
        databaseRef = FirebaseDatabase.getInstance().getReference("productos");

        // Inicializar vistas
        ivPublicarImagenPlaceholder = findViewById(R.id.iv_publicar_imagen);
        rvGaleriaImagenes = findViewById(R.id.rv_galeria_imagenes);
        etNombre = findViewById(R.id.et_publicar_nombre);
        etPrecio = findViewById(R.id.et_publicar_precio);
        etDireccion = findViewById(R.id.et_publicar_direccion);
        etDescripcion = findViewById(R.id.et_publicar_descripcion);
        etCategoria = findViewById(R.id.et_publicar_categoria);
        etCondicion = findViewById(R.id.et_publicar_condicion);
        btnPublicar = findViewById(R.id.btn_publicar_producto);

        // Lista de imágenes
        listaImagenesUri = new ArrayList<>();

        // Adaptador con orden correcto de parámetros
        galeriaImagenAdapter = new GaleriaImagenAdapter((Context) listaImagenesUri, (List<Uri>) this, this);
        rvGaleriaImagenes.setAdapter(galeriaImagenAdapter);

        // Evento para abrir galería
        ivPublicarImagenPlaceholder.setOnClickListener(v -> abrirGaleria());

        // Evento publicar (simulado)
        btnPublicar.setOnClickListener(v -> validarFormulario());
    }

    // Abrir galería
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(intent);
    }

    // Validación
    private void validarFormulario() {
        String nombre = etNombre.getText().toString().trim();

        if (listaImagenesUri.isEmpty()) {
            Toast.makeText(this, "Debes seleccionar al menos 1 imagen.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nombre.isEmpty()) {
            Toast.makeText(this, "El producto necesita un nombre.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this,
                "Producto '" + nombre + "' listo para publicar. (Simulación)",
                Toast.LENGTH_LONG).show();

        finish();
    }

    // Remover imagen desde el adaptador
    @Override
    public void onRemoveImage(int position) {
        listaImagenesUri.remove(position);
        galeriaImagenAdapter.notifyItemRemoved(position);
    }

    // Seleccionar imagen principal
    @Override
    public void onSelectMainImage(int position) {
        // Aquí puedes marcar una imagen como principal si lo deseas
    }

    @Override
    public boolean add(Uri uri) {
        return false;
    }

    @Override
    public void add(int i, Uri uri) {

    }

    @Override
    public boolean addAll(int i, @NonNull Collection<? extends Uri> collection) {
        return false;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Uri> collection) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean contains(@Nullable Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> collection) {
        return false;
    }

    @Override
    public Uri get(int i) {
        return null;
    }

    @Override
    public int indexOf(@Nullable Object o) {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @NonNull
    @Override
    public Iterator<Uri> iterator() {
        return null;
    }

    @Override
    public int lastIndexOf(@Nullable Object o) {
        return 0;
    }

    @NonNull
    @Override
    public ListIterator<Uri> listIterator() {
        return null;
    }

    @NonNull
    @Override
    public ListIterator<Uri> listIterator(int i) {
        return null;
    }

    @Override
    public Uri remove(int i) {
        return null;
    }

    @Override
    public boolean remove(@Nullable Object o) {
        return false;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> collection) {
        return false;
    }

    @Override
    public Uri set(int i, Uri uri) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @NonNull
    @Override
    public List<Uri> subList(int i, int i1) {
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @NonNull
    @Override
    public <T> T[] toArray(@NonNull T[] ts) {
        return null;
    }
}

package com.example.myapplication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.List;
import androidx.recyclerview.widget.GridLayoutManager;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import com.google.firebase.auth.FirebaseUser;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Publicar extends AppCompatActivity implements
        GaleriaImagenAdapter.OnImageInteractionListener {

    private static final String TAG = "PublicarActivity";
    private static final int MAX_IMAGES = 10;

    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    private ImageView ivPublicarImagenPlaceholder;
    private RecyclerView rvGaleriaImagenes;
    private TextInputEditText etNombre;
    private TextInputEditText etPrecio;
    private TextInputEditText etDireccion;
    private TextInputEditText etDescripcion;
    private TextInputEditText etMarca;
    private AutoCompleteTextView etCategoria;
    private AutoCompleteTextView etCondicion;
    private Button btnPublicar;

    private GaleriaImagenAdapter galeriaImagenAdapter;
    private List<Uri> listaImagenesUri;

    // ----------------------------------------------------------
    // 🔥 CORREGIDO: AHORA SÍ SE PUEDE USAR takePersistableUriPermission()
    // ----------------------------------------------------------
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                try {

                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        boolean imagesAdded = false;

                        // PERMISOS PERSISTENTES
                        final int takeFlags = result.getData().getFlags()
                                & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        // -----------------------------
                        // MULTIPLE
                        // -----------------------------
                        if (result.getData().getClipData() != null) {

                            int count = result.getData().getClipData().getItemCount();
                            Log.d(TAG, "ClipData con " + count + " imágenes.");

                            for (int i = 0; i < count; i++) {

                                if (listaImagenesUri.size() < MAX_IMAGES) {

                                    Uri uri = result.getData().getClipData().getItemAt(i).getUri();
                                    getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                    listaImagenesUri.add(uri);

                                    imagesAdded = true;

                                } else {
                                    Toast.makeText(this, "Máximo " + MAX_IMAGES + " imágenes permitidas.",
                                            Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }

                            // -----------------------------
                            // UNA SOLA IMAGEN
                            // -----------------------------
                        } else if (result.getData().getData() != null) {

                            if (listaImagenesUri.size() < MAX_IMAGES) {

                                Uri uri = result.getData().getData();
                                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                                listaImagenesUri.add(uri);

                                imagesAdded = true;

                            } else {
                                Toast.makeText(this, "Máximo " + MAX_IMAGES + " imágenes permitidas.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (imagesAdded) {
                            galeriaImagenAdapter.notifyDataSetChanged();
                            actualizarPlaceholderImagenPrincipal();
                        }

                    } else {
                        Toast.makeText(this, "Selección cancelada.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                    Log.e(TAG, "Error al cargar imagen: " + e.getMessage(), e);
                    Toast.makeText(this, "Error al cargar la imagen.", Toast.LENGTH_LONG).show();

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicar);

        ivPublicarImagenPlaceholder = findViewById(R.id.iv_publicar_imagen_placeholder);
        rvGaleriaImagenes = findViewById(R.id.rv_galeria_imagenes);
        etNombre = findViewById(R.id.et_publicar_nombre);
        etPrecio = findViewById(R.id.et_publicar_precio);
        etDireccion = findViewById(R.id.et_publicar_direccion);
        etDescripcion = findViewById(R.id.et_publicar_descripcion);
        etMarca = findViewById(R.id.et_publicar_marca);
        etCategoria = findViewById(R.id.spinner_categoria);
        etCondicion = findViewById(R.id.spinner_condicion);
        btnPublicar = findViewById(R.id.btn_publicar_producto);

        storageRef = FirebaseStorage.getInstance().getReference().child("productos_imagenes");
        databaseRef = FirebaseDatabase.getInstance().getReference("productos");
        mAuth = FirebaseAuth.getInstance();

        listaImagenesUri = new ArrayList<>();
        galeriaImagenAdapter = new GaleriaImagenAdapter(this, listaImagenesUri, this);

        rvGaleriaImagenes.setLayoutManager(new GridLayoutManager(this, 4));
        rvGaleriaImagenes.setAdapter(galeriaImagenAdapter);

        ArrayAdapter<String> adaptadorCat = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, Constantes.categorias);
        etCategoria.setAdapter(adaptadorCat);
        etCategoria.setOnClickListener(v -> etCategoria.showDropDown());

        ArrayAdapter<String> adaptadorCon = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, Constantes.condiciones);
        etCondicion.setAdapter(adaptadorCon);
        etCondicion.setOnClickListener(v -> etCondicion.showDropDown());

        ivPublicarImagenPlaceholder.setOnClickListener(v -> abrirGaleria());

        btnPublicar.setOnClickListener(v -> {
            if (validarCampos()) {

                FirebaseUser user = mAuth.getCurrentUser();

                if (user != null) {
                    publicarProducto(user.getUid());
                } else {
                    Toast.makeText(this, "Debe iniciar sesión.", Toast.LENGTH_LONG).show();
                }
            }
        });

        actualizarPlaceholderImagenPrincipal();
    }

    // --------------------------------------------------
    // 🔥 CORREGIDO: USAMOS ACTION_OPEN_DOCUMENT
    // --------------------------------------------------
    private void abrirGaleria() {

        if (listaImagenesUri.size() >= MAX_IMAGES) {
            Toast.makeText(this, "Máximo " + MAX_IMAGES + " imágenes.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        imagePickerLauncher.launch(intent);
    }

    private void actualizarPlaceholderImagenPrincipal() {
        ivPublicarImagenPlaceholder.setImageResource(R.drawable.agregar_img);
        ivPublicarImagenPlaceholder.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    @Override
    public void onRemoveImage(int position) {
        if (!listaImagenesUri.isEmpty() && position < listaImagenesUri.size()) {

            listaImagenesUri.remove(position);
            galeriaImagenAdapter.notifyItemRemoved(position);
            galeriaImagenAdapter.notifyItemRangeChanged(position, listaImagenesUri.size());
            actualizarPlaceholderImagenPrincipal();

            Toast.makeText(this, "Imagen eliminada.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSelectMainImage(int position) {

        if (!listaImagenesUri.isEmpty() && position < listaImagenesUri.size()) {

            Uri selectedUri = listaImagenesUri.remove(position);
            listaImagenesUri.add(0, selectedUri);

            galeriaImagenAdapter.notifyDataSetChanged();
            actualizarPlaceholderImagenPrincipal();

            Toast.makeText(this, "Imagen principal seleccionada.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCampos() {
        String nombre = etNombre.getText().toString().trim();
        String precio = etPrecio.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();
        String condicion = etCondicion.getText().toString().trim();
        String marca = etMarca.getText().toString().trim();

        if (listaImagenesUri.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar al menos una imagen.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(nombre)) { etNombre.setError("Obligatorio"); return false; }
        if (TextUtils.isEmpty(marca)) { etMarca.setError("Obligatorio"); return false; }
        if (TextUtils.isEmpty(categoria)) { etCategoria.setError("Obligatorio"); return false; }
        if (TextUtils.isEmpty(condicion)) { etCondicion.setError("Obligatorio"); return false; }
        if (TextUtils.isEmpty(precio)) { etPrecio.setError("Obligatorio"); return false; }
        if (TextUtils.isEmpty(direccion)) { etDireccion.setError("Obligatorio"); return false; }
        if (descripcion.length() < 10) { etDescripcion.setError("Mínimo 10 caracteres"); return false; }

        return true;
    }

    private void publicarProducto(String userId) {

        btnPublicar.setEnabled(false);

        final List<String> imageUrls = new ArrayList<>();
        final AtomicInteger imagesUploaded = new AtomicInteger(0);

        for (int i = 0; i < listaImagenesUri.size(); i++) {

            Uri imageUri = listaImagenesUri.get(i);

            final StorageReference fileRef = storageRef.child(Constantes.obtenerTiempoDis() + "_" + i
                    + "_" + imageUri.getLastPathSegment());

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {

                                imageUrls.add(uri.toString());

                                if (imagesUploaded.incrementAndGet() == listaImagenesUri.size()) {
                                    guardarDatosProducto(imageUrls, userId);
                                }

                            }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error subiendo imagen.", Toast.LENGTH_LONG).show();
                        btnPublicar.setEnabled(true);
                    });
        }
    }

    private void guardarDatosProducto(List<String> imageUrls, String userId) {

        String nombre = etNombre.getText().toString();
        String precioStr = etPrecio.getText().toString();
        String direccion = etDireccion.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String categoria = etCategoria.getText().toString();
        String condicion = etCondicion.getText().toString();
        String marca = etMarca.getText().toString();

        double precio;
        try { precio = Double.parseDouble(precioStr.replace(",", ".")); }
        catch (Exception e) { precio = 0; }

        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", nombre);
        producto.put("precio", precio);
        producto.put("direccion", direccion);
        producto.put("descripcion", descripcion);
        producto.put("categoria", categoria);
        producto.put("condicion", condicion);
        producto.put("marca", marca);
        producto.put("imageUrls", imageUrls);
        producto.put("fechaPublicacion", Constantes.obtenerTiempoDis());
        producto.put("vendedorId", userId);
        producto.put("estado", Constantes.anuncio_disponible);

        databaseRef.push().setValue(producto)
                .addOnSuccessListener(aVoid -> {

                    Toast.makeText(this, "Publicado con éxito.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Publicar.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this, "Error al guardar datos.", Toast.LENGTH_LONG).show();
                    btnPublicar.setEnabled(true);

                });
    }
}

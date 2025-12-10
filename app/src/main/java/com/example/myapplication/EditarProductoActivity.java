package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.model.Producto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditarProductoActivity extends AppCompatActivity {

    private EditText etNombre, etDescripcion, etPrecio;
    private Button btnGuardar;

    private DatabaseReference productoRef;
    private String productoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);
        btnGuardar = findViewById(R.id.btnGuardar);

        productoId = getIntent().getStringExtra("productoId");
        if(productoId == null) {
            Toast.makeText(this,"Producto no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productoRef = FirebaseDatabase.getInstance().getReference("productos").child(productoId);

        cargarDatosProducto();

        btnGuardar.setOnClickListener(v -> actualizarProducto());
    }

    private void cargarDatosProducto() {
        productoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Producto producto = snapshot.getValue(Producto.class);
                if(producto != null){
                    etNombre.setText(producto.getNombre());
                    etDescripcion.setText(producto.getDescripcion());
                    etPrecio.setText(String.valueOf(producto.getPrecio()));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void actualizarProducto() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();

        if(TextUtils.isEmpty(nombre) || TextUtils.isEmpty(descripcion) || TextUtils.isEmpty(precioStr)){
            Toast.makeText(this,"Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio;
        try { precio = Double.parseDouble(precioStr); }
        catch (NumberFormatException e) {
            Toast.makeText(this,"Precio inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("descripcion", descripcion);
        updates.put("precio", precio);

        productoRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this,"Producto actualizado",Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this,"Error: "+e.getMessage(),Toast.LENGTH_LONG).show());
    }
}

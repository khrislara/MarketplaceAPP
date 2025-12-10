package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.adapter.ProductoAdapter;
import com.example.myapplication.model.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AnunciosActivity extends AppCompatActivity {

    private RecyclerView rvMisAnuncios;
    private TextView tvNoAnuncios;

    private FirebaseAuth mAuth;
    private DatabaseReference productosRef;

    private List<Producto> listaProductos;
    private ProductoAdapter productoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        rvMisAnuncios = findViewById(R.id.rv_mis_productos);
        tvNoAnuncios = findViewById(R.id.tv_no_anuncios);

        mAuth = FirebaseAuth.getInstance();
        productosRef = FirebaseDatabase.getInstance().getReference("productos");

        listaProductos = new ArrayList<>();
        productoAdapter = new ProductoAdapter(this, listaProductos);

        rvMisAnuncios.setLayoutManager(new LinearLayoutManager(this));
        rvMisAnuncios.setAdapter(productoAdapter);

        productoAdapter.setOnProductoClickListener(new ProductoAdapter.OnProductoClickListener() {
            @Override
            public void onEditarClick(Producto producto) {
                Intent intent = new Intent(AnunciosActivity.this, EditarProductoActivity.class);
                intent.putExtra("productoId", producto.getId());
                startActivity(intent);
            }

            @Override
            public void onEliminarClick(Producto producto) {
                eliminarProducto(producto);
            }
        });

        cargarMisAnuncios();
    }

    private void cargarMisAnuncios() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            mostrarMensajeNoAnuncios(true);
            return;
        }

        String userId = currentUser.getUid();
        Query query = productosRef.orderByChild("vendedorId").equalTo(userId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaProductos.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        Producto p = s.getValue(Producto.class);
                        if (p != null) {
                            p.setId(s.getKey());
                            listaProductos.add(p);
                        }
                    }
                }
                productoAdapter.actualizarProductos(listaProductos);
                mostrarMensajeNoAnuncios(listaProductos.isEmpty());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AnunciosActivity.this,"Error: "+error.getMessage(),Toast.LENGTH_LONG).show();
                mostrarMensajeNoAnuncios(true);
            }
        });
    }

    private void mostrarMensajeNoAnuncios(boolean mostrar) {
        tvNoAnuncios.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        rvMisAnuncios.setVisibility(mostrar ? View.GONE : View.VISIBLE);
    }

    private void eliminarProducto(Producto producto) {
        if (producto.getId() == null) return;

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar anuncio")
                .setMessage("¿Seguro que deseas eliminar este anuncio?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    productosRef.child(producto.getId()).removeValue()
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(AnunciosActivity.this, "Anuncio eliminado", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(AnunciosActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .setNegativeButton("No", null)
                .show();
    }
}

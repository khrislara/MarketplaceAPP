package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import com.example.myapplication.adapter.ProductoAdapter;
import com.example.myapplication.model.Producto;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class Home extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final int EDITAR_PRODUCTO_REQUEST = 1001;

    // Vistas principales
    private RecyclerView recyclerView;
    private FloatingActionButton fabPublicar;
    private BottomNavigationView bottomNav;
    private Toolbar toolbar;

    // Buscador y adaptador
    private SearchView searchViewProductos;
    private ProductoAdapter productoAdapter;

    // Firebase
    private DatabaseReference databaseRef;
    private List<Producto> listaProductosActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 1. Inicializar Vistas y Toolbar
        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.rv_productos);
        fabPublicar = findViewById(R.id.fab_publicar);
        searchViewProductos = findViewById(R.id.search_view_productos);
        bottomNav = findViewById(R.id.bottom_navigation_view);

        // Firebase productos
        databaseRef = FirebaseDatabase.getInstance().getReference("productos");
        listaProductosActual = new ArrayList<>();

        // Configurar Recycler
        configurarRecyclerView();

        // Configurar buscador
        configurarBuscador();

        // Publicar nuevo producto
        fabPublicar.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, Publicar.class);
            startActivity(intent);
        });

        // --- BOTTOM NAV ---
        if (bottomNav.getMenu().findItem(R.id.navigation_home) != null)
            bottomNav.getMenu().findItem(R.id.navigation_home).setChecked(true);

        bottomNav.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_chats) {
                Intent intentChat = new Intent(Home.this, ChatActivity.class);
                // TODO: pasar datos reales del otro usuario
                intentChat.putExtra("otherUserId", "USERID_DEL_OTRO_USUARIO");
                intentChat.putExtra("nombreContacto", "Contacto");
                startActivity(intentChat);
                return true;
            } else if (itemId == R.id.navigation_account) {
                startActivity(new Intent(Home.this, CuentaActivity.class));
                return true;
            } else if (itemId == R.id.navigation_ads) {
                startActivity(new Intent(Home.this, AnunciosActivity.class));
                return true;
            } else if (itemId == R.id.navigation_home) {
                return true;
            }

            Toast.makeText(Home.this, "Navegando a: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });

        // Cargar productos
        cargarProductosDesdeFirebase();
    }

    private void configurarBuscador() {
        if (searchViewProductos != null) {
            searchViewProductos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) { return false; }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (productoAdapter != null) productoAdapter.filtrar(newText);
                    return true;
                }
            });
        }
    }

    private void configurarRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productoAdapter = new ProductoAdapter(this, listaProductosActual);

        // Configurar listener para editar/eliminar productos
        productoAdapter.setOnProductoClickListener(new ProductoAdapter.OnProductoClickListener() {
            @Override
            public void onEditarClick(Producto producto) {
                if (producto.getId() != null) {
                    Intent intent = new Intent(Home.this, EditarProductoActivity.class);
                    intent.putExtra("productoId", producto.getId());
                    startActivityForResult(intent, EDITAR_PRODUCTO_REQUEST);
                }
            }

            @Override
            public void onEliminarClick(Producto producto) {
                if (producto.getId() != null) {
                    databaseRef.child(producto.getId()).removeValue()
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(Home.this, "Producto eliminado", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(Home.this, "Error al eliminar producto", Toast.LENGTH_SHORT).show());
                }
            }
        });

        recyclerView.setAdapter(productoAdapter);
    }

    private void cargarProductosDesdeFirebase() {
        Log.d(TAG, "Cargando productos...");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaProductosActual.clear();

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    try {
                        Producto producto = productSnapshot.getValue(Producto.class);
                        if (producto != null) {
                            producto.setId(productSnapshot.getKey());
                            listaProductosActual.add(producto);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error producto: " + productSnapshot.getKey(), e);
                    }
                }

                productoAdapter.actualizarProductos(listaProductosActual);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error Firebase: " + error.getMessage());
                Toast.makeText(Home.this, "Error al cargar productos", Toast.LENGTH_LONG).show();
            }
        });
    }

    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDITAR_PRODUCTO_REQUEST && resultCode == RESULT_OK){
            cargarProductosDesdeFirebase(); // recargar lista
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            irALogin();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void irALogin() {
        Intent intent = new Intent(Home.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.adapter.ImagenesAdapter;
import com.example.myapplication.model.Producto;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class Detalle_Producto extends AppCompatActivity {

    public static final String EXTRA_PRODUCTO_ID = "producto_id";

    // Views
    private ViewPager2 vpImagenes;
    private TextView tvNombreDetalle, tvPrecioDetalle, tvDescripcionDetalle;
    private TextView tvMarcaDetalle, tvCategoriaDetalle, tvCondicionDetalle, tvDireccionDetalle;
    private TextView tvVendedorNombre, tvVendedorEmail, tvVendedorTelefono;
    private MaterialButton fabContactar;

    // Firebase
    private DatabaseReference productosRef, usuariosRef;

    // Teléfono vendedor
    private String telefonoVendedor = null;

    private static final String TAG = "Detalle_Producto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_detalle);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detalle del Producto");
        }

        // Firebase
        productosRef = FirebaseDatabase.getInstance().getReference("productos");
        usuariosRef = FirebaseDatabase.getInstance().getReference("users");

        // Views
        vpImagenes = findViewById(R.id.vp_imagenes_producto);
        tvNombreDetalle = findViewById(R.id.tv_detalle_titulo);
        tvPrecioDetalle = findViewById(R.id.tv_detalle_precio);
        tvDescripcionDetalle = findViewById(R.id.tv_detalle_descripcion);
        tvMarcaDetalle = findViewById(R.id.tv_detalle_marca);
        tvCategoriaDetalle = findViewById(R.id.tv_detalle_categoria);
        tvCondicionDetalle = findViewById(R.id.tv_detalle_condicion);
        tvDireccionDetalle = findViewById(R.id.tv_detalle_direccion);

        tvVendedorNombre = findViewById(R.id.tv_detalle_vendedor);
        tvVendedorEmail = findViewById(R.id.tv_vendedor_email);
        tvVendedorTelefono = findViewById(R.id.tv_vendedor_telefono);
        fabContactar = findViewById(R.id.fab_contactar);

        tvVendedorNombre.setText("Nombre: Cargando detalles...");

        // Obtener ID del producto
        Intent intent = getIntent();
        String productoId = intent.getStringExtra(EXTRA_PRODUCTO_ID);
        if (productoId != null) {
            cargarDatosProducto(productoId);
        } else {
            Toast.makeText(this, "Error: No se encontró el ID del producto.", Toast.LENGTH_LONG).show();
            finish();
        }

        // Botón contactar
        fabContactar.setOnClickListener(v -> realizarLlamada());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cargarDatosProducto(String productoId){
        productosRef.child(productoId).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                if(snapshot.exists()){
                    Producto producto = snapshot.getValue(Producto.class);
                    if(producto != null){
                        mostrarDetallesProducto(producto);
                        String vendedorId = producto.getVendedorId();
                        if(vendedorId != null) cargarDatosVendedor(vendedorId);
                        else mostrarDetallesVendedor("Vendedor Desconocido","N/A", null);
                    } else {
                        mostrarDetallesProductoIndividual(snapshot);
                    }
                } else {
                    Toast.makeText(Detalle_Producto.this,"Producto no encontrado.",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                Log.e(TAG,"Error Firebase: "+error.getMessage());
                Toast.makeText(Detalle_Producto.this,"Error al cargar datos.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDetallesProducto(Producto producto){
        // Carrusel de imágenes
        if(producto.getImageUrls() != null && !producto.getImageUrls().isEmpty()){
            ImagenesAdapter adapter = new ImagenesAdapter(producto.getImageUrls());
            vpImagenes.setAdapter(adapter);
        }

        tvNombreDetalle.setText(producto.getNombre());
        tvDescripcionDetalle.setText(producto.getDescripcion());

        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es","CL"));
        tvPrecioDetalle.setText(format.format(producto.getPrecio()));

        tvMarcaDetalle.setText("Marca: "+(producto.getMarca()!=null?producto.getMarca():"N/A"));
        tvCondicionDetalle.setText("Condición: "+(producto.getCondicion()!=null?producto.getCondicion():"N/A"));
        tvCategoriaDetalle.setText("Categoría: "+(producto.getCategoria()!=null?producto.getCategoria():"N/A"));
        tvDireccionDetalle.setText("Retiro en: "+(producto.getDireccion()!=null?producto.getDireccion():"No provista"));
    }

    private void mostrarDetallesProductoIndividual(DataSnapshot snapshot){
        String nombre = snapshot.child("nombre").getValue(String.class);
        Double precio = snapshot.child("precio").getValue(Double.class);
        String descripcion = snapshot.child("descripcion").getValue(String.class);
        String marca = snapshot.child("marca").getValue(String.class);
        String condicion = snapshot.child("condicion").getValue(String.class);
        String categoria = snapshot.child("categoria").getValue(String.class);
        String direccion = snapshot.child("direccion").getValue(String.class);
        String vendedorId = snapshot.child("vendedorId").getValue(String.class);

        tvNombreDetalle.setText(nombre!=null?nombre:"N/A");
        tvDescripcionDetalle.setText(descripcion!=null?descripcion:"Sin descripción");
        if(precio!=null){
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es","CL"));
            tvPrecioDetalle.setText(format.format(precio));
        } else tvPrecioDetalle.setText("Precio: N/A");

        tvMarcaDetalle.setText("Marca: "+(marca!=null?marca:"No especificada"));
        tvCondicionDetalle.setText("Condición: "+(condicion!=null?condicion:"N/A"));
        tvCategoriaDetalle.setText("Categoría: "+(categoria!=null?categoria:"N/A"));
        tvDireccionDetalle.setText("Retiro en: "+(direccion!=null?direccion:"No provista"));

        // Imagenes
        DataSnapshot imagesSnapshot = snapshot.child("imageUrls");
        if(imagesSnapshot.exists() && imagesSnapshot.getChildrenCount()>0){
            ImagenesAdapter adapter = new ImagenesAdapter();
            for(DataSnapshot imgSnap : imagesSnapshot.getChildren()){
                String url = imgSnap.getValue(String.class);
                if(url!=null) adapter.addImagen(url);
            }
            vpImagenes.setAdapter(adapter);
        }

        if(vendedorId!=null) cargarDatosVendedor(vendedorId);
    }

    private void cargarDatosVendedor(String vendedorId){
        usuariosRef.child(vendedorId).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                String nombre = snapshot.child("nombre").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String codigo = snapshot.child("codigoTelefono").getValue(String.class);
                String numero = String.valueOf(snapshot.child("telefono").getValue());
                String telefonoCompleto = combinarTelefono(codigo, numero);
                telefonoVendedor = telefonoCompleto;
                mostrarDetallesVendedor(nombre,email,telefonoCompleto);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error){
                mostrarDetallesVendedor("Error de carga","No disponible",null);
            }
        });
    }

    private void mostrarDetallesVendedor(String nombre,String email,String telefono){
        tvVendedorNombre.setText("Nombre: "+(nombre!=null?nombre:"Desconocido"));
        tvVendedorEmail.setText("Correo: "+(email!=null?email:"No proporcionado"));
        tvVendedorTelefono.setText("Teléfono: "+(telefono!=null?telefono:"No disponible"));
        fabContactar.setEnabled(telefono!=null && !telefono.isEmpty() && telefono.matches("^\\+?[0-9\\s()-]*$"));
    }

    private void realizarLlamada(){
        if(telefonoVendedor==null || telefonoVendedor.isEmpty()){
            Toast.makeText(this,"Vendedor no tiene teléfono disponible.",Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            Uri uri = Uri.parse("tel:"+telefonoVendedor);
            Intent intent = new Intent(Intent.ACTION_DIAL,uri);
            if(intent.resolveActivity(getPackageManager())!=null){
                startActivity(intent);
            } else {
                Toast.makeText(this,"No hay app para realizar llamadas.",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            Toast.makeText(this,"Error al iniciar la llamada.",Toast.LENGTH_SHORT).show();
        }
    }

    private String combinarTelefono(String codigo, String numero){
        if(codigo!=null && !codigo.isEmpty() && numero!=null && !numero.isEmpty()){
            String c = codigo.replaceAll("[^0-9]","");
            String n = numero.replaceAll("[^0-9]","");
            return "+"+c+n;
        }
        return null;
    }
}

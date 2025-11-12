package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
public class Detalle_Producto extends AppCompatActivity {
    public static final String EXTRA_PRODUCTO_ID = "producto_id";
    // Vistas
    private ImageView ivImagenDetalle;
    private TextView tvNombreDetalle;
    private TextView tvPrecioDetalle;
    private TextView tvDescripcionDetalle;
    private TextView tvVendedorDetalle;
    private ExtendedFloatingActionButton fabContactar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);
        // 1. Inicializar Vistas
        ivImagenDetalle = findViewById(R.id.iv_detalle_imagen);
        // ID: tv_detalle_titulo
        tvNombreDetalle = findViewById(R.id.tv_detalle_titulo);
        // ID: tv_detalle_precio
        tvPrecioDetalle = findViewById(R.id.tv_detalle_precio);
        // ID: tv_detalle_descripcion
        tvDescripcionDetalle = findViewById(R.id.tv_detalle_descripcion);
        // ID: tv_detalle_vendedor
        tvVendedorDetalle = findViewById(R.id.tv_detalle_vendedor);
        // ID: fab_contactar
        fabContactar = findViewById(R.id.fab_contactar);
        // 2. Obtener datos del Intent
        String productoId = getIntent().getStringExtra(EXTRA_PRODUCTO_ID);
        if (productoId != null) {
            Toast.makeText(this, "Detalles cargados para el ID: " + productoId,
                    Toast.LENGTH_SHORT).show();
            cargarDatosSimulados(productoId);
        } else {
            Toast.makeText(this, "Error: No se encontró el ID del producto.",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        // 3. Botón Contactar (Manejador de clic del FAB)
        fabContactar.setOnClickListener(v -> {
            Toast.makeText(this, "Simulando chat con el vendedor...", Toast.LENGTH_SHORT).show();
        });
    }
    private void cargarDatosSimulados(String id) {
        tvNombreDetalle.setText("Bicicleta Eléctrica Turbo X" + id.substring(0, 2));
        tvPrecioDetalle.setText("$450.000 CLP");
        tvDescripcionDetalle.setText("Modelo 2024, casi nueva. Perfecta para la ciudad y subir cuestas sin esfuerzo. Incluye cargador y garantía.");
                tvVendedorDetalle.setText("Usuario: DiegoDev");
    }
}
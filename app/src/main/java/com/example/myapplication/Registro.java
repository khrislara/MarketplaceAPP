package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private TextInputEditText etNombre, etEmail, etPassword, etTelefono;
    private Button btnRegistro;
    private TextView tvIrLogin;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etNombre = findViewById(R.id.et_nombre);
        etEmail = findViewById(R.id.et_email);
        etTelefono = findViewById(R.id.et_telefono);
        etPassword = findViewById(R.id.et_password);
        btnRegistro = findViewById(R.id.btn_registro);
        tvIrLogin = findViewById(R.id.tv_link_login);

        btnRegistro.setOnClickListener(v -> {
            if (validarCampos()) {
                registrarUsuario();
            }
        });

        tvIrLogin.setOnClickListener(v -> irALogin());
    }

    private void registrarUsuario() {
        final String nombre = etNombre.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            guardarDatosUsuario(user.getUid(), nombre, email, telefono);
                        }
                        Toast.makeText(Registro.this, "¡Registro exitoso! Bienvenido.", Toast.LENGTH_SHORT).show();
                        irALogin();
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Error de registro desconocido.";
                        Toast.makeText(Registro.this, "Fallo el registro: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void guardarDatosUsuario(String uid, String nombre, String email, String telefono) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", nombre);
        userData.put("email", email);
        userData.put("telefono", telefono);
        userData.put("fecha_creacion", System.currentTimeMillis());

        mDatabase.child("users").child(uid).setValue(userData)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(Registro.this, "Advertencia: No se pudieron guardar los datos del perfil.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validarCampos() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean valido = true;

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("El nombre es obligatorio.");
            valido = false;
        } else {
            etNombre.setError(null);
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Ingrese un email válido.");
            valido = false;
        } else {
            etEmail.setError(null);
        }

        if (TextUtils.isEmpty(telefono) || !Patterns.PHONE.matcher(telefono).matches()) {
            etTelefono.setError("Ingrese un número de teléfono válido.");
            valido = false;
        } else {
            etTelefono.setError(null);
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres.");
            valido = false;
        } else {
            etPassword.setError(null);
        }

        return valido;
    }

    private void irALogin() {
        Intent intent = new Intent(Registro.this, Login.class);
        startActivity(intent);
        finish();
    }
}

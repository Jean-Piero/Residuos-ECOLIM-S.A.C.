package com.zerodevelop.residuos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistroActivity extends AppCompatActivity {
    TextView txtTituloRegistro;
    TextView txtEmpresaRegistro;
    EditText etnombre, etcorreo, ettelefono, etpassword, etconfirmarpassword;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    TextView txtYaCuenta;
    Button btnregistrar;

    String nombre = "", correo = "", telefono = "", password = "", confirmarpassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etnombre           = findViewById(R.id.txtnombre);
        etcorreo           = findViewById(R.id.txtcorreo);
        ettelefono         = findViewById(R.id.txttelefono);
        etpassword         = findViewById(R.id.txtpassword);
        etconfirmarpassword = findViewById(R.id.txtconfirmarpassword);
        btnregistrar       = findViewById(R.id.btnregistrar);
        txtYaCuenta        = findViewById(R.id.txtYaCuenta);

        txtTituloRegistro  = findViewById(R.id.txtTituloRegistro);
        txtEmpresaRegistro = findViewById(R.id.txtEmpresaRegistro);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(RegistroActivity.this);
        progressDialog.setTitle("Espere por favor...");
        progressDialog.setCanceledOnTouchOutside(false);

        txtYaCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroActivity.this, MainActivity.class));
                finish();
            }
        });

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });

        txtTituloRegistro.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(200)
                .start();

        txtEmpresaRegistro.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(700)
                .start();
    }

    private void validarDatos() {
        nombre            = etnombre.getText().toString().trim();
        correo            = etcorreo.getText().toString().trim();
        telefono          = ettelefono.getText().toString().trim();
        password          = etpassword.getText().toString().trim();
        confirmarpassword = etconfirmarpassword.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(this, "El campo nombre está vacío", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(telefono)) {
            Toast.makeText(this, "El campo teléfono está vacío", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password) || password.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener mínimo 8 caracteres", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmarpassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        } else {
            registrar();
        }
    }

    private void registrar() {
        progressDialog.setMessage("Registrando usuario...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(correo, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        guardarUsuario();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegistroActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarUsuario() {
        progressDialog.setMessage("Guardando información...");

        String uid = firebaseAuth.getUid();

        HashMap<String, String> datosUsuario = new HashMap<>();
        datosUsuario.put("uid", uid);
        datosUsuario.put("nombre", nombre);
        datosUsuario.put("correo", correo);
        datosUsuario.put("telefono", telefono);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("usuarios");
        databaseReference.child(uid).setValue(datosUsuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(RegistroActivity.this, "Usuario creado exitosamente", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistroActivity.this, DashBoardActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(RegistroActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
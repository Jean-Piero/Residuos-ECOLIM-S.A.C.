package com.zerodevelop.residuos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView Registrarse, txtBienvenido, txtEmpresa;
    TextInputEditText etcorreol, etpasswordl;
    Button btnregistrar;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String correo = "", password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etcorreol    = findViewById(R.id.txtusuario);
        etpasswordl  = findViewById(R.id.txtpassword);
        btnregistrar = findViewById(R.id.btnregistrar);
        Registrarse  = findViewById(R.id.Registrarse);
        txtBienvenido = findViewById(R.id.txtBienvenido);
        txtEmpresa    = findViewById(R.id.txtEmpresa);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);

        txtBienvenido.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(200)
                .start();

        txtEmpresa.setTranslationY(30f);
        txtEmpresa.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .setStartDelay(700)
                .start();

        btnregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarDatos();
            }
        });

        Registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistroActivity.class));
            }
        });
    }
    private void validarDatos() {
        correo   = etcorreol.getText().toString().trim();
        password = etpasswordl.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingrese un correo válido", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Ingrese su contraseña", Toast.LENGTH_SHORT).show();
        } else {
            logearUsuario();
        }
    }
    private void logearUsuario() {
        progressDialog.setMessage("Iniciando sesión...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Bienvenido(a): " + firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, DashBoardActivity.class));
                            finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Ocurrió un problema", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
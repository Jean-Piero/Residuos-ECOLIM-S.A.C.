package com.zerodevelop.residuos.RegistroResiduos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zerodevelop.residuos.DashBoardActivity;
import com.zerodevelop.residuos.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RegistroResiduosActivity extends AppCompatActivity {

    CardView btnAtras, btnGuardar;
    Spinner spinnerTipo, spinnerUnidad;
    TextInputEditText txtCantidad;
    TextView txtFecha;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_residuos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerTipo   = findViewById(R.id.spinnerTipo);
        spinnerUnidad = findViewById(R.id.spinnerUnidad);
        txtCantidad   = findViewById(R.id.txtCantidad);
        txtFecha      = findViewById(R.id.txtFecha);
        btnGuardar    = findViewById(R.id.btnGuardar);
        btnAtras      = findViewById(R.id.btnAtras);

        firebaseAuth      = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("residuos");

        String fechaActual = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        txtFecha.setText(fechaActual);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroResiduosActivity.this, DashBoardActivity.class));
            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
    }
    private void validarDatos() {
        String cantidad = txtCantidad.getText().toString().trim();
        if (TextUtils.isEmpty(cantidad)) {
            Toast.makeText(this, "Ingrese la cantidad", Toast.LENGTH_SHORT).show();
            return;
        }
        guardarResiduo(cantidad);
    }
    private void guardarResiduo(String cantidad) {
        String uid       = firebaseAuth.getUid();
        String tipo      = spinnerTipo.getSelectedItem().toString();
        String unidad    = spinnerUnidad.getSelectedItem().toString();
        String fecha     = txtFecha.getText().toString();
        String idResiduo = databaseReference.push().getKey();

        HashMap<String, String> datos = new HashMap<>();
        datos.put("id",       idResiduo);
        datos.put("uid",      uid);
        datos.put("tipo",     tipo);
        datos.put("cantidad", cantidad);
        datos.put("unidad",   unidad);
        datos.put("fecha",    fecha);

        databaseReference.child(idResiduo).setValue(datos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RegistroResiduosActivity.this,
                                "Residuo registrado correctamente", Toast.LENGTH_SHORT).show();
                        limpiarFormulario();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistroResiduosActivity.this,
                                "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void limpiarFormulario() {
        txtCantidad.setText("");
        spinnerTipo.setSelection(0);
        spinnerUnidad.setSelection(0);
        String fechaActual = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        txtFecha.setText(fechaActual);
    }
}
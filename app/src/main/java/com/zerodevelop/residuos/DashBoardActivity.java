package com.zerodevelop.residuos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerodevelop.residuos.ListaRegistro.ListaRegistroActivity;
import com.zerodevelop.residuos.RegistroResiduos.RegistroResiduosActivity;
import com.zerodevelop.residuos.Reporte.ReporteActivity;

public class DashBoardActivity extends AppCompatActivity {

    TextView txtNombreUsuario, txtCorreoUsuario, txtTelefonoUsuario;
    CardView cardListaRegistro, cardRegistroResiduos, cardReporte, btnCerrarSesion;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtNombreUsuario     = findViewById(R.id.txtNombreUsuario);
        txtCorreoUsuario     = findViewById(R.id.txtCorreoUsuario);
        txtTelefonoUsuario   = findViewById(R.id.txtTelefonoUsuario);
        cardListaRegistro    = findViewById(R.id.cardListaRegistro);
        cardRegistroResiduos = findViewById(R.id.cardRegistroResiduos);
        cardReporte          = findViewById(R.id.cardReporte);
        btnCerrarSesion      = findViewById(R.id.btnCerrarSesion);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String nombre   = "" + snapshot.child("nombre").getValue();
                String correo   = "" + snapshot.child("correo").getValue();
                String telefono = "" + snapshot.child("telefono").getValue();

                txtNombreUsuario.setText(nombre);
                txtCorreoUsuario.setText(correo);
                txtTelefonoUsuario.setText(telefono);
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        cardListaRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this, ListaRegistroActivity.class));
            }
        });

        cardRegistroResiduos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this, RegistroResiduosActivity.class));
            }
        });

        cardReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoardActivity.this, ReporteActivity.class));
            }
        });

        // Cerrar sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(DashBoardActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
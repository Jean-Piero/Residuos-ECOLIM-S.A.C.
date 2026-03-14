package com.zerodevelop.residuos.ListaRegistro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerodevelop.residuos.DashBoardActivity;
import com.zerodevelop.residuos.R;

import java.util.ArrayList;

public class ListaRegistroActivity extends AppCompatActivity {
    ImageView btnAtras;
    ListView listViewResiduos;
    LinearLayout layoutVacio;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewResiduos = findViewById(R.id.listViewResiduos);
        layoutVacio      = findViewById(R.id.layoutVacio);
        btnAtras = findViewById(R.id.btnAtras);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListaRegistroActivity.this, DashBoardActivity.class));
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("residuos");

        cargarResiduos();
    }
    private void cargarResiduos() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> items = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String tipo     = "" + ds.child("tipo").getValue();
                    String cantidad = "" + ds.child("cantidad").getValue();
                    String unidad   = "" + ds.child("unidad").getValue();
                    String fecha    = "" + ds.child("fecha").getValue();

                    items.add(tipo + " | " + cantidad + " " + unidad + "\n" + fecha);
                }
                if (items.isEmpty()) {
                    layoutVacio.setVisibility(View.VISIBLE);
                    listViewResiduos.setVisibility(View.GONE);
                } else {
                    layoutVacio.setVisibility(View.GONE);
                    listViewResiduos.setVisibility(View.VISIBLE);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            ListaRegistroActivity.this,
                            android.R.layout.simple_list_item_1,
                            items
                    );
                    listViewResiduos.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
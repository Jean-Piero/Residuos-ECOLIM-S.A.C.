package com.zerodevelop.residuos.Reporte;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.zerodevelop.residuos.DashBoardActivity;
import com.zerodevelop.residuos.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReporteActivity extends AppCompatActivity {

    Spinner spinnerFiltroTipo;
    CardView btnGenerar, btnAtras, cardReporteCompleto;
    TextView txtTotalRegistros, txtTotalKg, txtTotalLitros, txtTotalUnidades;
    TextView txtFechaReporte, txtResponsable;
    ListView listViewReporte;
    DatabaseReference databaseResiduos, databaseUsuarios;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reporte);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerFiltroTipo   = findViewById(R.id.spinnerFiltroTipo);
        btnGenerar          = findViewById(R.id.btnGenerar);
        btnAtras            = findViewById(R.id.btnAtras);
        cardReporteCompleto = findViewById(R.id.cardReporteCompleto);
        txtTotalRegistros   = findViewById(R.id.txtTotalRegistros);
        txtTotalKg          = findViewById(R.id.txtTotalKg);
        txtTotalLitros      = findViewById(R.id.txtTotalLitros);
        txtTotalUnidades    = findViewById(R.id.txtTotalUnidades);
        txtFechaReporte     = findViewById(R.id.txtFechaReporte);
        txtResponsable      = findViewById(R.id.txtResponsable);
        listViewReporte     = findViewById(R.id.listViewReporte);

        firebaseAuth      = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        databaseResiduos  = FirebaseDatabase.getInstance().getReference("residuos");
        databaseUsuarios  = FirebaseDatabase.getInstance().getReference("usuarios").child(firebaseUser.getUid());

        btnGenerar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarReporte();
            }
        });

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReporteActivity.this, DashBoardActivity.class));
            }
        });
    }

    private void generarReporte() {
        String filtroTipo  = spinnerFiltroTipo.getSelectedItem().toString();
        String fechaActual = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        txtFechaReporte.setText(fechaActual);

        databaseUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nombre = "" + snapshot.child("nombre").getValue();
                txtResponsable.setText("Responsable: " + nombre);
                cargarResiduos(filtroTipo);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    private void cargarResiduos(String filtroTipo) {
        databaseResiduos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> items = new ArrayList<>();
                double totalKg     = 0;
                double totalLitros = 0;
                int totalUnidades  = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String tipo     = "" + ds.child("tipo").getValue();
                    String cantidad = "" + ds.child("cantidad").getValue();
                    String unidad   = "" + ds.child("unidad").getValue();
                    String fecha    = "" + ds.child("fecha").getValue();

                    if (filtroTipo.equals("Todos") || filtroTipo.equals(tipo)) {
                        items.add("▸ " + tipo + "\n   " + cantidad + " " + unidad + "  •  " + fecha);
                        try {
                            double valor = Double.parseDouble(cantidad);
                            if (unidad.equals("kg")) {
                                totalKg += valor;
                            } else if (unidad.equals("litros")) {
                                totalLitros += valor;
                            } else if (unidad.equals("unidades")) {
                                totalUnidades += (int) valor;
                            }
                        } catch (NumberFormatException e) { }
                    }
                }
                txtTotalRegistros.setText(String.valueOf(items.size()));
                txtTotalKg.setText(String.format(Locale.getDefault(), "%.2f kg", totalKg));
                txtTotalLitros.setText(String.format(Locale.getDefault(), "%.2f lt", totalLitros));
                txtTotalUnidades.setText(String.valueOf(totalUnidades));

                if (items.isEmpty()) {
                    items.add(getString(R.string.lbl_sin_resultados));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        ReporteActivity.this,
                        android.R.layout.simple_list_item_1,
                        items
                );
                listViewReporte.setAdapter(adapter);
                cardReporteCompleto.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
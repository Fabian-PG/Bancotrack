package com.example.bancotrack;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivityBD extends AppCompatActivity implements PuntoRetiroAdapter.OnPuntoListener{

    private EditText etNombre, etDireccion, etTipo, etBusqueda;
    private Button btnRegistrar, btnGuardarCambios, btnEliminar;
    private RecyclerView recyclerView;
    private PuntoRetiroAdapter adapter;

    // Guarda el nombre original del punto cuando se entra en modo edición/eliminación
    private String nombreOriginalEdicion = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_bd);

        // 1. Inicialización de Vistas
        etNombre = findViewById(R.id.txtNombrePunto);
        etDireccion = findViewById(R.id.txtDireccionPunto);
        etTipo = findViewById(R.id.txtTipoPunto);
        etBusqueda = findViewById(R.id.txtBusqueda);

        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        btnEliminar = findViewById(R.id.btnEliminar);

        // 2. Configuración del RecyclerView
        recyclerView = findViewById(R.id.rvPuntos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PuntoRetiroAdapter(this, this); // 'this' como OnPuntoListener
        recyclerView.setAdapter(adapter);

        // 3. Cargar la lista inicial de puntos
        CargarPuntos(null);

        // 4. Configurar el TextWatcher para BÚSQUEDA en tiempo real
        setupSearchWatcher();

        // 5. Configurar los clics de los botones
        AccionesBotones();

        // 6. Ocultar campos de CRUD al inicio
        ocultarCamposCRUD();

    }

    // Método implementado de la interfaz OnPuntoListener
    @Override
    public void onPuntoClick(PuntoRetiro puntoSeleccionado) {
        // Cargar los datos del punto seleccionado en los campos para edición/eliminación
        etNombre.setText(puntoSeleccionado.getNombre());
        etDireccion.setText(puntoSeleccionado.getDireccion());
        etTipo.setText(puntoSeleccionado.getTipo());

        nombreOriginalEdicion = puntoSeleccionado.getNombre();

        // Entrar en modo edición
        mostrarCamposCRUD(true);
    }

    // --- LÓGICA DE UI Y BÚSQUEDA ---

    private void ocultarCamposCRUD() {
        etNombre.setVisibility(View.GONE);
        etDireccion.setVisibility(View.GONE);
        etTipo.setVisibility(View.GONE);
        btnGuardarCambios.setVisibility(View.GONE);
        btnEliminar.setVisibility(View.GONE); // Eliminar también se oculta si no estamos en edición

        // El botón Registrar se mantiene visible para iniciar un nuevo registro
        btnRegistrar.setText("Registrar Nuevo Punto");
        limpiarCampos();
    }

    private void mostrarCamposCRUD(boolean esEdicion) {
        etNombre.setVisibility(View.VISIBLE);
        etDireccion.setVisibility(View.VISIBLE);
        etTipo.setVisibility(View.VISIBLE);

        if (esEdicion) {
            etNombre.setEnabled(false); // No se puede cambiar el nombre (es la PK)
            btnGuardarCambios.setVisibility(View.VISIBLE);
            btnEliminar.setVisibility(View.VISIBLE);
            btnRegistrar.setVisibility(View.GONE); // Ocultar Registrar en modo edición
        } else {
            etNombre.setEnabled(true);
            btnGuardarCambios.setVisibility(View.VISIBLE);
            btnEliminar.setVisibility(View.GONE);
            btnRegistrar.setVisibility(View.GONE);
            limpiarCampos();
        }
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etDireccion.setText("");
        etTipo.setText("");
        nombreOriginalEdicion = "";
    }

    private void setupSearchWatcher() {
        etBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                // Filtra la lista cada vez que el usuario escribe
                CargarPuntos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Para que el botón buscar solo recargue la lista, ya que el TextWatcher hace el filtro
        findViewById(R.id.btnBuscar).setOnClickListener(view -> CargarPuntos(etBusqueda.getText().toString()));
    }

    // --- LÓGICA DE DATOS (Read) ---

    public void CargarPuntos(String busqueda) {
        AdminSQLiteOpenHelper adminBD = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase baseDeDatos = adminBD.getReadableDatabase();

        List<PuntoRetiro> listaPuntos = new ArrayList<>();
        Cursor cursor;

        if (busqueda == null || busqueda.trim().isEmpty()) {
            cursor = baseDeDatos.rawQuery(
                    "SELECT * FROM " + AdminSQLiteOpenHelper.NOMBRE_TABLA + " ORDER BY " + AdminSQLiteOpenHelper.COL_NOMBRE, null);
        } else {
            // Búsqueda LIKE en nombre, dirección o tipo
            String query = "%" + busqueda.trim() + "%";
            cursor = baseDeDatos.rawQuery(
                    "SELECT * FROM " + AdminSQLiteOpenHelper.NOMBRE_TABLA +
                            " WHERE " + AdminSQLiteOpenHelper.COL_NOMBRE + " LIKE ? OR " +
                            AdminSQLiteOpenHelper.COL_DIRECCION + " LIKE ? OR " +
                            AdminSQLiteOpenHelper.COL_TIPO + " LIKE ?",
                    new String[]{query, query, query});
        }

        if (cursor.moveToFirst()) {
            do {
                // Obtener datos por el nombre de la columna
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteOpenHelper.COL_NOMBRE));
                String direccion = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteOpenHelper.COL_DIRECCION));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(AdminSQLiteOpenHelper.COL_TIPO));

                listaPuntos.add(new PuntoRetiro(nombre, direccion, tipo));

            } while (cursor.moveToNext());
        }

        cursor.close();
        baseDeDatos.close();

        adapter.setPuntos(listaPuntos);
    }

    // --- LÓGICA DE ACCIONES DE BOTONES (CRUD) ---

    public void AccionesBotones() {
        // Botón REGISTRAR (Activa el modo de ingreso de datos)
        btnRegistrar.setOnClickListener(view -> mostrarCamposCRUD(false));

        // Botón GUARDAR CAMBIOS (Realiza Insertar o Actualizar)
        btnGuardarCambios.setOnClickListener(view -> {
            if (etNombre.isEnabled()) {
                // Si el campo nombre está habilitado, es un nuevo registro
                RegistraPunto();
            } else {
                // Si el campo nombre está deshabilitado, es una edición
                ActualizarPunto();
            }
        });

        // Botón ELIMINAR
        btnEliminar.setOnClickListener(view -> EliminarPunto());
    }

    // METODO PARA REGISTRAR PUNTO (Create)
    public void RegistraPunto() {
        AdminSQLiteOpenHelper adminBD = new AdminSQLiteOpenHelper(this);
        SQLiteDatabase baseDeDatos = adminBD.getWritableDatabase();

        String nombre = etNombre.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String tipo = etTipo.getText().toString().trim();

        if (!nombre.isEmpty() && !direccion.isEmpty() && !tipo.isEmpty()) {
            ContentValues registro = new ContentValues();
            registro.put(AdminSQLiteOpenHelper.COL_NOMBRE, nombre);
            registro.put(AdminSQLiteOpenHelper.COL_DIRECCION, direccion);
            registro.put(AdminSQLiteOpenHelper.COL_TIPO, tipo);

            long resultado = baseDeDatos.insert(AdminSQLiteOpenHelper.NOMBRE_TABLA, null, registro);
            baseDeDatos.close();

            if (resultado != -1) {
                ocultarCamposCRUD();
                CargarPuntos(null);
                Toast.makeText(this, "Punto Registrado Exitosamente!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error: Ya existe un punto con ese nombre.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_LONG).show();
        }
    }


}
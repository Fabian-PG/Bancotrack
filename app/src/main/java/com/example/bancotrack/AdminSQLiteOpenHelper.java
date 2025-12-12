package com.example.bancotrack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String NOMBRE_DB = "administracionBD";
    private static final int VERSION_DB = 1;
    public static final String NOMBRE_TABLA = "PUNTOS";

    // Nombres de las columnas
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_DIRECCION = "direccion";
    public static final String COL_TIPO = "tipo";

    public AdminSQLiteOpenHelper(@Nullable Context context) {
        super(context, NOMBRE_DB, null, VERSION_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase baseDeDatosV1) {
        // Creamos la tabla PUNTOS. El nombre será la clave primaria.
        baseDeDatosV1.execSQL("CREATE TABLE " + NOMBRE_TABLA + " (" +
                COL_NOMBRE + " text primary key, " +
                COL_DIRECCION + " text, " +
                COL_TIPO + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Eliminamos y recreamos la tabla si la versión cambia (simple migración)
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOMBRE_TABLA);
        onCreate(sqLiteDatabase);
    }
}

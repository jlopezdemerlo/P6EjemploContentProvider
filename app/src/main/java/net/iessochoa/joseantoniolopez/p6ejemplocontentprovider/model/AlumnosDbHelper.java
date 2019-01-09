package net.iessochoa.joseantoniolopez.p6ejemplocontentprovider.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JoseA on 26/12/2016.
 */

public class AlumnosDbHelper extends SQLiteOpenHelper {
    //si cambiamos la versión, en SQLiteOpenHelper se llamará a onUpdate
    private static final int DATABASE_VERSION = 3;
    //nombre del archivo
    private static final String DATABASE_NAME = "Alumnos.db";
    //creamos las sentencias que nos serán útiles en la clase. Muchas de ellas parametrizadas
    private static final String CREATE_TABLE = "CREATE TABLE if not exists " + AlumnoContract.AlumnoEntry.TABLE_NAME + " ("
            + AlumnoContract.AlumnoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + AlumnoContract.AlumnoEntry.NOMBRE + " TEXT NOT NULL,"
            + AlumnoContract.AlumnoEntry.GRUPO + " TEXT NOT NULL,"
            + AlumnoContract.AlumnoEntry.FOTO_URI + " TEXT, "
            + "UNIQUE (" + AlumnoContract.AlumnoEntry.NOMBRE + ")"
            + ")";
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + AlumnoContract.AlumnoEntry.TABLE_NAME;

    public AlumnosDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        crearDatosPrueba(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_TABLE);
        db.execSQL(CREATE_TABLE);
        crearDatosPrueba(db);
    }

    private void crearDatosPrueba(SQLiteDatabase db) {
        ContentValues values= new ContentValues();
        values.put(AlumnoContract.AlumnoEntry.NOMBRE,"Pepe Fernádez");
        values.put(AlumnoContract.AlumnoEntry.GRUPO,"2F");
        values.put(AlumnoContract.AlumnoEntry.FOTO_URI,"");
        db.insertOrThrow(AlumnoContract.AlumnoEntry.TABLE_NAME,null,values);

        values= new ContentValues();
        values.put(AlumnoContract.AlumnoEntry.NOMBRE,"María López");
        values.put(AlumnoContract.AlumnoEntry.GRUPO,"2F");
        values.put(AlumnoContract.AlumnoEntry.FOTO_URI,"");
        db.insertOrThrow(AlumnoContract.AlumnoEntry.TABLE_NAME,null,values);

        values= new ContentValues();
        values.put(AlumnoContract.AlumnoEntry.NOMBRE,"Juan Fernádez");
        values.put(AlumnoContract.AlumnoEntry.GRUPO,"2G");
        values.put(AlumnoContract.AlumnoEntry.FOTO_URI,"");
        db.insertOrThrow(AlumnoContract.AlumnoEntry.TABLE_NAME,null,values);

        values= new ContentValues();
        values.put(AlumnoContract.AlumnoEntry.NOMBRE,"Rita Pérez");
        values.put(AlumnoContract.AlumnoEntry.GRUPO,"2G");
        values.put(AlumnoContract.AlumnoEntry.FOTO_URI,"");
        db.insertOrThrow(AlumnoContract.AlumnoEntry.TABLE_NAME,null,values);

    }
    /**
     * Dada una posición del cursor, nos devuelve un objeto Alumno
     */
    public static Alumno deCursorAAlumno(Cursor cursor) {
        int indiceColumna;
        //obtenemos la posicion de la columna id
        indiceColumna = cursor.getColumnIndex(AlumnoContract.AlumnoEntry._ID);
        //obtenemos el valor del id
        String id = cursor.getString(indiceColumna);
        indiceColumna = cursor.getColumnIndex(AlumnoContract.AlumnoEntry.NOMBRE);
        String nombre = cursor.getString(indiceColumna);
        indiceColumna = cursor.getColumnIndex(AlumnoContract.AlumnoEntry.GRUPO);
        String grupo = cursor.getString(indiceColumna);
        indiceColumna = cursor.getColumnIndex(AlumnoContract.AlumnoEntry.FOTO_URI);
        String foto = cursor.getString(indiceColumna);

        return new Alumno(id, nombre, grupo, foto);

    }
}

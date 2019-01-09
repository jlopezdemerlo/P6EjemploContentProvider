package net.iessochoa.joseantoniolopez.p6ejemplocontentprovider.model;

import android.content.UriMatcher;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.BaseColumns;

/**
 * Es clase nos permitira mantener aislado la base de datos del código
 * Es conveniente crearla y utilizar sus propiedades en vez del los nombres
 * directos de la base de datos en el código
 */

public class AlumnoContract {
    //Se implementó la interfaz BaseColumns con el fin de agregar una columna extra que se recomienda tenga toda tabla.
    public static abstract class AlumnoEntry {
        public static final String TABLE_NAME = "alumno";
        //nombre de las columnas
        public static final String _ID = BaseColumns._ID;//esta columna es necesaria para Android
        public static final String NOMBRE = "nombre";
        public static final String GRUPO = "grupo";
        public static final String FOTO_URI = "fotoUri";
    }
    //*************Contrato ContentProvider***********
    //Autoridad del ContentProvider
    public final static String AUTHORITY ="net.iessochoa.joseantoniolopez.p6ejemplocontentprovider.model.alumnoprovider";
    //Uri de contenido principal
    public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" +AlumnoEntry.TABLE_NAME);
    //Código para Uri para mútiples registros
    public static final int ALLROWS=1;
    //código para uris de un sólo registro
    public static final int SINGLE_ROW=2;

    /**
     * Comparador de URIs de contenido
     */
    public static final UriMatcher uriMatcher;

    // Asignación de URIs
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, AlumnoEntry.TABLE_NAME, ALLROWS);
        uriMatcher.addURI(AUTHORITY, AlumnoEntry.TABLE_NAME + "/#", SINGLE_ROW);
    }

    //Tipo MIME que retorna la consulta de una sola fila

    public final static String SINGLE_MIME =
            "vnd.android.cursor.item/vnd." + AUTHORITY + AlumnoEntry.TABLE_NAME;
    //Tipo MIME que retorna la consulta de {@link CONTENT_URI}

    public final static String MULTIPLE_MIME =
            "vnd.android.cursor.dir/vnd." + AUTHORITY + AlumnoEntry.TABLE_NAME;


}

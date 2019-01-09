package net.iessochoa.joseantoniolopez.p6ejemplocontentprovider.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static net.iessochoa.joseantoniolopez.p6ejemplocontentprovider.model.AlumnoContract.uriMatcher;

/**
 * Created by JoseA on 23/12/2016.
 */

public class AlumnoProvider extends ContentProvider {
    //definimos la base de datos
   private AlumnosDbHelper alumnosDbHelper;

    @Override
    public boolean onCreate() {
        alumnosDbHelper=new AlumnosDbHelper(getContext());
        return true;
    }
    /**
     * Permite identificar el tipo MIME correspondiente a la uri
     * @param uri
     * @return
     */
    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case AlumnoContract.ALLROWS:
                return AlumnoContract.MULTIPLE_MIME;
            case AlumnoContract.SINGLE_ROW:
                return AlumnoContract.SINGLE_MIME;
            default:
                throw new IllegalArgumentException("Tipo de actividad desconocida: "+uri);

        }

    }

    /**
     * Devuelve el un cursor con el resultado de la consulta realizada
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Nullable
    @Override
    public Cursor query(Uri uri,String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //abrimos la base de datos
        SQLiteDatabase db = alumnosDbHelper.getWritableDatabase();
        //miramos el tipo de consulta según la uri
        int match= uriMatcher.match(uri);
        Cursor cursor;
        switch (match){
            case AlumnoContract.ALLROWS://consulta todos los registros
                cursor=db.query(AlumnoContract.AlumnoEntry.TABLE_NAME, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                //nos permite avisar al observador de la uri en caso de que hayan modificaciones
                cursor.setNotificationUri(getContext().getContentResolver(),AlumnoContract.CONTENT_URI);
                break;
            case AlumnoContract.SINGLE_ROW://un solo registro basado en id
                long idAlumno= ContentUris.parseId(uri);
                cursor=db.query(AlumnoContract.AlumnoEntry.TABLE_NAME, projection,
                        AlumnoContract.AlumnoEntry._ID+" = "+idAlumno, selectionArgs,
                        null, null, sortOrder);

                cursor.setNotificationUri(getContext().getContentResolver(),AlumnoContract.CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("Uri no soportada: "+uri.toString());

        }
        return cursor;
    }



    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        // Validar la uri
        if (uriMatcher.match(uri) != AlumnoContract.ALLROWS) {
            throw new IllegalArgumentException("URI desconocida : " + uri);
        }


        // Si es necesario, verifica los valores

        // Inserción de nueva fila
        SQLiteDatabase db = alumnosDbHelper.getWritableDatabase();
        long rowId = db.insertOrThrow(AlumnoContract.AlumnoEntry.TABLE_NAME,null,contentValues);
        if (rowId > 0) {
            Uri uri_alumno =
                    ContentUris.withAppendedId(AlumnoContract.CONTENT_URI, rowId);
            //notificamos al observador que ha habido un cambio
            getContext().getContentResolver().notifyChange(uri_alumno, null);
            return uri_alumno;
        }else
            throw new SQLException("Falla al insertar fila en : " + uri);
    }


    @Override
    public int delete(Uri uri,  String selection, String[] selectionArgs) {
        SQLiteDatabase db = alumnosDbHelper.getWritableDatabase();
        int affected;
        switch (AlumnoContract.uriMatcher.match(uri)) {
            case AlumnoContract.ALLROWS:
                affected = db.delete(AlumnoContract.AlumnoEntry.TABLE_NAME,selection, selectionArgs);
                break;
            case AlumnoContract.SINGLE_ROW:
                String idActividad = uri.getPathSegments().get(1);
                affected = db.delete(AlumnoContract.AlumnoEntry.TABLE_NAME,
                        AlumnoContract.AlumnoEntry._ID + "=" + idActividad
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        //devuelve el número de filas afectadas
        return affected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = alumnosDbHelper.getWritableDatabase();
        int affected;
        switch (AlumnoContract.uriMatcher.match(uri)) {
            case AlumnoContract.ALLROWS:
                affected = db.update(AlumnoContract.AlumnoEntry.TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case AlumnoContract.SINGLE_ROW:
                String idActividad = uri.getPathSegments().get(1);
                affected = db.update(AlumnoContract.AlumnoEntry.TABLE_NAME, values,
                        AlumnoContract.AlumnoEntry._ID + "=" + idActividad
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("URI desconocida: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        //devuelve el número de filas afectadas
        return affected;
    }
}

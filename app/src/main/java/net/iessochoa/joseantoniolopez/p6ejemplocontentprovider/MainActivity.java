package net.iessochoa.joseantoniolopez.p6ejemplocontentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import net.iessochoa.joseantoniolopez.p6ejemplocontentprovider.model.Alumno;
import net.iessochoa.joseantoniolopez.p6ejemplocontentprovider.model.AlumnoContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class MainActivity extends AppCompatActivity {

    static public String TAG_ERROR = "P6EjemploContentProvider-Error:";
    @BindView(R.id.et_Nombre)
    EditText etNombre;
    @BindView(R.id.et_Grupo)
    EditText etGrupo;
    @BindView(R.id.et_FotoUri)
    EditText etFotoUri;
    @BindView(R.id.spn_Grupos)
    Spinner spnGrupos;
    ListaAlumnosFragment frgListaAlumnos;
    //nos permitirá no recargar el Loader dos veces por culpa del spinner
    boolean esPrimeraVez=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        iniciaDatosGrupoSpinner();
        frgListaAlumnos=(ListaAlumnosFragment) getSupportFragmentManager().findFragmentById(R.id.frgListaAlumnos);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        ocultarTeclado();
    }

    @OnClick({R.id.btn_add, R.id.btn_del, R.id.btn_update})
    public void onClick(View view) {
        ocultarTeclado();
        Alumno alumno = creaAlumno();
        ContentValues values;
        //obtenemos el contentResolver
        ContentResolver cr = getContentResolver();
        try {//comprobamos posibles errores en la base de datos
            switch (view.getId()) {
                case R.id.btn_add:
                    values = new ContentValues();
                    values.put(AlumnoContract.AlumnoEntry.NOMBRE, alumno.getNombre());
                    values.put(AlumnoContract.AlumnoEntry.GRUPO, alumno.getGrupo());
                    values.put(AlumnoContract.AlumnoEntry.FOTO_URI, alumno.getFotoUri());
                    cr.insert(AlumnoContract.CONTENT_URI, values);
                    break;
                case R.id.btn_del:
                    cr.delete(AlumnoContract.CONTENT_URI,
                            AlumnoContract.AlumnoEntry.NOMBRE + " =?",
                            new String[]{alumno.getNombre()});
                    break;
                case R.id.btn_update:
                    values = new ContentValues();
                    values.put(AlumnoContract.AlumnoEntry.NOMBRE, alumno.getNombre());
                    values.put(AlumnoContract.AlumnoEntry.GRUPO, alumno.getGrupo());
                    values.put(AlumnoContract.AlumnoEntry.FOTO_URI, alumno.getFotoUri());
                    cr.update(AlumnoContract.CONTENT_URI, values,
                            AlumnoContract.AlumnoEntry.NOMBRE + " =?",
                            new String[]{alumno.getNombre()});
                    break;
            }
            iniciaDatosGrupoSpinner();

        } catch (SQLiteException e) {
            mostrarMensajeError(e);
        }
    }

    /**
     * Crea un alumno con los datos de la Activity
     *
     * @return
     */
    private Alumno creaAlumno() {
        return (new Alumno(etNombre.getText().toString(), etGrupo.getText().toString(), etFotoUri.getText().toString()));
    }

    private void mostrarMensajeError(Exception e) {
        Log.e(TAG_ERROR, e.getMessage());
        Toast.makeText(this, "Problema con el content provider" + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * Permite ocultar el teclado
     */
    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(etNombre.getWindowToken(), 0);
        }
    }

    /**
     * Inicia el spinner con los grupos.
     * Vemos como podemos consultar a un Content Provider
     */
    private void iniciaDatosGrupoSpinner() {
        ContentResolver cr = getContentResolver();
        try {
            //realizamos la consulta
            Cursor cursor = cr.query(AlumnoContract.CONTENT_URI,
                    new String[]{"DISTINCT " + AlumnoContract.AlumnoEntry.GRUPO},//Columnas a devolver
                    null,//Condición de la query
                    null,//Argumentos variables de la query
                    null);//Orden de los resultados
            //buscamos los grupos en la base de datos

            ArrayList<String> al_Grupos = new ArrayList<String>();
            if (cursor.moveToFirst()) {
                do {
                    al_Grupos.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            //si el usuario selecciona Todos, mostraremos todos los alumnos
            al_Grupos.add(0, "-TODOS-");
            ArrayAdapter<String> adaptadorSpinner = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, al_Grupos);
            adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnGrupos.setAdapter(adaptadorSpinner);

        } catch (SQLiteException e) {
            mostrarMensajeError(e);
        }
    }
    /**
     * Cuando se selecciona un grupo del spinner, recuperamos los alumnos según el grupo
     * elegido. Si es "-TODOS-", mostramos todos
     */
    @OnItemSelected(R.id.spn_Grupos)
    public void onItemSelected(Spinner spinner, int position) {
        if(!esPrimeraVez) {
            String grupo = "";
            if (position != 0)//si no se ha seleccionado -TODOS-
                grupo = (String) spnGrupos.getSelectedItem();
            frgListaAlumnos.cambiaGrupo(grupo);
        }else
            esPrimeraVez=false;



    }
}

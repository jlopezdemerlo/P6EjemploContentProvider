package net.iessochoa.joseantoniolopez.p6ejemplocontentprovider;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import net.iessochoa.joseantoniolopez.p6ejemplocontentprovider.model.AlumnoContract;
import net.iessochoa.joseantoniolopez.p6ejemplocontentprovider.model.AlumnoProvider;

/**
 * Este fragment hereda de ListFragment que es un Fragment de sistema que contiene un ListView
 * Unicamente tendremos que implementar el adaptador.
 * Por otro lado utilizaremos Loader que manejarán en segundo plano la carga de datos
 * Podéis encontrar más información sobre Loader en
 * https://developer.android.com/guide/components/loaders.html?hl=es-419
 */

public class ListaAlumnosFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>

{
    private AlumnoAdapter adaptador;
    //nos permitirá seleccionar los alumnos del grupo
    private String grupo="";

    public ListaAlumnosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Iniciar adaptador
        adaptador = new AlumnoAdapter(getContext());
        // Relacionar adaptador a la lista
        setListAdapter(adaptador);
        // Iniciar Loader. El 0 es el identificador único del loader
        getLoaderManager().initLoader(0, null, this);
        //En caso de una reconstruccion por un giro de pantalla, retenemos los datos,
        //con la diferencia de que si no utilizaramos Loaders, el cursor se cierra y hay que volver a abrirlo
        //Mediante Loaders, se retiene el cursor y además el Loader trabaja en segundo plano,
        this.setRetainInstance(true);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //cerramos el Loader
        try {
            getLoaderManager().destroyLoader(0);
            if (adaptador != null) {
                adaptador.changeCursor(null);
                adaptador = null;
            }
        } catch (Throwable localThrowable) {
            // Proyectar la excepción
        }
    }

    //permite cargar el cursor
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] argumentos=null;
        String condicion=null;

        if(grupo!=""){//si el usuario selecciona un grupo desde el spinner de la actividad principal
            //mostraremos los alumnos de ese grupo
            condicion= AlumnoContract.AlumnoEntry.GRUPO+" =?";
            argumentos=new String[]{grupo};
        }

        return new CursorLoader(getActivity(),
                AlumnoContract.CONTENT_URI,
                null, condicion, argumentos, null);
    }

    /**
     * cuando termina de leer los datos el Loader, nos devuelve el cursor que nosotros se
     * lo pasaremos al adaptador para que realice su trabajo
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adaptador.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adaptador.swapCursor(null);
    }

    /**
     * Cambia el Loader cuando selecciona grupo en la
     * Actividad principal
     * @param gr grupo de alumnos
     */
    public void cambiaGrupo(String gr){
        grupo=gr;
        getLoaderManager().restartLoader(0, null, this);

    }

}
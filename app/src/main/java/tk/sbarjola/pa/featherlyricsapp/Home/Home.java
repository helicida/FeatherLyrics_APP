package tk.sbarjola.pa.featherlyricsapp.Home;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.widget.TextView;

import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.R;
import tk.sbarjola.pa.featherlyricsapp.provider.music.MusicColumns;

public class Home extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    MusicListAdapter myListAdapter;     // Adaptador para el listView
    ListView listView;                  // ListView

    public View onCreateView(LayoutInflater inflater, ViewGroup contenedor, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, contenedor, false);

        listView = (ListView) view.findViewById(R.id.home_listCanciones);

        // Creamos y definimos el ListAdapter
        myListAdapter = new MusicListAdapter(
                getContext(),
                R.layout.fragment_home,
                null,
                new String[]{MusicColumns.TITLE, MusicColumns.BAND},
                new int[]{R.id.music_list_adapter_songName, R.id.music_list_adapter_songName},
                0);

        listView.setAdapter(myListAdapter); //Acoplem el adaptador

        // Cargamos las peliculas y las mostramos
        showMovies();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //Listener para el list

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Extraemos el artista del listView
                TextView textViewAuxiliar = (TextView) view.findViewById(R.id.music_list_adapter_songName);
                String text = textViewAuxiliar.getText().toString();

                // Cortamos el nombre de la pista y el artista
                String artist = text.split("-")[0];
                String track = text.split("-")[1];

                // Y lo mandamos al fragment de canciones
                ((MainActivity) getActivity()).setSearchedArtist(artist);
                ((MainActivity) getActivity()).setSearchedTrack(track);
                ((MainActivity) getActivity()).abrirCanciones();
            }
        });

        getLoaderManager().initLoader(0, null, this); // Creamos un loaderManager

        return view;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void showMovies(){
        Cursor cursor = getContext().getContentResolver().query(
                MusicColumns.CONTENT_URI,
                null,
                null,
                null,
                "_id DESC"
        );
        myListAdapter.swapCursor(cursor);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.support.v4.content.CursorLoader(getContext(),
                MusicColumns.CONTENT_URI,
                null,
                null,
                null,
                "_id DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        myListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        myListAdapter.swapCursor(null);
    }
}
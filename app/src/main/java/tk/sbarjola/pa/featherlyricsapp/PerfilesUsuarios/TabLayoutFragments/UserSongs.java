package tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.TabLayoutFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


import tk.sbarjola.pa.featherlyricsapp.Firebase.FirebaseItem;
import tk.sbarjola.pa.featherlyricsapp.Firebase.FirebaseConfig;
import tk.sbarjola.pa.featherlyricsapp.Firebase.Usuario;
import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.Adapters.userSongsAdapter;
import tk.sbarjola.pa.featherlyricsapp.R;

/**
 * Created by 46465442z on 27/04/16.
 */
public class UserSongs extends Fragment {

    // Firebase
    FirebaseConfig config;                                      // Configuración de firebase
    private Firebase referenciaListaUsuarios;                   // Apunta a la lista de usuarios

    // Instancia del usuario que mostraremos y su UID
    Usuario userToShow;
    String userUID = "";
    ListView songList;

    // Adapter y diferentes contenedores para la lista de artistas
    private userSongsAdapter myListAdapter;
    List<String> listCollectionMusic = new ArrayList<String>();

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_user_songs, container, false);    //Definimos el fragment

        // Instancia de la configuración
        config = (FirebaseConfig) getActivity().getApplication();
        referenciaListaUsuarios = config.getReferenciaListaUsuarios();

        // Instancia de la configuración
        config = (FirebaseConfig) getActivity().getApplication();
        referenciaListaUsuarios = config.getReferenciaListaUsuarios();

        // Primero extraemos el del main activity
        userUID = ((MainActivity) getActivity()).getOpenedProfile();

        // Si no se ha buscado ninguno, el nuestro por defecto
        if(userUID.equals("no profile") || userUID.equals("") || userUID == null){
            userUID = config.getUserUID();
        }

        try{
            songList = (ListView) view.findViewById(R.id.user_songs_list);

            // Seccion del grid y los albumes
            myListAdapter = new userSongsAdapter(container.getContext(), 0, listCollectionMusic);  // Definimos nuestro adaptador
        }
        catch(RuntimeException e){}

        // Primero extraemos el del main activity
        userUID = ((MainActivity) getActivity()).getOpenedProfile();

        // Si no se ha buscado ninguno, el nuestro por defecto
        if(userUID.equals("no profile") || userUID.equals("") || userUID == null){
            userUID = config.getUserUID();
        }

        // Descargamos la lista de usuarios
        referenciaListaUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {

                    try {

                        Usuario usuario = userSnapshot.getValue(Usuario.class);

                        if (usuario.getUID().equals(userUID)) {

                            // Cuando encotremos el usuario anyadimos la infromación a la vista
                            userToShow = usuario;

                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(usuario.getNombre());

                            final Firebase referenciaMusicaContacto = new Firebase(config.getReferenciaListaUsuarios().toString() + "/" + usuario.getKey() + "/Canciones");

                            // Descargamos la lista de usuarios
                            referenciaMusicaContacto.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        FirebaseItem grupo = userSnapshot.getValue(FirebaseItem.class);
                                        listCollectionMusic.add(0, userSnapshot.getKey() + "-" + grupo.getItemUrl());
                                    }

                                        // Limpiamos los duplicados. Gracias al LinkedHashSet mantenemos el orden de los elementos
                                        Set<String> hs = new LinkedHashSet<>(listCollectionMusic);
                                        hs.addAll(listCollectionMusic);
                                        myListAdapter.clear();
                                        myListAdapter.addAll(hs);

                                        // Setteamos el adapter
                                        songList.setAdapter(myListAdapter);
                                        setListViewHeightBasedOnChildren(songList);
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {}
                            });
                        }
                    }
                    catch (NullPointerException e){}
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() { //Listener para el list

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Extraemos el artista del listView
                TextView textSong = (TextView) view.findViewById(R.id.album_adapter_tituloAlbum);
                TextView textGrupo = (TextView) view.findViewById(R.id.user_songs_band);

                // Y lo mandamos al fragment de canciones
                ((MainActivity) getActivity()).setSearchedArtist(textGrupo.getText().toString());
                ((MainActivity) getActivity()).setSearchedTrack(textSong.getText().toString());
                ((MainActivity) getActivity()).abrirCanciones();
            }
        });

        return view;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {

        // Calculamos caunto hay que desplegar el GridView para poder mostrarlo todo dentro del ScrollView

        try{

            int alturaTotal = 0;
            int items = myListAdapter.getCount();
            int filas = 0;

            View listItem = myListAdapter.getView(0, null, listView);
            listItem.measure(0, 0);
            alturaTotal = listItem.getMeasuredHeight();

            float x = 1;

            x = items;
            filas = (int) (x + 1);
            alturaTotal *= filas;

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = alturaTotal;
            listView.setLayoutParams(params);

        }
        catch (IndexOutOfBoundsException e){}
    }
}

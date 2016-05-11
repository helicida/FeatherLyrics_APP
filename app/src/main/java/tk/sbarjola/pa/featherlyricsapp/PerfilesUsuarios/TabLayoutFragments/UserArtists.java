package tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.TabLayoutFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
import tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.Adapters.userArtistsAdapter;
import tk.sbarjola.pa.featherlyricsapp.R;

/**
 * Created by 46465442z on 27/04/16.
 */
public class UserArtists extends Fragment {

    // Firebase
    FirebaseConfig config;                                      // Configuración de firebase
    private Firebase referenciaUsuario;                         // Apunta al usuario loggeado
    private Firebase referenciaListaUsuarios;                   // Apunta a la lista de usuarios


    // Instancia del usuario que mostraremos y su UID
    Usuario userToShow;
    String userUID = "";
    GridView historial;

    // Adapter y diferentes contenedores para la lista de artistas
    private userArtistsAdapter myGridAdapter;
    List<String> listCollectionArtist = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_user_artists, container, false);    //Definimos el fragment

        // Instancia de la configuración
        config = (FirebaseConfig) getActivity().getApplication();
        referenciaUsuario = config.getReferenciaUsuarioLogeado();
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

            historial = (GridView) view.findViewById(R.id.user_artists_grid);

            // Seccion del grid y los albumes
            myGridAdapter = new userArtistsAdapter(container.getContext(), 0, listCollectionArtist);  // Definimos nuestro adaptador

            // Primero extraemos el del main activity
            userUID = ((MainActivity) getActivity()).getOpenedProfile();

        }catch(RuntimeException e){}


        // Si no se ha buscado ninguno, el nuestro por defecto
        if(userUID.equals("no profile") || userUID.equals("") || userUID == null){
            userUID = config.getUserUID();
        }

         /*
            Comprobamos si queremos consultar los datos del mismo usuario o de otro diferente al tuyo
            Si es del usuario, utilizamos su referencia directamente ahorrando datos
         */
        if(!userUID.contains(config.getUserUID())) {

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

                                final Firebase referenciaMusicaContacto = new Firebase(config.getReferenciaListaUsuarios().toString() + "/" + usuario.getKey() + "/Artistas");

                                // Descargamos la lista de usuarios

                                referenciaMusicaContacto.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                            FirebaseItem grupo = userSnapshot.getValue(FirebaseItem.class);
                                            listCollectionArtist.add(userSnapshot.getKey() + "-" + grupo.getItemUrl());
                                        }

                                        try {
                                            // Limpiamos los duplicados. Gracias al LinkedHashSet mantenemos el orden de los elementos
                                            Set<String> hs = new LinkedHashSet<>(listCollectionArtist);
                                            hs.addAll(listCollectionArtist);
                                            myGridAdapter.clear();
                                            myGridAdapter.addAll(hs);

                                            // Setteamos el adapter
                                            historial.setAdapter(myGridAdapter);

                                            setGridViewHeightBasedOnChildren(historial, 2);
                                        } catch (NullPointerException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                    }
                                });
                            }
                        } catch (NullPointerException e) {
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
        }
        else{

            // Descargamos la lista de usuarios
            referenciaUsuario.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {

                        Usuario usuario = dataSnapshot.getValue(Usuario.class);

                        if (usuario.getUID().equals(userUID)) {

                            // Cuando encotremos el usuario anyadimos la infromación a la vista
                            userToShow = usuario;

                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(usuario.getNombre());

                            final Firebase referenciaMusicaContacto = new Firebase(config.getReferenciaListaUsuarios().toString() + "/" + usuario.getKey() + "/Artistas");

                            // Descargamos la lista de usuarios

                            referenciaMusicaContacto.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        FirebaseItem grupo = userSnapshot.getValue(FirebaseItem.class);
                                        listCollectionArtist.add(userSnapshot.getKey() + "-" + grupo.getItemUrl());
                                    }

                                    try {
                                        // Limpiamos los duplicados. Gracias al LinkedHashSet mantenemos el orden de los elementos
                                        Set<String> hs = new LinkedHashSet<>(listCollectionArtist);
                                        hs.addAll(listCollectionArtist);
                                        myGridAdapter.clear();
                                        myGridAdapter.addAll(hs);

                                        // Setteamos el adapter
                                        historial.setAdapter(myGridAdapter);

                                        setGridViewHeightBasedOnChildren(historial, 2);

                                    } catch (NullPointerException e) {}
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {}
                            });
                        }
                    } catch (NullPointerException e) {}
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });
        }

        historial.setOnItemClickListener(new AdapterView.OnItemClickListener() { //Listener para el list

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Extraemos el artista del listView
                TextView textViewAuxiliar = (TextView) view.findViewById(R.id.user_artists_artistName);
                String text = textViewAuxiliar.getText().toString();

                // Cortamos el nombre de la pista y el artista
                String artist = text.split("-")[0];

                // Y lo mandamos al fragment de canciones
                ((MainActivity) getActivity()).setDiscographyStart(artist);
                ((MainActivity) getActivity()).abrirArtistas();

            }
        });

        return view;
    }

    public void setGridViewHeightBasedOnChildren(GridView gridView, int columnas) {

        // Calculamos caunto hay que desplegar el GridView para poder mostrarlo todo dentro del ScrollView

        try {

            int alturaTotal = 0;
            int items = myGridAdapter.getCount();
            int filas = 0;

            View listItem = myGridAdapter.getView(0, null, gridView);
            listItem.measure(0, 0);
            alturaTotal = listItem.getMeasuredHeight();

            float x = 1;

            if (items > columnas) {
                x = items / columnas;
                filas = (int) (x + 2);
                alturaTotal *= filas;
            }

            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.height = alturaTotal;
            gridView.setLayoutParams(params);

        } catch (IndexOutOfBoundsException e){}
    }
}

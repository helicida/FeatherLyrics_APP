package tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.TabLayoutFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


import tk.sbarjola.pa.featherlyricsapp.Firebase.FirebaseConfig;
import tk.sbarjola.pa.featherlyricsapp.Firebase.Usuario;
import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.R;

/**
 * Created by 46465442z on 27/04/16.
 */
public class UserProfile extends Fragment {

    // Firebase
    FirebaseConfig config;                                      // Configuración de firebase
    private Firebase referenciaListaUsuarios;                   // Apunta a la lista de usuarios

    // Instancia del usuario que mostraremos y su UID
    Usuario userToShow;
    String userUID = "";

    // Vistas en las que mostraremo la información
    ImageView imageUser;
    TextView userName;
    TextView userDescription;

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);    //Definimos el fragment

        // Instancia de la configuración
        config = (FirebaseConfig) getActivity().getApplication();
        referenciaListaUsuarios = config.getReferenciaListaUsuarios();

        // Referencia a las vistas
        imageUser = (ImageView) view.findViewById(R.id.userProfile_userPicture);
        userName = (TextView) view.findViewById(R.id.userProfile_userName);
        userDescription = (TextView) view.findViewById(R.id.userProfile_userInfo);

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
                            userName.setText(usuario.getNombre() + " - (" + usuario.getEdad() + ")");
                            userDescription.setText(usuario.getDescripcion());

                            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(usuario.getNombre());

                        }
                    }catch (NullPointerException e){}
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        return view;
    }
}

package tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.TabLayoutFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.HashMap;
import java.util.Map;

import tk.sbarjola.pa.featherlyricsapp.Firebase.FirebaseConfig;
import tk.sbarjola.pa.featherlyricsapp.Firebase.Usuario;
import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.R;

/**
 * Created by 46465442z on 10/05/16.
 */
public class EditProfile extends Fragment {

    // Firebase
    FirebaseConfig config;                                      // Configuración de firebase
    private Firebase referenciaUser;                            // Apunta al usuario

    // Vistas en las que mostraremo la información
    ImageView imageUser;
    EditText userName;
    EditText userDescription;
    EditText edad;
    EditText urlImagen;
    Button guardar;

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);    //Definimos el fragment

        // Instancia de la configuración
        config = (FirebaseConfig) getActivity().getApplication();
        referenciaUser = config.getReferenciaUsuarioLogeado();

        // Referencia a las vistas
        imageUser = (ImageView) view.findViewById(R.id.editProfile_profilePic);
        userName = (EditText) view.findViewById(R.id.editProfile_nombreUser);
        userDescription = (EditText) view.findViewById(R.id.editProfile_SobreMi);
        edad = (EditText) view.findViewById(R.id.editProfile_edadUser);
        urlImagen = (EditText) view.findViewById(R.id.editProfile_urlImagen);
        guardar = (Button) view.findViewById(R.id.editprofile_guardarCambios);

        // Descargamos la lista de usuarios
        referenciaUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    Usuario usuario = dataSnapshot.getValue(Usuario.class);

                    userName.setText(usuario.getNombre());
                    userDescription.setText(usuario.getDescripcion());
                    edad.setText(usuario.getEdad());
                    urlImagen.setText(usuario.getRutaImagen());

                    if (urlImagen.getText() != null && urlImagen.getText().length() > 5) {

                        // Le damos la imagen de album transformada en redonda
                        final Transformation transformation = new RoundedTransformationBuilder()
                                .cornerRadiusDp(360)
                                .oval(false)
                                .build();

                        Picasso.with(getContext()).load(urlImagen.getText().toString()).fit().centerCrop().transform(transformation).into(imageUser);
                    }
                } catch (NullPointerException e) {}
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        // On click para editar el perfil
        guardar.setOnClickListener(new View.OnClickListener() {
            //@Override
            public void onClick(View v) {

                // Update de la rutaImagen

                Map<String, Object> rutaImagen = new HashMap<>();
                rutaImagen.put("rutaImagen", urlImagen.getText().toString());
                referenciaUser.updateChildren(rutaImagen);

                // Update del nombre

                Map<String, Object> nombre = new HashMap<>();
                rutaImagen.put("nombre", userName.getText().toString());
                referenciaUser.updateChildren(nombre);

                // Update de la edad

                Map<String, Object> mapEdad = new HashMap<>();
                rutaImagen.put("edad", edad.getText().toString());
                referenciaUser.updateChildren(mapEdad);

                // Update de la descripcion

                Map<String, Object> descripcion = new HashMap<>();
                rutaImagen.put("descripcion", userDescription.getText().toString());
                referenciaUser.updateChildren(descripcion);

                Toast.makeText(getContext(), "Changes saved sucessfully", Toast.LENGTH_SHORT).show(); // Y lanzamos la toast

            }
        });

        return view;
    }
}

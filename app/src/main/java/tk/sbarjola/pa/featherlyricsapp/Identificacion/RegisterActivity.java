package tk.sbarjola.pa.featherlyricsapp.Identificacion;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.squareup.picasso.Picasso;

import java.io.File;

import tk.sbarjola.pa.featherlyricsapp.Firebase.FirebaseConfig;
import tk.sbarjola.pa.featherlyricsapp.Firebase.Usuario;
import tk.sbarjola.pa.featherlyricsapp.R;

public class RegisterActivity extends AppCompatActivity implements LocationListener {

    // IMPORTANTE!
    // A la hora de usar Firebase para regisrar usuarios utilizando este metodo, es necesario activar
    // esta funcionalidad dentro de nuestro proyecto Firebase:
    // -> Enable Email & Password Authentication

    // Referencias a Firebase
    FirebaseConfig config;
    private Firebase mainReference;              // Apunta a la raiz de firebase
    private Firebase referenciaListaUsuarios;    // Apunta a la lista de usuarios

    //EditText
    EditText registerEmail;     // EditText que contiene el email
    EditText registerPassword;  // EditText que contiene la contrasenya
    EditText about;             // Descripcion del usuario
    EditText nombre;            // Nombre del usuario
    EditText edad;              // Edad del usuario
    Button buttonRegister;      // Boton para registarse en la aplicación
    TextView info;              // Info del intento de register

    // Localización del usuario
    private ProgressDialog dialog;      // Pantalla de localización
    Location localizacion = null;       // Localización
    private ImageView imagenUsuario;    // Foto a enviar
    boolean imagen = false;             // Booleano que determina si el mensaje tiene imagen o no

    public void onStart() {

        super.onStart();

        // Cuando inicia el fragmento comprobamos si se ha sacado una foto en caso positivo, la acoplamos a nuestra imagen
        if (imagen) {
            File imagePath = new File(getRutaImagen());
            Picasso.with(this).load(imagePath).centerCrop().resize(240, 300).into(imagenUsuario);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Feather Lyrics - Registarse");

        // Mostramos la flecha para retroceder de activity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Damos valor a nuestras variables de firebase
        config = (FirebaseConfig) getApplication();
        mainReference = config.getMainReference();
        referenciaListaUsuarios = config.getReferenciaListaUsuarios();

        // Referncias al layout
        registerEmail = (EditText) this.findViewById(R.id.register_TextEmail);
        registerPassword = (EditText) this.findViewById(R.id.register_TextPassword);
        about = (EditText) this.findViewById(R.id.register_SobreMi);
        nombre = (EditText) this.findViewById(R.id.register_nombreUser);
        edad = (EditText) this.findViewById(R.id.register_edadUser);
        buttonRegister = (Button) this.findViewById(R.id.register_ButtonRegister);
        info = (TextView) this.findViewById(R.id.register_info);
        imagenUsuario = (ImageView) this.findViewById(R.id.register_profilePic);

        dialog = ProgressDialog.show(this, "", "Localizando tu posición..."); // Dialog que mostrará localizando

        // Localizacion
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, this);

        // Le damos un listener, que abrirá la camara al darl encima
        imagenUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagen = true;
                Intent camara = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
                startActivity(camara);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Extraemos los datos de los editText
                final String email = registerEmail.getText().toString();
                final String password = registerPassword.getText().toString();

                // Creamos un nuevo usuario y lo subimos. Mostarmos el resultado en nuestro TextView.
                mainReference.createUser(email, password, new Firebase.ResultHandler() {
                    @Override
                    public void onSuccess() {

                        info.setVisibility(View.VISIBLE);
                        Toast.makeText(getBaseContext(), "Se ha creado el nuevo usuario correctamente", Toast.LENGTH_SHORT).show(); // Y lanzamos la toast

                        // Comenzamos la identificación de nuestro usuario con el correo y password
                        mainReference.authWithPassword(email, password, new Firebase.AuthResultHandler() {

                            @Override
                            public void onAuthenticated(AuthData authData) {

                                Firebase nuevoUsuario = referenciaListaUsuarios.push();
                                Usuario usuario = new Usuario();
                                usuario.setKey(nuevoUsuario.getKey());
                                usuario.setEmail(email);
                                usuario.setPassword(password);
                                usuario.setUID(authData.getUid());
                                usuario.setDescripcion(about.getText().toString());
                                usuario.setNombre(nombre.getText().toString());
                                usuario.setEdad(edad.getText().toString());
                                usuario.setLatitud(localizacion.getLatitude());
                                usuario.setLongitud(localizacion.getLongitude());

                                // Si tiene imagen le damos la ruta de la imagen
                                if (imagen) {
                                    usuario.setRutaImagen(getRutaImagen().toString());
                                }

                                nuevoUsuario.setValue(usuario);
                                config.setReferenciaUsuarioLogeado(referenciaListaUsuarios.child(nuevoUsuario.getKey()));
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                info.setVisibility(View.VISIBLE);
                                info.setText("Error: Error al guardar algunos datos del usuario :(");
                            }
                        });

                        finish();   // Volvemos al login
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        info.setVisibility(View.VISIBLE);
                        info.setText("Error " + firebaseError.toString().split(":")[1]);
                    }
                });

            }
        });
    }

    public String getRutaImagen() {
        // Con este código obtenemos la ruta de la imagen
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToLast();
        return cursor.getString(column_index_data);
    }

    // Metodos locationListener
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onLocationChanged(Location location) {
        localizacion = location;
        dialog.dismiss();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }

}

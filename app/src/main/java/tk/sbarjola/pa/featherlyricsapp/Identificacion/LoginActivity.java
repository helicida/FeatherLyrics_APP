package tk.sbarjola.pa.featherlyricsapp.Identificacion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.florent37.materialtextfield.MaterialTextField;

import java.util.ArrayList;

import tk.sbarjola.pa.featherlyricsapp.Firebase.FirebaseConfig;
import tk.sbarjola.pa.featherlyricsapp.Firebase.Usuario;
import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.R;

public class LoginActivity extends AppCompatActivity {

    // Referencias a Firebase
    FirebaseConfig config;                       // Clase que configura firebase
    private Firebase mainReference;              // Apunta a la raiz de firebase
    private Firebase referenciaListaUsuarios;    // Apunta a la lista de usuarios

    //EditText
    EditText loginEmail;        // EditText que contiene el email
    EditText loginPassword;     // EditText que contiene la contrasenya
    Button buttonLogin;         // Boton para loggearse
    Button buttonRegister;      // Boton para registarse en la aplicación
    TextView info;              // Info del intento de login
    MaterialTextField pass;     // MaterialField de la contraseña
    MaterialTextField user;     // MaterialField del usuario

    // ArrayList con informacion de los usuarios
    ArrayList<Usuario> listaUsuarios = new ArrayList<>();

    // Booleano auxiliar para el login
    boolean usuarioEncontrado = false;

    // Preferencias
    SharedPreferences preferencias;                     // Preferencias personalizadas
    Switch recordarCuenta;                              // Switch que determina si guardamos las credenciales de la cuenta o no
    SharedPreferences.Editor sharedPreferencesEditor;   // SharedPreferenceEditor que usaremos para modificar las settings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ajustamos la tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Feather Lyrics - Login");

        toolbar.setVisibility(View.GONE);

        // Damos valor a nuestras variables de firebase
        config = (FirebaseConfig) getApplication();
        mainReference = config.getMainReference();
        referenciaListaUsuarios = config.getReferenciaListaUsuarios();

        // Tambien a las preferencias
        preferencias = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);

        // Referncias al layout
        loginEmail = (EditText) this.findViewById(R.id.login_TextEmail);
        loginPassword = (EditText) this.findViewById(R.id.login_TextPassword);
        buttonLogin = (Button) this.findViewById(R.id.login_ButtonLogin);
        buttonRegister = (Button) this.findViewById(R.id.login_ButtonRegister);
        info = (TextView) this.findViewById(R.id.login_info);
        recordarCuenta = (Switch) this.findViewById(R.id.login_saveAcc);
        pass = (MaterialTextField) this.findViewById(R.id.passwordWrapper);
        user = (MaterialTextField) this.findViewById(R.id.usernameWrapper);

        // Desplegamos los campos del login
        pass.expand();
        user.expand();

        pass.setClickable(false);
        user.setClickable(false);

        boolean autologin = preferencias.getBoolean("autologin", false);

        // Autologin

        if (autologin) {
            // Hacemos el intent
            Intent mainClass = new Intent().setClass(LoginActivity.this, MainActivity.class);
            startActivity(mainClass);

            finish();
        }

        // On click para el botón de login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // On click para el botón de regisrarse
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the next activity
                Intent registerIntent = new Intent().setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        // Descargamos la lista de usuarios
        referenciaListaUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaUsuarios.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Usuario usuario = userSnapshot.getValue(Usuario.class);
                    listaUsuarios.add(usuario);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void login() {

        // Función que lleva acabo el proceso de autentificación e identificación del usuario en Firebase

        // Extraemos los datos de los editText
        final String email = loginEmail.getText().toString();
        final String password = loginPassword.getText().toString();

        // Comenzamos la identificación de nuestro usuario con el correo y password
        mainReference.authWithPassword(email, password, new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {

                // Switch
                if (recordarCuenta.isChecked()) {
                    sharedPreferencesEditor = preferencias.edit();
                    sharedPreferencesEditor.putBoolean("autologin", true);
                    sharedPreferencesEditor.commit();
                } else {
                    sharedPreferencesEditor = preferencias.edit();
                    sharedPreferencesEditor.putBoolean("autologin", false);
                    sharedPreferencesEditor.commit();
                }

                // Mostramos información del proceso de login
                info.setText("Iniciando sesison...");
                info.setVisibility(View.VISIBLE);

                // Breve explicación:
                /*
                A parte de registrar los usuarios en el log de autentificación de Firebsae
                es necesario crear una referencia en el arbol para poder vincularles información
                por lo tanto, al logear necesitamos saber si esa referencia existe
                Lo hacemos con dos arrayLists
                 */

                for (int iterador = 0; iterador < listaUsuarios.size(); iterador++) {

                    // Comprobamos si el usuario existe
                    if (listaUsuarios.get(iterador).getUID().equals(authData.getUid()))  {
                        config.setReferenciaUsuarioLogeado(referenciaListaUsuarios.child(listaUsuarios.get(iterador).getKey()));
                        config.setUserUID(authData.getUid());

                        // Guardamos la referencia del usuario logeado por si se activa el autologin
                        sharedPreferencesEditor = preferencias.edit();
                        sharedPreferencesEditor.putString("UID", authData.getUid());
                        sharedPreferencesEditor.commit();

                        usuarioEncontrado = true;
                        break;
                    }
                    else {
                        usuarioEncontrado = false;
                    }
                }

                if (!usuarioEncontrado) {

                    Firebase nuevoUsuario = referenciaListaUsuarios.push();
                    Usuario usuario = new Usuario();
                    usuario.setKey(nuevoUsuario.getKey());
                    usuario.setUID(authData.getUid());
                    nuevoUsuario.setValue(usuario);
                    config.setReferenciaUsuarioLogeado(referenciaListaUsuarios.child(nuevoUsuario.getKey()));
                }

                // Guardamos la referencia del usuario logeado por si se activa el autologin
                sharedPreferencesEditor = preferencias.edit();
                sharedPreferencesEditor.putString("refUsuario", config.getReferenciaUsuarioLogeado().toString());
                sharedPreferencesEditor.commit();

                // Hacemos el intent
                Intent registerLogin = new Intent().setClass(LoginActivity.this, MainActivity.class);
                startActivity(registerLogin);

                // Terminamos la activity para que no podamos volver pusando el botón de atrás
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                info.setVisibility(View.VISIBLE);
                info.setText("Error: " + firebaseError.toString().split(":")[1]);

                // Autologin a falso, por si acaso
                sharedPreferencesEditor = preferencias.edit();
                sharedPreferencesEditor.putBoolean("autoLogin", false);
                sharedPreferencesEditor.commit();
            }
        });
    }
}

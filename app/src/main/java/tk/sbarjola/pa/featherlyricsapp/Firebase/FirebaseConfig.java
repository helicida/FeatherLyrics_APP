package tk.sbarjola.pa.featherlyricsapp.Firebase;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.firebase.client.Firebase;

import tk.sbarjola.pa.featherlyricsapp.MainActivity;

/**
 * Created by sergi on 20/03/16.
 */

public class FirebaseConfig extends Application {

    // Clase que configura Firebsae

    // Referencias firebase
    private Firebase mainReference;              // Apunta a la raiz de firebase
    private Firebase referenciaListaUsuarios;    // Apunta a la lista de usuarios
    private Firebase referenciaUsuarioLogeado;   // Apunta al usuario loggeado

    // Preferencias
    SharedPreferences preferencias;     // Preferencias personalizadas

    @Override
    public void onCreate() {
        super.onCreate();

        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        preferencias = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);

        // Referncias de firebase
        mainReference = new Firebase("https://feathermusic.firebaseio.com/");
        referenciaListaUsuarios = new Firebase("https://feathermusic.firebaseio.com/Usuarios");
        referenciaUsuarioLogeado  = new Firebase("https://feathermusic.firebaseio.com/Usuarios");

        boolean autologin = preferencias.getBoolean("autologin", true);

        String refUsuarioSp = preferencias.getString("refUsuario", null);

        // Autologin

        if (autologin && refUsuarioSp != null) {
            setReferenciaUsuarioLogeado(new Firebase(refUsuarioSp));
        }
    }

    // Getters

    public Firebase getMainReference() {
        return mainReference;
    }

    public Firebase getReferenciaListaUsuarios() {
        return referenciaListaUsuarios;
    }

    public Firebase getReferenciaUsuarioLogeado() {
        return referenciaUsuarioLogeado;
    }

    // Setters

    public void setMainReference(Firebase mainReference) {
        this.mainReference = mainReference;
    }

    public void setReferenciaListaUsuarios(Firebase referenciaListaUsuarios) {
        this.referenciaListaUsuarios = referenciaListaUsuarios;
    }

    public void setReferenciaUsuarioLogeado(Firebase referenciaUsuarioLogeado) {
        this.referenciaUsuarioLogeado = referenciaUsuarioLogeado;
    }
}

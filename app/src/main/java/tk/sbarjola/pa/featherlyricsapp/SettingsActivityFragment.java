package tk.sbarjola.pa.featherlyricsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import tk.sbarjola.pa.featherlyricsapp.provider.music.MusicColumns;

/**
 * Created by sergi on 27/12/15.
 */
public class SettingsActivityFragment extends PreferenceFragment {

    SharedPreferences myPreferences;    // SharedPreference personalizado
    Button buttonBBDD;                  // Button con el que limpiamos la BBDD
    Button buttonTono;                  // Button con el que cambiamos el tono de la APP
    Switch toastSwitch;                 // Switch que gestiona el TOAST de las canciones
    Switch splashSwitch;                // Siwtch que gestiona el splash screen
    Switch soundSwitch;                 // Switch que gestiona el tono de la aplicación

    SharedPreferences.Editor sharedPreferencesEditor;   // SharedPreferenceEditor que usaremos para modificar las settings

    public SettingsActivityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View settingsActivity = inflater.inflate(R.layout.fragment_settings, container, false);

        myPreferences = getActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE);

        // Declaramos el button y los swichs

        buttonTono = (Button) settingsActivity.findViewById(R.id.settings_cambiarTono);
        buttonBBDD = (Button) settingsActivity.findViewById(R.id.settings_buttonBBDD);
        toastSwitch = (Switch) settingsActivity.findViewById(R.id.settings_mostrarToast);
        splashSwitch = (Switch) settingsActivity.findViewById(R.id.settings_mostarSplashScreen);
        soundSwitch = (Switch) settingsActivity.findViewById(R.id.settings_sound);

        // Establecemos por la opción predefinida

        toastSwitch.setChecked(myPreferences.getBoolean("toastNotificacion", true));
        splashSwitch.setChecked(myPreferences.getBoolean("splash", true));
        soundSwitch.setChecked(myPreferences.getBoolean("sound", true));

        // Listeners para los switch y el button

        toastSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    sharedPreferencesEditor = myPreferences.edit();
                    sharedPreferencesEditor.putBoolean("toastNotificacion", true);
                    sharedPreferencesEditor.commit();
                } else {
                    sharedPreferencesEditor = myPreferences.edit();
                    sharedPreferencesEditor.putBoolean("toastNotificacion", false);
                    sharedPreferencesEditor.commit();
                }
            }
        });

        splashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    sharedPreferencesEditor = myPreferences.edit();
                    sharedPreferencesEditor.putBoolean("splash", true);
                    sharedPreferencesEditor.commit();
                } else {
                    sharedPreferencesEditor = myPreferences.edit();
                    sharedPreferencesEditor.putBoolean("splash", false);
                    sharedPreferencesEditor.commit();
                }
            }
        });

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    sharedPreferencesEditor = myPreferences.edit();
                    sharedPreferencesEditor.putBoolean("sound", true);
                    sharedPreferencesEditor.commit();
                } else {
                    sharedPreferencesEditor = myPreferences.edit();
                    sharedPreferencesEditor.putBoolean("sound", false);
                    sharedPreferencesEditor.commit();
                }
            }
        });

        buttonBBDD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getBaseContext().getContentResolver().delete(
                        MusicColumns.CONTENT_URI,
                        null,
                        null);

                Toast.makeText(getActivity().getBaseContext(), R.string.toast_bbdd_settings, Toast.LENGTH_SHORT).show(); // Mostramos un toast
            }
        });

        buttonTono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRecorder = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(openRecorder, 1);

                sharedPreferencesEditor = myPreferences.edit();
                sharedPreferencesEditor.putBoolean("nuevoTono", true);
                sharedPreferencesEditor.putString("rutaTono", getRutaAudio());
                sharedPreferencesEditor.commit();
            }
        });

        return settingsActivity;
    }

    public String getRutaAudio() {
        // Con este código obtenemos la ruta del ultimo archivo de musica modificado
        String[] projection = { MediaStore.Audio.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToLast();
        return cursor.getString(column_index_data);
    }
}
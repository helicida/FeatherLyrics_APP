package tk.sbarjola.pa.featherlyricsapp.MusicReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.provider.music.MusicColumns;
import tk.sbarjola.pa.featherlyricsapp.provider.music.MusicContentValues;

/**
 * Created by 46465442z on 14/01/16.
 */
public class MusicBroadcastReceiver extends BroadcastReceiver {

    public static String playingArtist = "no artist";   // Nombre artista de la pista en reproduccion
    public static String playingTrack = "no track";     // Titulo de la pista en reproduccion
    static MainActivity mainVar = null;                 // Esta será la referencia a la clase del MainActivity

    public MusicBroadcastReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {

    /* Este if auxiliar es necesario debido a que ciertos dispositivos reciben dos intents al BroadcastReceiver
    en el mismo BroadcastReceiver, duplicando la informacion.
    Usando un condicional solventamos el problema*/

        if(!playingTrack.equals(intent.getStringExtra("track"))) {

            playingArtist = intent.getStringExtra("artist");    // Sacamos el artista del intent
            playingTrack = intent.getStringExtra("track");      // sacamos la pista

            // Guardamos la informacion en nuestra base de datos

            MusicContentValues values = new MusicContentValues();
            values.putTitle(playingTrack);
            values.putBand(playingArtist);

            context.getContentResolver().insert(MusicColumns.CONTENT_URI, values.values());

            // Preferencias personalizadas
            SharedPreferences preferencias = context.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);

            if (preferencias.getBoolean("toastNotificacion", true)) {
                Toast.makeText(context, playingTrack + " - " + playingArtist, Toast.LENGTH_SHORT).show(); // Y lanzamos la toast
            }

        /* Si está vinculado al MainActivity, es decir, si la APP se está ejecutando
         le dará un aviso de que ha habido un cambio de canción */

            if (mainVar != null) {
                mainVar.extraerInfoMusica();
                mainVar.actualizarMusica();
            }
        }
    }

    public static void setMainActivityHandler(MainActivity main){
        mainVar = main;
    }

    public static String getPlayingArtist() {
        return playingArtist;
    }

    public static String getPlayingTrack() {
        return playingTrack;
    }
}
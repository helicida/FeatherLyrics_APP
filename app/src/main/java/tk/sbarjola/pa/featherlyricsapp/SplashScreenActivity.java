package tk.sbarjola.pa.featherlyricsapp;

/**
 * Created by sergi on 21/12/15.
 */
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import tk.sbarjola.pa.featherlyricsapp.Identificacion.LoginActivity;

public class SplashScreenActivity extends Activity {

    // Duración del SplashScreen
    private static final long SPLASH_SCREEN_DELAY = 2000;
    SharedPreferences preferencias; // Preferencias personalizadas

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        preferencias = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);

        boolean sonido = preferencias.getBoolean("sound", true);
        boolean nuevoTono = preferencias.getBoolean("nuevoTono", false);

        if (sonido == true) {

            if(nuevoTono == true){

                try {
                    MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(preferencias.getString("rutaTono", null));
                    mp.prepare();
                    mp.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else{
                MediaPlayer mp = MediaPlayer.create(this, R.raw.intro);
                mp.start();
            }
        }

        if (preferencias.getBoolean("splash", true)) {

            // Obligamos a que la pantalla esté en portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            // Ocultamos la titlebar
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            setContentView(R.layout.splash_screen);

            // Referenciamos nuestra imagen
            ImageView icon = (ImageView) findViewById(R.id.splashscreen_splashIcon);

            Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);    // Creamos la nueva animación haciendo referencia al xml
            icon.startAnimation(myFadeInAnimation); // Y se la damos a nuestra imagen

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    startMain();
                }
            };
            // Simulate a long loading process on application startup.
            Timer timer = new Timer();
            timer.schedule(task, SPLASH_SCREEN_DELAY);
        }
        else {
            startMain();
        }
    }

    public void startMain(){
        // Start the next activity
        Intent mainIntent = new Intent().setClass(SplashScreenActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
package tk.sbarjola.pa.featherlyricsapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import tk.sbarjola.pa.featherlyricsapp.Canciones.Canciones;
import tk.sbarjola.pa.featherlyricsapp.Discografia.Discografia;
import tk.sbarjola.pa.featherlyricsapp.Home.Home;
import tk.sbarjola.pa.featherlyricsapp.Mapa.OSMap;
import tk.sbarjola.pa.featherlyricsapp.Noticias.Noticias;
import tk.sbarjola.pa.featherlyricsapp.MusicReceiver.MusicBroadcastReceiver;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;                // Drawer
    private NavigationView navigationView;      // NavigationView
    Fragment fragment = null;                   // fragmento que ocupara el centro de nuestro navigation drawer

    String discographyStart = "no artist";      // Nombre del artista que mostraremos en la sección de discografia
    String playingArtist = "no artist";         // Nombre del artista de la canción en reproducción
    String playingTrack = "no track";           // Nombre de la pista en reproducción
    String searchedArtist = "no artist";        // Nombre del artista seleccionado en discografia
    String searchedTrack = "no track";          // Nombre de la pista seleccionada en discografia

    SharedPreferences preferencias;                    // Preferencias personalizadas
    SharedPreferences.Editor sharedPreferencesEditor;  // SharedPreferenceEditor que usaremos para modificar las settings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Comprobamos si se ha ejecutado a través del widget
        Bundle extras = getIntent().getExtras();
        String alertaWidget = "aa";

        if (extras != null) {
            alertaWidget = extras.getString("alertaWidget");
        }

        // Ajustamos la tooblar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Ajustamos el drawer de nuestro activity
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Ajustamos el navigationView
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Hacemos que autmaticamente arranque en el fragmento "Home"
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new Home(), "home")
                .commit();

        navigationView.getMenu().getItem(0).setChecked(true);   // Marcamos el menu del navigation Drawer

        preferencias = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);

        // Con este BroadcastReceiver escucharemos a nuestro Receiver que controla la Musica

        BroadcastReceiver broadcastReceiver = null;
        broadcastReceiver = new MusicBroadcastReceiver();
        MusicBroadcastReceiver.setMainActivityHandler(this);    // Le pasamos este activity para vincularlos
        IntentFilter callInterceptorIntentFilter = new IntentFilter("android.intent.action.ANY_ACTION");
        registerReceiver(broadcastReceiver, callInterceptorIntentFilter);

        extraerInfoMusica();    // Extraemos la musica del BroadcastReceiver para saber que hay en reproducción

        /* Si se ha ejecutado a través del widget, abrimos la sección de canciones
        para mostar la canción actual en reproducción */

        if(alertaWidget.equals("widget")){
            abrirCanciones();
        }
    }

    public void extraerInfoMusica(){

        playingArtist = MusicBroadcastReceiver.getPlayingArtist();    // Sacamos el artista del intent
        playingTrack = MusicBroadcastReceiver.getPlayingTrack();      // sacamos la pista

        // Si hay algo reproduciendose mostramos la snackbar
        if(playingArtist != "no artist"){
            Snackbar.make(findViewById(R.id.content_frame), playingTrack + " - " + playingArtist, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    public void actualizarMusica(){

        // Con esta función actualizamos la letra de la cancion en reproduccion el el fragment canciones

        Canciones canciones = (Canciones) getSupportFragmentManager().findFragmentByTag("canciones");

        if (canciones != null) {
            canciones.setSong(playingArtist, playingTrack);
        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    public void abrirCanciones() {

        // Función para llamar al fragment de canciones

        fragment = new Canciones();
        String tag = "canciones";

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment, tag)
                .commit();

        navigationView.getMenu().getItem(1).setChecked(true);   // Marcamos el menu del navigation Drawer

        getSupportActionBar().setTitle("Canciones");    // Cambiamos el titulo del ActionBar
    }

    public void abrirDiscografia() {

        // Función para llamar al fragment de canciones

        fragment = new Discografia();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        navigationView.getMenu().getItem(2).setChecked(true);   // Marcamos el menu del navigation Drawer

        getSupportActionBar().setTitle("Discografia");    // Cambiamos el titulo del ActionBar
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        boolean transaccion = false; // Cuadno el boleano sea tre, se cambiará a otro fragment

        String tag = null;

        // Asignamos las acciones a cada menuItem del drawer
        if (id == R.id.nav_home) {
            fragment = new Home();
            transaccion = true;
        }else if (id == R.id.nav_canciones) {
            fragment = new Canciones();
            transaccion = true;
            tag = "canciones";
        }else if (id == R.id.nav_discografia) {
            fragment = new Discografia();
            transaccion = true;
        }else if (id == R.id.nav_descubrir) {
            fragment = new OSMap();
            transaccion = true;
        } else if (id == R.id.nav_noticias) {
            fragment = new Noticias();
            transaccion = true;
        } else if (id == R.id.nav_preferencias) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_bio){
            showAbout();
        } else if (id == R.id.nav_salir) {

            // Borramos la cuenta
            sharedPreferencesEditor = preferencias.edit();
            sharedPreferencesEditor.putBoolean("autologin", false);
            sharedPreferencesEditor.commit();

            finish();   // Volvemos al login
        }

        // Si el boleano es true llamamos al nuevo fragment
        if(transaccion){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment, tag)
                    .commit();

            item.setChecked(true);  // Y lo ponemos como marcado.
            getSupportActionBar().setTitle(item.getTitle());
        }

        drawer.closeDrawers();
        return true;
    }

    protected void showAbout() {
        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        TextView textView = (TextView) messageView.findViewById(R.id.about_credits);
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.feather_icon);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Setters

    public void setSearchedArtist(String searchedArtist){
        this.searchedArtist = searchedArtist;
    }

    public void setSearchedTrack(String searchedTrack){
        this.searchedTrack = searchedTrack;
    }

    public void setDiscographyStart(String discographyStart) { this.discographyStart = discographyStart; }

    // Getters

    public String getDiscographyStart() { return discographyStart; }

    public String getPlayingArtist() {
        return playingArtist;
    }

    public String getPlayingTrack() {
        return playingTrack;
    }

    public String getSearchedArtist() {
        return searchedArtist;
    }

    public String getSearchedTrack() {
        return searchedTrack;
    }
}

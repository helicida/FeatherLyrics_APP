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
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

import tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Discografia.Item;
import tk.sbarjola.pa.featherlyricsapp.Canciones.Canciones;
import tk.sbarjola.pa.featherlyricsapp.Discografia.Album;
import tk.sbarjola.pa.featherlyricsapp.Discografia.Discografia;
import tk.sbarjola.pa.featherlyricsapp.Home.About;
import tk.sbarjola.pa.featherlyricsapp.Home.Home;
import tk.sbarjola.pa.featherlyricsapp.Identificacion.LoginActivity;
import tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.BaseFragmentUser;
import tk.sbarjola.pa.featherlyricsapp.Mapa.OSMap;
import tk.sbarjola.pa.featherlyricsapp.Noticias.Noticias;
import tk.sbarjola.pa.featherlyricsapp.MusicReceiver.MusicBroadcastReceiver;
import tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.TabLayoutFragments.EditProfile;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;                // Drawer
    private NavigationView navigationView;      // NavigationView
    Fragment fragment = null;                   // fragmento que ocupara el centro de nuestro navigation drawer
    Item searchedAlbum;                         // List que le pasamos al fragment de album
    String searchedAlbumURL;                    // URL que contiene la imagen de la caratula del album
    String discographyStart = "no artist";      // Nombre del artista que mostraremos en la sección de discografia
    String playingArtist = "no artist";         // Nombre del artista de la canción en reproducción
    String playingTrack = "no track";           // Nombre de la pista en reproducción
    String searchedArtist = "no artist";        // Nombre del artista seleccionado en discografia
    String searchedTrack = "no track";          // Nombre de la pista seleccionada en discografia
    String openedProfile = "no profile";        // UID del perfil a buscar

    // Para monitorizar la musica en reproducción
    BroadcastReceiver broadcastReceiver = null;

    // Preferncias
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

        this.getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Feather Lyrics");

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
                .setCustomAnimations(R.anim.fragment_enter_animation, R.anim.fragment_exit_animation, R.anim.fragment_enter_animation, R.anim.fragment_exit_animation)
                .addToBackStack(null)
                .replace(R.id.content_frame, new Noticias(), "home")
                .commit();

        navigationView.getMenu().getItem(0).setChecked(true);   // Marcamos el menu del navigation Drawer

        preferencias = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);

        // Con este BroadcastReceiver escucharemos a nuestro Receiver que controla la Musica

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

        TrackingData data = new TrackingData();

        data.setPlayingArtist(playingArtist);
        data.setPlayingTrack(playingTrack);
        data.setSearchedArtist(searchedArtist);
        data.setSearchedTrack(searchedTrack);

        EventBus.getDefault().postSticky(data);
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

        checkBackStackOverhead();

        // Función para llamar al fragment de canciones

        fragment = new Canciones();
        String tag = "canciones";

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter_animation, R.anim.fragment_exit_animation, R.anim.fragment_enter_animation, R.anim.fragment_exit_animation)
                .replace(R.id.content_frame, fragment, tag)
                .addToBackStack(null)
                .commit();

        navigationView.getMenu().getItem(1).setChecked(true);   // Marcamos el menu del navigation Drawer

        TrackingData data = new TrackingData();

        data.setPlayingArtist(playingArtist);
        data.setPlayingTrack(playingTrack);
        data.setSearchedArtist(searchedArtist);
        data.setSearchedTrack(searchedTrack);

        EventBus.getDefault().postSticky(data);

        getSupportActionBar().setTitle("Canciones");    // Cambiamos el titulo del ActionBar
    }

    public void abrirDiscografia() {

        checkBackStackOverhead();

        // Función para llamar al fragment de canciones

        fragment = new Discografia();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter_animation, R.anim.fragment_exit_animation, R.anim.fragment_enter_animation, R.anim.fragment_exit_animation)
                .addToBackStack(null)
                .replace(R.id.content_frame, fragment)
                .commit();

        navigationView.getMenu().getItem(2).setChecked(true);   // Marcamos el menu del navigation Drawer

        TrackingData data = new TrackingData();
        data.setDiscographyStart(discographyStart);

        EventBus.getDefault().postSticky(data);

        getSupportActionBar().setTitle("Discografia");    // Cambiamos el titulo del ActionBar
    }

    public void abrirPerfil() {

        checkBackStackOverhead();

        // Función para llamar al fragment de canciones
        fragment = new BaseFragmentUser();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter_animation, R.anim.fragment_exit_animation, R.anim.fragment_enter_animation, R.anim.fragment_exit_animation)
                .addToBackStack(null)
                .replace(R.id.content_frame, fragment)
                .commit();

        navigationView.getMenu().getItem(0).setChecked(true);   // Marcamos el menu del navigation Drawer

    }

    public void abrirEditorPerfil(){

        checkBackStackOverhead();

        // Función para llamar al fragment de editar perfil
        fragment = new EditProfile();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter_animation, R.anim.fragment_exit_animation, R.anim.fragment_enter_animation, R.anim.fragment_exit_animation)
                .addToBackStack(null)
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    public void abrirPersonalProfile() {

        // Que se abra el nuestro
        openedProfile = "no profile";

        checkBackStackOverhead();

        // Función para llamar al fragment del perfil personal
        fragment = new BaseFragmentUser();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter_animation, R.anim.fragment_exit_animation, R.anim.fragment_enter_animation, R.anim.fragment_exit_animation)
                .addToBackStack(null)
                .replace(R.id.content_frame, fragment)
                .commit();

        navigationView.getMenu().getItem(0).setChecked(true);   // Marcamos el menu del navigation Drawer
    }

    public void abrirDisco() {

        // Función para llamar al fragment de canciones
        fragment = new Album();

        checkBackStackOverhead();

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fragment_enter_animation, R.anim.fragment_exit_animation, R.anim.fragment_enter_animation, R.anim.fragment_exit_animation)
                .addToBackStack(null)
                .replace(R.id.content_frame, fragment)
                .commit();

        navigationView.getMenu().getItem(2).setChecked(true);   // Marcamos el menu del navigation Drawer
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        boolean transaccion = false; // Cuadno el boleano sea tre, se cambiará a otro fragment

        String tag = null;

        // Asignamos las acciones a cada menuItem del drawer
        if (id == R.id.nav_home) {
            fragment = new BaseFragmentUser();
            transaccion = true;
        }else if (id == R.id.nav_canciones) {
            abrirCanciones();
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
        } else if (id == R.id.nav_bio) {
            fragment = new About();
            transaccion = true;
        } else if(id == R.id.nav_local){
            fragment = new Home();
            transaccion = true;
        } else if (id == R.id.nav_salir) {

            try{
                unregisterReceiver(broadcastReceiver);
            }
            catch(IllegalArgumentException e){}

            // Borramos la cuenta
            sharedPreferencesEditor = preferencias.edit();
            sharedPreferencesEditor.putBoolean("autologin", false);
            sharedPreferencesEditor.commit();

            // Hacemos el intent al login
            Intent loginIntent = new Intent().setClass(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }

        // Si el boleano es true llamamos al nuevo fragment
        if(transaccion){

            checkBackStackOverhead();

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slideup, R.anim.slideout, R.anim.slideup, R.anim.slideout)
                    .addToBackStack(null)
                    .replace(R.id.content_frame, fragment, tag)
                    .commit();

            item.setChecked(true);  // Y lo ponemos como marcado.
            getSupportActionBar().setTitle(item.getTitle());
        }

        drawer.closeDrawers();
        return true;
    }

    public void checkBackStackOverhead(){

        FragmentManager fm = this.getSupportFragmentManager();

        int numeroFragments = fm.getBackStackEntryCount();

        // Cuando nos pasemos de 3 fragments cargados en la memoria, nos eliminará el primero añadido a la pila
        if(numeroFragments > 3) {
            fm.popBackStack();
        }
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

    public void setOpenedProfile(String openedProfile) {
        this.openedProfile = openedProfile;
    }

    public void setSearchedAlbum(Item searchedAlbum) {
        this.searchedAlbum = searchedAlbum;
    }

    public void setSearchedAlbumURL(String searchedAlbumURL) {
        this.searchedAlbumURL = searchedAlbumURL;
    }

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

    public String getOpenedProfile() {
        return openedProfile;
    }

    public Item getSearchedAlbum() {
        return searchedAlbum;
    }

    public String getSearchedAlbumURL() {
        return searchedAlbumURL;
    }
}
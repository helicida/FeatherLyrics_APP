package tk.sbarjola.pa.featherlyricsapp.Canciones;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.List;
import at.markushi.ui.CircleButton;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;
import tk.sbarjola.pa.featherlyricsapp.Firebase.Artista;
import tk.sbarjola.pa.featherlyricsapp.Firebase.FirebaseConfig;
import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.R;

public class Canciones extends Fragment{

    // Datos de la API
    private String BaseURL = "http://api.vagalume.com.br/";         //Principio de la URL que usará retrofit
    private String apiKey = "754f223018be007a45003e3b87877bac";     // Key de Vagalume. Máximo 100.000 peticiones /dia
    private List<Mu> resultadosLetras = null;                       // List con el resultado de las letras obtenidas

    // Variables del fragment
    String playingArtist = "no artist";         // Nombre del artista de la canción en reproducción
    String playingTrack = "no track";           // Nombre de la pista en reproducción
    String searchedArtist = "no artist";        // Nombre del artista seleccionado en discografia
    String searchedTrack = "no track";          // Nombre de la pista seleccionada en discografia

    // Artista y pista que vamos a mostrar
    String artist = "no artist";
    String track = "no track";
    String cancionMostrada = "reproduccion";       // Que canción estamos mostrando en este momento
    String letraCancion;                           // String en el que guardaremos la letra de la canción

    FirebaseConfig config;

    // Variables y Adapters
    private servicioLetrasRetrofit servicioLetras;  // Interfaz para las peliculas populares

    // Declaramos el retrofit como variable global para poder reutilizarlo si es necesario
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup contenedor, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_canciones, contenedor, false);

        ProgressBar progress = (ProgressBar) view.findViewById(R.id.progressAnimation);   // Animacion de cargando
        CircleButton button = (CircleButton) view.findViewById(R.id.canciones_circleButton);   // Nuestro circle button

        // Descargamos la musica y ocultamos la animación de carga
        extraerInfoMusica();
        progress.setVisibility(View.GONE);

        // Seteamos las variables de la canción a mostrar
        if(!searchedArtist.equals("no artist")){
            artist = searchedArtist;
            track = searchedTrack;
            cancionMostrada = "busqueda";
        }
        else{
            artist = playingArtist;
            track = playingTrack;
            cancionMostrada = "reproduccion";
        }

        // Que descargue letras
        DescargarLetras descargarLetras = new DescargarLetras();  // Instanciams nuestro asyncTask para descargar en segundo plano la letra
        descargarLetras.execute();                                // Y lo ejecutamos

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Programamos el botón para que alterne entra la canción en reproducción y la buscada

                if (!playingArtist.equals("no artist") && !searchedArtist.equals("no artist")) {    // Comprueba si hay algún artista en reproducción

                    ProgressBar progress = (ProgressBar) getView().findViewById(R.id.progressAnimation);   // Animacion de cargando

                    if (cancionMostrada.equals("reproduccion") && !searchedArtist.equals("no artist") && !searchedTrack.equals("no track")) {
                        artist = searchedArtist;
                        track = searchedTrack;
                        cancionMostrada = "busqueda";
                        progress.setVisibility(View.VISIBLE);
                    } else if (cancionMostrada.equals("busqueda") && !playingTrack.equals("no track")) {
                        artist = playingArtist;
                        track = playingTrack;
                        cancionMostrada = "reproduccion";
                        progress.setVisibility(View.VISIBLE);
                    }

                    DescargarLetras descargarLetras = new DescargarLetras();  // Instanciams nuestro asyncTask para descargar en segundo plano la letra
                    descargarLetras.execute();
                                                 // Y lo ejecutamos
                } else {
                    Toast.makeText(getContext(), "No se ha detectado ningun artista", Toast.LENGTH_SHORT).show(); // Mostramos un toast
                }
            }
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_discografia) {
            ((MainActivity) getActivity()).setDiscographyStart(artist);
            ((MainActivity) getActivity()).abrirDiscografia();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){ //Afegim una opcio "Refresh" al menu del fragment
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();   // Limpiamos el menú

        // Y creamos el suyo propio con un buscador
        inflater.inflate(R.menu.dashboard, menu);
        inflater.inflate(R.menu.canciones_fragment_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.contains("-")) {
                    searchedTrack = query.split("-")[0];   // Cojemos la primer aparte de la busqueda que será la canción
                    searchedArtist = query.split("-")[1];  // Y la segunda que será el grupo
                } else {
                    searchedTrack = query;
                    searchedArtist = query;
                }

                // Seteamos las variables
                track = searchedTrack;
                artist = searchedArtist;
                cancionMostrada = "busqueda";

                // Descargamos la información de las canciones
                DescargarLetras descargarLetras = new DescargarLetras();  // Instanciams nuestro asyncTask para descargar en segundo plano la letra
                descargarLetras.execute();                                // Y lo ejecutamos

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
    }


    class DescargarLetras extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            descargarLetra();
            return null;
        }
    }

    public void descargarLetra(){

        servicioLetras = retrofit.create(servicioLetrasRetrofit.class);

        Call<LyricsList> llamada = (Call<LyricsList>) servicioLetras.letras(artist, track, apiKey);

        llamada.enqueue(new Callback<LyricsList>() {
            @Override
            public void onResponse(Response<LyricsList> response, Retrofit retrofit) {

                if (response.isSuccess()) {

                    LyricsList resultado = response.body();

                    try{
                        resultadosLetras = resultado.getMus();

                        if (resultadosLetras.size() == 0) {   // Si no hemos conseguido nada mostramos que la letra no está disponible
                            letraCancion = "Letra no disponible";
                        } else {
                            letraCancion = resultadosLetras.get(0).getText();

                            // Ajustamos correctamente el nombre de pista y el del artista
                            track = resultadosLetras.get(0).getName();
                            artist = resultado.getArt().getName();

                            TextView textCancion = (TextView) getView().findViewById(R.id.canciones_letraCancion);
                            TextView tituloCancion = (TextView) getView().findViewById(R.id.canciones_instrucciones);
                            ProgressBar progress = (ProgressBar) getView().findViewById(R.id.progressAnimation);   // Animacion de cargando

                            progress.setVisibility(View.GONE);  // Una vez descargado todo ocultamos la animación
                            tituloCancion.setText(track + "\n" + artist);   // Asignamos el titulo de la canción y el grupo a su textView
                            textCancion.setText(letraCancion);  // Asigamos la letra de la canción a su textView

                            ScrollView scrollLetra = (ScrollView) getView().findViewById(R.id.canciones_scrollView);
                            scrollLetra.fullScroll(ScrollView.FOCUS_UP);    // Cada vez que pone el texto de una canción, mueve el scrollView al principio

                            // Subimos el artista a Firebase para guardar un registro
                            config = (FirebaseConfig) getActivity().getApplication();
                            Artista artista = new Artista();

                            // Cogemos el artista que nos interese
                            if(cancionMostrada.equals("reproduccion") && !playingArtist.equals("no artist")){
                                artista.setArtistas(playingArtist);
                            }
                            else if (cancionMostrada.equals("busqueda") && !searchedArtist.equals("no artist")){
                                artista.setArtistas(searchedArtist);
                            }

                            // If para prevenir subidas erroneas de artista
                            if(!artista.getArtistas().equals("no artist")){
                                subirArtista(artista);
                            }
                        }
                    }
                    catch (NullPointerException ex){}


                } else {
                    Toast.makeText(getContext(), "Canción no disponible", Toast.LENGTH_SHORT).show(); // Mostramos un toast
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), "Se ha producido un error", Toast.LENGTH_SHORT).show(); // Mostramos un toast
            }
        });

    }

    public void subirArtista(Artista artista) {
        Firebase refUsuario = config.getReferenciaUsuarioLogeado().child("Artistas");
        Firebase artistaAsubir = refUsuario.push();
        artistaAsubir.setValue(artista);
    }

    public interface servicioLetrasRetrofit{ //Interficie para descargar las letras
        @GET("search.php")
        Call<LyricsList> letras(
                @Query("art") String artista,
                @Query("mus") String songName,
                @Query("apikey") String api
        );
    }

    public void setSong(String artist, String track) {

        // Les damos a las variables globales el valor de la que hemos recibido para pasarsela a retrofit
        this.playingArtist = artist;
        this.playingTrack = track;
        this.artist = artist;
        this.track = track;

        if(cancionMostrada.equals("reproduccion")) {

            TextView textCancion = (TextView) getView().findViewById(R.id.canciones_letraCancion);
            textCancion.setText("");
            ProgressBar progress = (ProgressBar) getView().findViewById(R.id.progressAnimation);   // Animacion de cargando
            progress.setVisibility(View.VISIBLE);

            // Descargamos la información de las canciones
            DescargarLetras descargarLetras = new DescargarLetras();  // Instanciams nuestro asyncTask para descargar en segundo plano la letra
            descargarLetras.execute();                                // Y lo ejecutamos
        }
    }

    public void extraerInfoMusica(){

        // Sacamos los datos de la cancion en reproduccion
        playingArtist = ((MainActivity)getActivity()).getPlayingArtist();
        playingTrack = ((MainActivity)getActivity()).getPlayingTrack();

        // Y de la cancion buscada
        searchedArtist = ((MainActivity)getActivity()).getSearchedArtist();
        searchedTrack = ((MainActivity)getActivity()).getSearchedTrack();

        ((MainActivity)getActivity()).setSearchedArtist("no artist");
        ((MainActivity)getActivity()).setSearchedTrack("no track");

    }
}
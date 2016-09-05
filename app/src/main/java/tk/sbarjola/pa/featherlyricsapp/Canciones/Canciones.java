package tk.sbarjola.pa.featherlyricsapp.Canciones;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
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

import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import at.markushi.ui.CircleButton;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

import retrofit.http.Url;
import tk.sbarjola.pa.featherlyricsapp.APIs.Spotify.ArtistSpotify;
import tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Canciones.LyricsList;
import tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Canciones.Mu;
import tk.sbarjola.pa.featherlyricsapp.Firebase.FirebaseItem;
import tk.sbarjola.pa.featherlyricsapp.Firebase.FirebaseConfig;
import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.R;

public class Canciones extends Fragment{

    // Datos de la API
    private String BaseURL = "http://api.vagalume.com.br/";         //Principio de la URL que usará retrofit
    private String apiKey = "754f223018be007a45003e3b87877bac";     // Key de Vagalume. Máximo 100.000 peticiones /dia
    private String URLSpotify = "";                                 // Spotify
    private List<Mu> resultadosLetras = null;                       // List con el resultado de las letras obtenidas

    // Objetos que subiremos a firebase
    FirebaseItem cancion = new FirebaseItem();
    String cancionKey = "adsad";
    FirebaseItem firebaseItem = new FirebaseItem();
    String artistaKey = "adsad";

    // Variables del fragment
    String playingArtist = "no artist";         // Nombre del firebaseItem de la canción en reproducción
    String playingTrack = "no track";           // Nombre de la pista en reproducción
    String searchedArtist = "no artist";        // Nombre del firebaseItem seleccionado en discografia
    String searchedTrack = "no track";          // Nombre de la pista seleccionada en discografia

    // Artista y pista que vamos a mostrar
    String artist = "no artist";                  // Arista que buscaremos
    String track = "no track";                    // Pista que buscaremos
    String url_cancion = "URL desconocida";       // Url de la cancion (imagen)
    String url_artista = "URL desconocida";       // Url del artista (imagen)
    String cancionMostrada = "reproduccion";      // Que canción estamos mostrando en este momento
    String letraCancion;                          // String en el que guardaremos la letra de la canción

    FirebaseConfig config;  // Config firebase

    // Variables y Adapters
    private servicioLetrasRetrofit servicioLetras;  // Interfaz para las peliculas populares
    private servicioImagenArtistaRetrofit servicioImagen;      // Interfaz para descargar la imagen

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

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.toolbar_canciones);

        ProgressBar progress = (ProgressBar) view.findViewById(R.id.progressAnimation);   // Animacion de cargando
        CircleButton button = (CircleButton) view.findViewById(R.id.canciones_circleButton);   // Nuestro circle button

        // Descargamos la musica y ocultamos la animación de carga
        extraerInfoMusica();
        progress.setVisibility(View.GONE);

        // Seteamos las variables de la canción a mostrar
        if(!searchedArtist.equals("no artist")){
            artist = searchedArtist;
            track = filtrarTitulo(searchedTrack);
            cancionMostrada = "busqueda";
        }
        else{
            artist = playingArtist;
            track = filtrarTitulo(playingTrack);
            cancionMostrada = "reproduccion";
        }

        // Que descargue letras
        DescargarLetras descargarLetras = new DescargarLetras();  // Instanciams nuestro asyncTask para descargar en segundo plano la letra
        descargarLetras.execute();                                // Y lo ejecutamos

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Programamos el botón para que alterne entra la canción en reproducción y la buscada

                if (!playingArtist.equals("no artist") && !searchedArtist.equals("no artist")) {    // Comprueba si hay algún firebaseItem en reproducción

                    ProgressBar progress = (ProgressBar) getView().findViewById(R.id.progressAnimation);   // Animacion de cargando

                    if (cancionMostrada.equals("reproduccion") && !searchedArtist.equals("no artist") && !searchedTrack.equals("no track")) {
                        artist = searchedArtist;
                        track = filtrarTitulo(searchedTrack);
                        cancionMostrada = "busqueda";
                        progress.setVisibility(View.VISIBLE);
                    } else if (cancionMostrada.equals("busqueda") && !playingTrack.equals("no track")) {
                        artist = playingArtist;
                        track = filtrarTitulo(playingTrack);
                        cancionMostrada = "reproduccion";
                        progress.setVisibility(View.VISIBLE);
                    }

                    DescargarLetras descargarLetras = new DescargarLetras();  // Instanciams nuestro asyncTask para descargar en segundo plano la letra
                    descargarLetras.execute();                                // Y lo ejecutamos

                } else {
                    Toast.makeText(getActivity(), "No se ha detectado ninguna canción en reproducción o buscada", Toast.LENGTH_SHORT).show(); // Mostramos un toast
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
        sv.setQueryHint("Example: Wonderwall - Oasis");
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
                track = filtrarTitulo(searchedTrack);
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

    public void descargarLetra(){

        servicioLetras = retrofit.create(servicioLetrasRetrofit.class);

        Call<LyricsList> llamada = (Call<LyricsList>) servicioLetras.letras(artist, track, apiKey);

        llamada.enqueue(new Callback<LyricsList>() {
            @Override
            public void onResponse(Response<LyricsList> response, Retrofit retrofit) {

                if (response.isSuccess()) {

                    final LyricsList resultado = response.body();

                    try{
                        resultadosLetras = resultado.getMus();

                        if (resultadosLetras.size() == 0) {   // Si no hemos conseguido nada mostramos que la letra no está disponible
                            letraCancion = "Letra no disponible";
                        } else {
                            letraCancion = resultadosLetras.get(0).getText();

                            // Ajustamos correctamente el nombre de pista y el del firebaseItem
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

                            // Subimos el firebaseItem a Firebase para guardar un registro
                            config = (FirebaseConfig) getActivity().getApplication();

                            if(cancion != null && artist != null) {

                                String textArtist = artist.replace(".", "").replace("$", "").replace("#", "").replace("[", "").replace("]", "");
                                String textCanciones = track.replace(".", "").replace("$", "").replace("#", "").replace("[", "").replace("]", "");

                                cancionKey = textCanciones + "-" + textArtist;
                                artistaKey = textArtist;

                                URLSpotify = "https://api.spotify.com/v1/search?q=" + textArtist + "&type=artist";

                                // Lo mismo para los datos del firebaseItem
                                DescargarArtista descargarArt = new DescargarArtista();
                                descargarArt.execute();
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

    public void descargaArtista(){

        servicioImagen = retrofit.create(servicioImagenArtistaRetrofit.class);

        Call<ArtistSpotify> llamadaSpotify = (Call<ArtistSpotify>) servicioImagen.artistsSpotify(URLSpotify);

        llamadaSpotify.enqueue(new Callback<ArtistSpotify>() {
            @Override
            public void onResponse(Response<ArtistSpotify> response, Retrofit retrofit) {

               final ArtistSpotify resultado = response.body();

                if (response.isSuccess()) {

                    if(resultado.getArtists().getItems().size() != 0){

                        try{
                            //Comprobamos si el firebaseItem tiene imagen
                            if(resultado.getArtists().getItems().get(0).getImages().size() != 0){

                                // Extraemos la URL de nuestra imagen parsendo el JSON
                                String URLimagen = resultado.getArtists().getItems().get(0).getImages().get(0).toString();
                                URLimagen = URLimagen.split(",")[1].split(",")[0].replace("url=", "").trim();
                                cancion.setItemUrl(URLimagen);
                                firebaseItem.setItemUrl(URLimagen);
                            }
                        }
                        catch (NullPointerException ex){}
                    }

                } else {
                    url_artista = "URL desconocido";
                    url_cancion = "URL desconocida";
                }

                subirArtista(firebaseItem, artistaKey);
                subirCancion(cancion, cancionKey);
            }

            @Override
            public void onFailure(Throwable t) {

                url_artista = "URL desconocida";

                subirArtista(firebaseItem, artistaKey);
                subirCancion(cancion, cancionKey);
            }
        });
    }

    // Metodos firebase

    public void subirCancion(FirebaseItem firebaseItem, String key) {

        Firebase refCancionesUsuarioLoggeado = config.getReferenciaUsuarioLogeado().child("Canciones").child(key);
        refCancionesUsuarioLoggeado.setValue(firebaseItem);
    }

    public void subirArtista(FirebaseItem firebaseItem, String key) {

        Firebase refArtistasUsuarioLoggeado = config.getReferenciaUsuarioLogeado().child("Artistas").child(key);
        refArtistasUsuarioLoggeado.setValue(firebaseItem);
    }

    // Interficies retrofit

    public interface servicioLetrasRetrofit{ //Interficie para descargar las letras
        @GET("search.php")
        Call<LyricsList> letras(
                @Query("art") String artista,
                @Query("mus") String songName,
                @Query("apikey") String api
        );
    }

    public interface servicioImagenArtistaRetrofit{ // Interficie para descargar la imagen del firebaseItem
        @GET
        Call<ArtistSpotify> artistsSpotify(@Url String url); // Le pasamos la URL entera ya construida
    }

    // Async Tasks

    class DescargarLetras extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            descargarLetra();
            return null;
        }
    }

    class DescargarArtista extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            descargaArtista();
            return null;
        }
    }

    public void setSong(String artist, String track) {

        // Les damos a las variables globales el valor de la que hemos recibido para pasarsela a retrofit
        this.playingArtist = artist;
        this.playingTrack = filtrarTitulo(track);
        this.artist = artist;
        this.track = filtrarTitulo(track);

        if(cancionMostrada.equals("reproduccion")) {

            try{

                TextView textCancion = (TextView) getView().findViewById(R.id.canciones_letraCancion);
                textCancion.setText("");
                ProgressBar progress = (ProgressBar) getView().findViewById(R.id.progressAnimation);   // Animacion de cargando
                progress.setVisibility(View.VISIBLE);

                // Descargamos la información de las canciones
                DescargarLetras descargarLetras = new DescargarLetras();  // Instanciams nuestro asyncTask para descargar en segundo plano la letra
                descargarLetras.execute();                                // Y lo ejecutamos

            }catch (NullPointerException e){}
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

    public String filtrarTitulo(String titulo){

        // Metodo que elimina palabras clave de los titulos

        String tituloFiltrado = titulo;
        tituloFiltrado = tituloFiltrado.replace(" - ", "");
        tituloFiltrado = tituloFiltrado.replace("[Live]", "");
        tituloFiltrado = tituloFiltrado.replace("[Directo]", "");
        tituloFiltrado = tituloFiltrado.replace("[Bonus]", "");
        tituloFiltrado = tituloFiltrado.replace("[*]", "");
        tituloFiltrado = tituloFiltrado.replace("Directo", "");
        tituloFiltrado = tituloFiltrado.replace("Live", "");
        tituloFiltrado = tituloFiltrado.replace("Bonus", "");
        tituloFiltrado = tituloFiltrado.replace("*", "");

        return tituloFiltrado;
    }
}
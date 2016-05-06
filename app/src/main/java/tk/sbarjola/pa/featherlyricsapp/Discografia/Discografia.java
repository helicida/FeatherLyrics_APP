package tk.sbarjola.pa.featherlyricsapp.Discografia;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Url;

import tk.sbarjola.pa.featherlyricsapp.APIs.Lastfm.ArtistInfo;
import tk.sbarjola.pa.featherlyricsapp.APIs.Spotify.ArtistSpotify;
import tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Discografia.Discography;
import tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Discografia.Item;
import tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Discografia.ListDiscografia;
import tk.sbarjola.pa.featherlyricsapp.Discografia.Adapters.DiscografiaAdapter;
import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.R;

public class Discografia extends Fragment {

    // Datos de la APIf
    private String BaseURL = "http://api.vagalume.com.br/";         // Principio de la URL que usará retrofit
    private final static String endURL = "/discografia/index.js";   // Ultima parte de la url
    private String URLVagalume = "";                                // Parte del medio que será el artista en minusculas y los espacios cambiados por guiones
    private String URLSpotify = "";                                 // Spotify
    private String URLLastFm = "";                                  // LastFM
    private String artist = "";                                     // Nombre del artista
    private String artistSpotify = "Arista no disponible";          // Nombre del artista de la imagen de Spotify

    //  Booleano que determina si vagalume ha encontrado
    private boolean vagalumeFound = true;

    // Variables y Adapters
    private servicioDiscografiaRetrofit servicioDiscografia;   // Interfaz para descargar la discografia
    private servicioLetraRetrofit servicioLetra;               // Interfaz para descargar la imagen
    private servicioImagenArtistaRetrofit servicioImagen;      // Interfaz para descargar la imagen
    private ArrayList<Item> items = new ArrayList<>();;        // ArrayList que llenaremos con los albumes
    private GridView gridDiscos;                               // Grid View donde mostraremos los discos
    private DiscografiaAdapter myGridAdapter;                  // Adaptador para el gridView

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

    @Override
    public void onStart() { //Cada vez que se abra el fragment que se descargen las noticias
        super.onStart();

        artist = ((MainActivity) getActivity()).getDiscographyStart();

        if (artist.equals("no artist")){
            artist = "Iron Maiden";
        }

        // Descargar discografía e imagen
        DescargarDiscografia descargarDiscografia = new DescargarDiscografia();  // Instanciams nuestro asyncTask para descargar en segundo plano las noticias
        DescargarArtista descargarArt = new DescargarArtista();                  // Lo mismo para los datos del artista
        descargarDiscografia.execute();                                          // Y lo ejecutamos
        descargarArt.execute();

        // Descargar info artista

        URLLastFm = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + artist.replace("-", "") + "&api_key=29d77eab70a48896e7b80f7d5977a5be&format=json";

        DescargarInfo info = new DescargarInfo();                                // Lo mismo para la biografia del artista
        info.execute();                                                          // Y ejecutamos tambien

        ((MainActivity) getActivity()).setDiscographyStart(artist);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_discografia, container, false);

        // Asignamos el grid y el list a sus variables
        gridDiscos = (GridView) view.findViewById(R.id.discografia_gridDiscos);

        // Los hacemos no focusable para que el scrollView inicie al principio
        gridDiscos.setFocusable(false);

        // Seccion del grid y los albumes
        myGridAdapter = new DiscografiaAdapter(container.getContext(), 0, items);  // Definimos nuestro adaptador
        gridDiscos.setAdapter(myGridAdapter);                                      // Y acoplamos el adaptador


        gridDiscos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  // En caso de pulsar sobre un album

                // Subimos al inicio del ScrollView
                ScrollView scrollLetra = (ScrollView) getView().findViewById(R.id.discografia_scrollViewDiscografia);
                scrollLetra.fullScroll(ScrollView.FOCUS_UP);

                Item disco = items.get(position);    // Sacamos los discos del elemento que hayamos pulsado

                ((MainActivity) getActivity()).setSearchedArtist(artist);
                ((MainActivity) getActivity()).setSearchedAlbum(disco);
                ((MainActivity) getActivity()).abrirDisco();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();   // Limpiamos el menú

        // Y generamos el propio de este fragment con su buscador
        inflater.inflate(R.menu.dashboard, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, sv);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                artist = query;

                // La url de lastfm necesita un artista con espacios y sin caracteres especiales
                URLLastFm = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + artist.replace("-", "").replace("'", "") + "&api_key=29d77eab70a48896e7b80f7d5977a5be&format=json";

                DescargarDiscografia descargarDiscografia = new DescargarDiscografia();  // Instanciams nuestro asyncTask para descargar en segundo plano la discografia
                descargarDiscografia.execute();                                          // Y lo ejecutamos
                DescargarArtista descargarArt = new DescargarArtista();                  // Lo mismo para los datos del artista
                descargarArt.execute();                                                  // Y ejecutamos tambien
                DescargarInfo info = new DescargarInfo();                                // Lo mismo para la biografia del artista
                info.execute();                                                          // Y ejecutamos tambien

                ((MainActivity) getActivity()).setDiscographyStart(artist);              // Fijamos el artista en el mainArtista
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("tap");
                return false;
            }
        });
    }

    public void descargarDiscografia(){

        artist = artist.toLowerCase();      // la busqueda del usuario la pasamos a minusculas
        artist = artist.replace(" ", "-");  // y cambiamos los espacios por guiones
        artist = artist.replace("'", "");  // y cambiamos las comillas

        // Y construimos las URL's
        URLVagalume = BaseURL + artist + endURL;
        URLSpotify = "https://api.spotify.com/v1/search?q=" + artist + "&type=artist";

        servicioDiscografia = retrofit.create(servicioDiscografiaRetrofit.class);

        Call<ListDiscografia> llamada = (Call<ListDiscografia>) servicioDiscografia.discografia(URLVagalume);

        llamada.enqueue(new Callback<ListDiscografia>() {
            @Override
            public void onResponse(Response<ListDiscografia> response, Retrofit retrofit) {

                if (response.isSuccess()) {

                    final ListDiscografia resultado = response.body();
                    final Discography discografia = resultado.getDiscography();
                    artist = resultado.getDiscography().getArtist().getDesc();

                    vagalumeFound = true;
                    myGridAdapter.clear();

                    try {

                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(artist);

                        TextView artista = (TextView) getView().findViewById(R.id.discografia_artistName);
                        artista.setText(artist);

                        for (int iterador = 0; iterador < discografia.getItem().size(); iterador++) {
                            myGridAdapter.add(discografia.getItem().get(iterador));
                        }

                        if (myGridAdapter.getCount() != 0) {
                            setGridViewHeightBasedOnChildren(gridDiscos, 2);
                        }



                    } catch (NullPointerException e) {}

                } else {

                    TextView artista = (TextView) getView().findViewById(R.id.discografia_artistName);
                    artista.setText(artistSpotify);

                    if (vagalumeFound == false){
                        Toast.makeText(getContext(), "Discografía no disponible", Toast.LENGTH_SHORT).show(); // Mostramos un toast
                    }

                    myGridAdapter.clear();
                    vagalumeFound = false;
                }
            }

            @Override
            public void onFailure(Throwable t) {

                if(vagalumeFound == false){
                    Toast.makeText(getContext(), "Se ha producido un error", Toast.LENGTH_SHORT).show(); // Mostramos un toast
                }

                myGridAdapter.clear();
                vagalumeFound = false;
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

                    String datosArtista = "";   // String que contiene la popularidad y generos del artista

                    if(resultado.getArtists().getItems().size() != 0){

                        try{
                            artistSpotify = resultado.getArtists().getItems().get(0).getName(); // Extraemos el nombre del artista del cual descargamos la imagen

                            // Imagen y textView relacionados con el artista
                            ImageView imagenArtista = (ImageView) getView().findViewById(R.id.discografia_artistImage);

                            if(resultado.getArtists().getItems().get(0).getPopularity() != null){
                                datosArtista = "Popularidad: " + resultado.getArtists().getItems().get(0).getPopularity() + "%";
                            }

                            if(resultado.getArtists().getItems().get(0).getGenres().size() != 0){
                                datosArtista = datosArtista + "                 Género: " + resultado.getArtists().getItems().get(0).getGenres().get(0).toString();
                            }

                            //Comprobamos si el artista tiene imagen

                            if(resultado.getArtists().getItems().get(0).getImages().size() != 0){

                                // Extraemos la URL de nuestra imagen parsendo el JSON
                                String URLimagen = resultado.getArtists().getItems().get(0).getImages().get(0).toString();
                                URLimagen = URLimagen.split(",")[1].split(",")[0].replace("url=", "").trim();

                                Picasso.with(getContext()).load(URLimagen).fit().centerCrop().into(imagenArtista);

                                ScrollView scrollLetra = (ScrollView) getView().findViewById(R.id.discografia_scrollViewDiscografia);
                                scrollLetra.fullScroll(ScrollView.FOCUS_UP);
                            }

                            // Si spotify ha encontrado el artista pero vagalume no, cambiamos el artista buscado por el nombre del encontrado en spotify
                            // y volvemos a hacer peticiones Vagalume y LastFm
                            if(vagalumeFound == false && resultado != null){

                                // Dormirmos un tiempo hasta que acaben
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        artist = resultado.getArtists().getItems().get(0).getName();
                                        URLLastFm = "http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + artist.replace("-", "") + "&api_key=29d77eab70a48896e7b80f7d5977a5be&format=json";

                                        DescargarDiscografia descargarDiscografia = new DescargarDiscografia();  // Instanciams nuestro asyncTask para descargar en segundo plano las noticias
                                        descargarDiscografia.execute();                                          // Ejecutamos el async task de discograía
                                        DescargarInfo info = new DescargarInfo();                                // Lo mismo para la biografia del artista
                                        info.execute();                                                          // Y ejecutamos tambien
                                    }
                                }, 2000);
                            }
                        }
                        catch (NullPointerException ex){}
                    }

                    try{
                        TextView infoArtista = (TextView) getView().findViewById(R.id.discografia_artistInfo);
                        infoArtista.setText(datosArtista);  // Asignamos los datos del artista
                    }
                    catch (NullPointerException e){}

                } else {
                    Toast.makeText(getContext(), "Imagen de artista no disponible", Toast.LENGTH_SHORT).show(); // Mostramos un toast
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(getContext(), "Se ha producido un error al buscar la imagen de artista", Toast.LENGTH_SHORT).show(); // Mostramos un toast
            }
        });
    }

    public void descargarInfo(){

        servicioLetra = retrofit.create(servicioLetraRetrofit.class);

        Call<ArtistInfo> llamadaLastFm = (Call<ArtistInfo>) servicioLetra.artistsLastfm(URLLastFm);

        llamadaLastFm.enqueue(new Callback<ArtistInfo>() {
            @Override
            public void onResponse(Response<ArtistInfo> response, Retrofit retrofit) {

                try{

                    final ArtistInfo resultado = response.body();

                        if (response.isSuccess()) {

                            String datosArtista = "";   // String que contiene la popularidad y generos del artista

                            if(resultado.getArtist() != null){

                                datosArtista = resultado.getArtist().getBio().getSummary();

                                String toDelete = "There are multiple artists with this name:";

                                if (datosArtista.contains(toDelete)){
                                    datosArtista = datosArtista.substring(toDelete.length() + 5, datosArtista.length());
                                }

                                TextView bioArtista = (TextView) getView().findViewById(R.id.discografia_bio);

                                if(!datosArtista.substring(0,9).contains("<a href=") && !datosArtista.substring(0,9).contains("Fix your tags to")){
                                    bioArtista.setText(datosArtista);  // Asignamos los datos del artista
                                }
                                else {
                                    bioArtista.setText("Biografía no encontrada");
                                }
                          }
                        } else {
                            Toast.makeText(getContext(), "Biografía del artista no disponible", Toast.LENGTH_SHORT).show(); // Mostramos un toast
                        }
                }catch (NullPointerException e){}
            }

            @Override
            public void onFailure(Throwable t) {
                if(vagalumeFound == false){
                    Toast.makeText(getContext(), "No se ha encontrado la discografía del artista", Toast.LENGTH_SHORT).show(); // Mostramos un toast

                }
            }
        });
    }

    public interface servicioDiscografiaRetrofit{ //Interficie para descargar las discografia de un artista
        @GET
        Call<ListDiscografia> discografia(@Url String url); // Le pasamos la URL entera ya construida
    }

    public interface servicioImagenArtistaRetrofit{ // Interficie para descargar la imagen del artista
        @GET
        Call<ArtistSpotify> artistsSpotify(@Url String url); // Le pasamos la URL entera ya construida
    }

    public interface servicioLetraRetrofit{ // Interficie para descargar información sobre el artista
        @GET
        Call<ArtistInfo> artistsLastfm(@Url String url); // Le pasamos la URL entera ya construida
    }

    // AsyncTasks en los que ejecutaremos nuestras descargas en segundo plano
    class DescargarDiscografia extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            descargarDiscografia();
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

    class DescargarInfo extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            descargarInfo();
            return null;
        }
    }

    // Métodos auxiliares que calculan como expandir el list view y el gridView

    public void setGridViewHeightBasedOnChildren(GridView gridView, int columnas) {

        // Calculamos caunto hay que desplegar el GridView para poder mostrarlo todo dentro del ScrollView

        try {

            int alturaTotal = 0;
            int items = myGridAdapter.getCount();
            int filas = 0;

            View listItem = myGridAdapter.getView(0, null, gridView);
            listItem.measure(0, 0);
            alturaTotal = listItem.getMeasuredHeight();

            float x = 1;

            if (items > columnas) {
                x = items / columnas;
                filas = (int) (x + 1);
                alturaTotal *= filas;
            }

            ViewGroup.LayoutParams params = gridView.getLayoutParams();
            params.height = alturaTotal;
            gridView.setLayoutParams(params);

        } catch (IndexOutOfBoundsException e){}
    }
}

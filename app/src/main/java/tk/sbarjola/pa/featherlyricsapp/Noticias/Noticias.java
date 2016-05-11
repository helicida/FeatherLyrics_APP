package tk.sbarjola.pa.featherlyricsapp.Noticias;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;

import tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Noticias.News;
import tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Noticias.NewsList;
import tk.sbarjola.pa.featherlyricsapp.R;

public class Noticias extends Fragment{

    // Datos de la API
    private String BaseURL = "http://api.vagalume.com.br/";    //Principio de la URL que usará retrofit

    // Variables y Adapters
    private servicioNoticiasRetrofit servicioNoticias;   // Interfaz para las noticias
    private ArrayList<News> items = new ArrayList<>();   // ArrayList con los items
    private ListView listaNoticias;                      // ListView en el que mostraremos los items
    NoticiasAdapter myListAdapter;                       //Adaptador per al listView

    // Declaramos el retrofit como variable global para poder reutilizarlo si es necesario
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BaseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    @Override
    public void onStart() {
        super.onStart();

        //Cada vez que se abra el fragment que se descargen las noticias
        DescargarNoticias descargarNoticias = new DescargarNoticias();  // Instanciams nuestro asyncTask para descargar en segundo plano las noticias
        descargarNoticias.execute();    // Y lo ejecutamos
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public void descargarNoticias(){

        servicioNoticias = retrofit.create(servicioNoticiasRetrofit.class);

        Call<NewsList> llamada = (Call<NewsList>) servicioNoticias.noticias();

        llamada.enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Response<NewsList> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    NewsList resultado = response.body();
                    myListAdapter.clear();
                    myListAdapter.addAll(resultado.getNews());
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View fragmentoLista = inflater.inflate(R.layout.fragment_noticias, container, false);    //Definimos el fragment

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.toolbar_noticias);

        try{

            // Asignamos el list a su variable
            listaNoticias = (ListView) fragmentoLista.findViewById(R.id.noticias_listaNoticias);

            myListAdapter = new NoticiasAdapter(container.getContext(), 0, items);  // Definimos el adapter
            listaNoticias.setAdapter(myListAdapter);                                // Acoplamos el adaptador

            listaNoticias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  //Listener para el list
                    // De momento no hace nada
                }
            });

        }catch (NullPointerException e){}

        return fragmentoLista;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            myListAdapter.clear();  // Limpia el arrayList que contiene las noticias

            DescargarNoticias descargarNoticias = new DescargarNoticias();  // Instanciams nuestro asyncTask para descargar en segundo plano las noticias
            descargarNoticias.execute();    // Y lo ejecutamos

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){ //Afegim una opcio "Refresh" al menu del fragment
        super.onCreateOptionsMenu(menu, inflater);


        inflater.inflate(R.menu.noticias_fragment_menu, menu);
    }

    public interface servicioNoticiasRetrofit{ //Interficie para descargar las noticias
        @GET("news/index.js")
        Call<NewsList> noticias();
    }

    class DescargarNoticias extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            descargarNoticias();
            return null;
        }
    }
}

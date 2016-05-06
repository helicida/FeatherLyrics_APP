package tk.sbarjola.pa.featherlyricsapp.Discografia;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import java.util.ArrayList;
import java.util.List;

import tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Discografia.Item;
import tk.sbarjola.pa.featherlyricsapp.Discografia.Adapters.AlbumAdapter;
import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.R;

/**
 * Created by 46465442z on 04/05/16.
 */
public class Album extends Fragment {

    // Adapter y diferentes contenedores para la lista de artistas
    private AlbumAdapter myListAdapter;
    List<String> listCollectionMusic = new ArrayList<String>();

    // Disco
    Item disco;                         // Disco
    private ListView listCanciones;     // List View donde mostraremos las canciones

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_disco, container, false);

        try{
            listCanciones = (ListView) view.findViewById(R.id.disc_listAlbum);
            disco = ((MainActivity) getActivity()).getSearchedAlbum();

            TextView tituloAlbum = (TextView) view.findViewById(R.id.disc_albumTitle);
            TextView infoAlbum = (TextView) view.findViewById(R.id.disc_anyo);
            TextView discLabel = (TextView) view.findViewById(R.id.disc_label);
            ImageView imagenAlbum = (ImageView) view.findViewById(R.id.disc_albumImage);

            // Le damos la imagen de album transformada en redonda

            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(360)
                    .oval(false)
                    .build();

            Picasso.with(getContext()).load("http://www.vagalume.com.br/" + disco.getCover()).fit().centerCrop().transform(transformation).into(imagenAlbum);

            discLabel.setText(disco.getLabel());
            infoAlbum.setText(disco.getPublished());
            tituloAlbum.setText(disco.getDesc());

            // Los hacemos no focusable para que el scrollView inicie al principio
            listCanciones.setFocusable(false);

            // Sección del list y las canciones
            myListAdapter = new AlbumAdapter(container.getContext(), 0, listCollectionMusic, disco.getDesc());  // Definimos nuestro adaptador

            // Cambioamos el titulo de la toolbar
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(disco.getDesc() + " - " + disco.getPublished());

        }catch (RuntimeException e){}

        int numeroPista = 1;

        // Y cargamos las canciones del album correspondiente a nuestro listView

        try{
            // El primer for recorre los cd's del album, ya que pueden ser varios
            for (int iterador1 = 0; iterador1 < disco.getDiscs().size(); iterador1++) {

                // El segundo las canciones de cada CD
                for (int iterador2 = 0; iterador2 < disco.getDiscs().get(iterador1).size(); iterador2++) {
                    listCollectionMusic.add(numeroPista + " - " + disco.getDiscs().get(iterador1).get(iterador2).getDesc());
                    numeroPista++;
                }
            }

            try{

                // Acoplamos el adaptador y fijamos el album buscado
                listCanciones.setAdapter(myListAdapter);
                setListViewHeightBasedOnChildren(listCanciones);

                ((MainActivity) getActivity()).setSearchedAlbum(disco);

            }catch (NullPointerException e){}

        }catch (RuntimeException e){}

        // onClick del listView

        listCanciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {  // En caso de pulsar sobre un album

                // Extrae el artista y pista del MainActivity
                ((MainActivity) getActivity()).setSearchedTrack(listCanciones.getItemAtPosition(position).toString().split("-")[1]);
                ((MainActivity) getActivity()).abrirCanciones();
            }
        });

        return view;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {

        // Calculamos caunto hay que desplegar el GridView para poder mostrarlo todo dentro del ScrollView

        try{

            int alturaTotal = 0;
            int items = myListAdapter.getCount();
            int filas = 0;

            View listItem = myListAdapter.getView(0, null, listView);
            listItem.measure(0, 0);
            alturaTotal = listItem.getMeasuredHeight();

            float x = 1;

            x = items;
            filas = (int) (x + 2);
            alturaTotal *= filas;

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = alturaTotal;
            listView.setLayoutParams(params);

        }
        catch (IndexOutOfBoundsException e){}
    }



}

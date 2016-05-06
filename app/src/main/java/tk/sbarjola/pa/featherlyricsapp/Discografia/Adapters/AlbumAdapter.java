package tk.sbarjola.pa.featherlyricsapp.Discografia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import tk.sbarjola.pa.featherlyricsapp.R;

/**
 * Created by sergi on 22/12/15.
 */
public class AlbumAdapter extends ArrayAdapter<String> {

    String nombreArtista = "Desconocido";

    public AlbumAdapter(Context context, int resource, List<String> objects, String nombreArtista) {
        super(context, resource, objects);

        this.nombreArtista = nombreArtista;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Creamos el objeto en la posición correspondiente
        String tituloCancion = getItem(position);

        // Comprobamos si la view ya se ha usado antes, si no, la inflamos (es una buena practica y ahorramos recursos)
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.album_songs_adapter, parent, false);
        }

        // Asociamos cada variable a su elemento del layout
        TextView nombreCancion = (TextView) convertView.findViewById(R.id.album_adapter_tituloAlbum);
        TextView nombreGrupo = (TextView) convertView.findViewById(R.id.album_adapter_band);

        // Incorporamos los objetos al layout
        nombreGrupo.setText(nombreArtista);
        nombreCancion.setText(tituloCancion);

        return convertView; //Devolvemos la view ya rellena
    }

}

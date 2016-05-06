package tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.Adapters;

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
 * Created by 46465442z on 28/04/16.
 */
public class userSongsAdapter extends ArrayAdapter<String> {


    public userSongsAdapter(Context context, int resource, List<String> name) {
        super(context, resource, name);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Creamos el objeto en la posición correspondiente
        final String item = getItem(position);

        // Comprobamos si la view ya se ha usado antes, si no, la inflamos (es una buena practica y ahorramos recursos)
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_songs_adapter, parent, false);
        }

        // Asociamos cada variable a su elemento del layout
        TextView nombreCancion = (TextView) convertView.findViewById(R.id.album_adapter_tituloAlbum);
        TextView nombreGrupo = (TextView) convertView.findViewById(R.id.user_songs_band);
        ImageView imagenGrupo = (ImageView) convertView.findViewById(R.id.album_adapter_image);

        if(item.contains("-")){

            // Le damos la imagen de album transformada en redonda

            final Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(360)
                    .oval(false)
                    .build();

            Picasso.with(getContext()).load(item.split("-")[2]).fit().centerCrop().transform(transformation).into(imagenGrupo);

            nombreGrupo.setText(item.split("-")[1]);
            nombreCancion.setText(item.split("-")[0]);
        }
        else{
            nombreGrupo.setText(item);
            nombreCancion.setText("Canción desconocida");
        }

        return convertView; //Devolvemos la view ya rellena
    }
}

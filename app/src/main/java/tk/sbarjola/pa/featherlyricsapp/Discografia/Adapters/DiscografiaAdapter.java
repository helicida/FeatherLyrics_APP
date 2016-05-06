package tk.sbarjola.pa.featherlyricsapp.Discografia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Discografia.Item;
import tk.sbarjola.pa.featherlyricsapp.R;

/**
 * Created by sergi on 22/12/15.
 */
public class DiscografiaAdapter extends ArrayAdapter<Item> {

    public String BASE_URL =  "http://www.vagalume.com.br/";

    public DiscografiaAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Creamos el objeto en la posición correspondiente
        Item item = getItem(position);

        // Comprobamos si la view ya se ha usado antes, si no, la inflamos (es una buena practica y ahorramos recursos)
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.discografia_adapter_layout, parent, false);
        }

        // Asociamos cada variable a su elemento del layout
        TextView titular = (TextView) convertView.findViewById(R.id.user_artists_artistName);
        TextView anyo = (TextView) convertView.findViewById(R.id.discografia_anyoAlbum);
        ImageView imagenAlbum = (ImageView) convertView.findViewById(R.id.album_adapter);

        // Incorporamos los objetos al layout
        String urlImagen = BASE_URL + item.getCover();
        urlImagen.replace("W125", "W512");
        titular.setText(item.getDesc());
        anyo.setText(item.getPublished());
        Picasso.with(getContext()).load(urlImagen).fit().centerCrop().into(imagenAlbum);

        return convertView; //Devolvemos la view ya rellena
    }

}

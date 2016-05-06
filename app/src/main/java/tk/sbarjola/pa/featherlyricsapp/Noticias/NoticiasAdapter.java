package tk.sbarjola.pa.featherlyricsapp.Noticias;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.io.Serializable;
import java.util.List;

import tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Noticias.News;
import tk.sbarjola.pa.featherlyricsapp.R;

/**
 * Created by sergi on 18/12/15.
 */
public class NoticiasAdapter extends ArrayAdapter<News> implements Serializable {

    public NoticiasAdapter(Context context, int resource, List<News> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Creamos el objeto en la posición correspondiente
        News noticia = getItem(position);

        // Comprobamos si la view ya se ha usado antes, si no, la inflamos (es una buena practica y ahorramos recursos)
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.noticias_adapter_layout, parent, false);
        }

        // Asociamos cada variable a su elemento del layout
        TextView titular = (TextView) convertView.findViewById(R.id.list_titular);
        TextView fecha = (TextView) convertView.findViewById(R.id.list_fecha);
        ImageView imagenNoticias = (ImageView) convertView.findViewById(R.id.list_imagenNoticias);
        TextView description = (TextView) convertView.findViewById(R.id.list_descripcion);

        // Incorporamos los objetos al layout
        String urlImagen = noticia.getPicSrc();
        titular.setText(noticia.getHeadline());
        fecha.setText(noticia.getInserted());
        description.setText(noticia.getKicker());
        Picasso.with(getContext()).load(urlImagen).fit().centerCrop().into(imagenNoticias);

        return convertView; //Devolvemos la view ya rellena
    }
}
package tk.sbarjola.pa.featherlyricsapp.PerfilesUsuarios.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Url;

import tk.sbarjola.pa.featherlyricsapp.APIs.Spotify.ArtistSpotify;
import tk.sbarjola.pa.featherlyricsapp.R;

/**
 * Created by 46465442z on 28/04/16.
 */
public class userArtistsAdapter extends ArrayAdapter<String> {

    public userArtistsAdapter(Context context, int resource, List<String> name) {
        super(context, resource, name);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        // Creamos el objeto en la posición correspondiente
        final String item = getItem(position);
        final String artista = item.split("-")[0];
        final String URLimagen = item.split("-")[1];

        // Comprobamos si la view ya se ha usado antes, si no, la inflamos (es una buena practica y ahorramos recursos)
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_artists_adapter, parent, false);
        }

        // Asociamos cada variable a su elemento del layout
        TextView nombreArtista = (TextView) convertView.findViewById(R.id.user_artists_artistName);
        nombreArtista.setText(artista);

        // Le damos la iamgen
        ImageView imagenArtista = (ImageView) convertView.findViewById(R.id.album_adapter);
        Picasso.with(getContext())
                .load(URLimagen)
                .error(R.drawable.loading_image)
                .placeholder(R.drawable.progress_animation)
                .fit()
                .centerCrop()
                .into(imagenArtista);

        return convertView;
    }
}

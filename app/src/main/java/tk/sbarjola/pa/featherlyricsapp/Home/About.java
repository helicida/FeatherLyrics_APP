package tk.sbarjola.pa.featherlyricsapp.Home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import tk.sbarjola.pa.featherlyricsapp.R;

/**
 * Created by 46465442z on 28/04/16.
 */
public class About extends Fragment {

    ImageView alejandro;
    ImageView oriol;
    ImageView sergi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.toolbar_fragmentAbout);


        // Referencia a las vistas
        sergi = (ImageView) view.findViewById(R.id.imageSergi);
        oriol = (ImageView) view.findViewById(R.id.imageOriol);
        alejandro = (ImageView) view.findViewById(R.id.imageAlejandro);

        // Le damos la imagen de album transformada en redonda

        final Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadiusDp(360)
                .oval(false)
                .build();

        // Imagen sergi

        Picasso.with(getContext())
                .load("https://avatars3.githubusercontent.com/u/13574613?v=3&s=460")
                .error(R.drawable.loading_image)
                .placeholder(R.drawable.progress_animation)
                .fit().centerCrop()
                .transform(transformation)
                .into(sergi);

        // Imagen oriol

        Picasso.with(getContext())
                .load("https://avatars0.githubusercontent.com/u/14791455?v=3&s=460")
                .error(R.drawable.loading_image)
                .placeholder(R.drawable.progress_animation)
                .fit().centerCrop()
                .transform(transformation)
                .into(oriol);

        // Imagen alejandro

        Picasso.with(getContext())
                .load("https://avatars3.githubusercontent.com/u/14791459?v=3&s=460")
                .error(R.drawable.loading_image)
                .placeholder(R.drawable.progress_animation)
                .fit().centerCrop()
                .transform(transformation)
                .into(alejandro);


        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.aboutInfo) {

            showAbout();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){ //Afegim una opcio "Refresh" al menu del fragment
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.about, menu);
    }

    protected void showAbout() {
        // Inflate the about message contents
        View messageView = getActivity().getLayoutInflater().inflate(R.layout.about, null, false);

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        TextView textView = (TextView) messageView.findViewById(R.id.about_credits);
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.drawer_icon);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

}

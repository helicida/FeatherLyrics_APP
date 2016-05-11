package tk.sbarjola.pa.featherlyricsapp.Mapa;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import java.util.ArrayList;
import java.util.HashSet;
import tk.sbarjola.pa.featherlyricsapp.Firebase.FirebaseItem;
import tk.sbarjola.pa.featherlyricsapp.Firebase.FirebaseConfig;
import tk.sbarjola.pa.featherlyricsapp.Firebase.Usuario;
import tk.sbarjola.pa.featherlyricsapp.MainActivity;
import tk.sbarjola.pa.featherlyricsapp.R;


/**
 * Created by Sergi on 25/03/2016.
 */
public class OSMap extends Fragment {

    // Mapa
    private MapView map;                              // Mapa
    private IMapController mapController;             // Controlador del mapa

    // Elementos visuales del mapa
    private CompassOverlay brujulaOverlay;            // Brujula
    private ScaleBarOverlay escalaOverlay;            // Barra que indica la escala del mapa
    private MyLocationNewOverlay miPosicionOverlay;   // Marcador de donde nos encontramos
    private RadiusMarkerClusterer marcadoresMensajes; // Cluster de los marcadores de los mensajes
    private UserMarkerInfo userMarkerInfo;            // Envoltorio de usuarios
    FirebaseConfig config;                            // Configuración de firebase

    ArrayList<String> grupos = new ArrayList<>();        // Grupos escuchados por el usuario
    ArrayList<String> grupoContacto = new ArrayList<>(); // Grupo del otro contacto
    String gruposEnComun = "";                           // Grupos en común con otro contacto

    private class UserMarkerInfo extends MarkerInfoWindow {

        public UserMarkerInfo(int layoutResId, final MapView mapView) {
            super(layoutResId, mapView);
        }

        @Override
        public void onOpen(Object item) {
            final Marker marker = (Marker) item;
            final Usuario markerUsuario = (Usuario) marker.getRelatedObject();

            super.onOpen(item);

            closeAllInfoWindowsOn(map);

            RelativeLayout layout = (RelativeLayout) getView().findViewById(R.id.map_bubble_layout);

            layout.setClickable(true);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) getActivity()).setOpenedProfile(markerUsuario.getUID());
                    ((MainActivity) getActivity()).abrirPerfil();
                }
            });
        }
    }

    public OSMap() {
        // es necesario tener un constructor vacio
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setHasOptionsMenu(true);

        config = (FirebaseConfig) getActivity().getApplication();

        final Firebase referenciaMusicaUser = new Firebase(config.getReferenciaUsuarioLogeado().toString() + "/Artistas");

        // Descargamos la lista de usuarios
        referenciaMusicaUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                grupos.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    FirebaseItem grupo = userSnapshot.getValue(FirebaseItem.class);
                    grupos.add(userSnapshot.getKey());
                }

                //Al utilizar un HashSet se eliminan todos los duplicados y luego lo convertimos de nuevo a arrayList
                grupos = new ArrayList<String>(new HashSet<String>(grupos));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_osmap, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.toolbar_mapa);

        // Declaramos el mapa
        map = (MapView) view.findViewById(R.id.map);
        map.setTilesScaledToDpi(true);
        map.setTileSource(TileSourceFactory.MAPNIK);

        try{

            // Ajustamos el mapa con los controles, los elementos a mostrar y el zoom deseado
            ajustarMapa();

            // Ponemos los marcadores
            marcadoresMapa();

            map.invalidate();

            // Custom marker
            userMarkerInfo = new UserMarkerInfo(R.layout.user_map_item, map);
        }
        catch (IllegalStateException e){}

        return view;
    }

    private void marcadoresMapa(){

        try{
            // Declaramos un overlay con nuestra posicion
            miPosicionOverlay = new MyLocationNewOverlay(
                    getContext(),
                    new GpsMyLocationProvider(getContext()),
                    map
            );

            map.getOverlays().add(miPosicionOverlay);   // Situamos el marcador de nuestra posicion en el mapa

            miPosicionOverlay.enableMyLocation();   // Activamos la localizacion

            // Cuando se abra el mapa que nos lleve a nuestra localizacion
            miPosicionOverlay.runOnFirstFix(new Runnable() {
                public void run() {
                    mapController.animateTo(miPosicionOverlay.getMyLocation());
                }
            });

            // Rutas y nodos firebase

            // Creamos un cluster (es decir, la acumulación de mensajes en un mismo marcador)
            marcadoresMensajes = new RadiusMarkerClusterer(getContext());
            marcadoresMensajes.getTextPaint().setTextSize(60);
            map.getOverlays().add(marcadoresMensajes);

            // Le damos la imagen drawable que queremos que tenga
            Drawable clusterIconD = getResources().getDrawable(R.drawable.marcador_100x100);
            Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();

            // Y le definimos nuestra imagen y ajustamos el tamaño
            marcadoresMensajes.setIcon(clusterIcon);
            marcadoresMensajes.setRadius(100);
        }
        catch (IllegalStateException e){}

        config.getReferenciaListaUsuarios().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    final Usuario usuario;

                    // Mensaje que extraemos de Firebase
                    usuario = postSnapshot.getValue(Usuario.class);

                    final Firebase referenciaMusicaContacto = new Firebase(config.getReferenciaListaUsuarios().toString() + "/" + usuario.getKey() + "/Artistas");

                    gruposEnComun = "";

                    // Para evitar mostrar al propio usuario
                    if(!referenciaMusicaContacto.toString().equals(config.getReferenciaUsuarioLogeado().toString() + "/Artistas") && (usuario.getLatitud() != -1 && usuario.getLongitud() != -1)){

                        // Descargamos la lista de usuarios
                        referenciaMusicaContacto.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                gruposEnComun = "";

                                grupoContacto.clear();

                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    FirebaseItem grupo = userSnapshot.getValue(FirebaseItem.class);
                                    grupoContacto.add(userSnapshot.getKey());
                                }

                                //Al utilizar un HashSet se eliminan todos los duplicados y luego lo convertimos de nuevo a arrayList
                                grupoContacto = new ArrayList<String>(new HashSet<String>(grupoContacto));

                                // Comparamos los dos arrayList
                                for(int iterador = 0; iterador < grupos.size(); iterador++){
                                    for(int iterador2 = 0; iterador2 < grupoContacto.size(); iterador2++){

                                        // Evitamos los repetidos y añadimos grupos en comun
                                        if((grupos.get(iterador).contains(grupoContacto.get(iterador2)) || grupoContacto.get(iterador2).contains(grupos.get(iterador))) && !gruposEnComun.trim().contains(grupos.get(iterador).trim())){
                                            gruposEnComun = gruposEnComun + ", " + grupos.get(iterador);
                                        }
                                    }
                                }

                                try{
                                    // Marcamos todos los usuarios en común
                                    if(!gruposEnComun.equals("") && gruposEnComun != null){

                                        // Le damos la imagen drawable que queremos que tenga
                                        Drawable markerIconD = getResources().getDrawable(R.drawable.ic_person_pin_circle_deep_purple_a400_48dp);

                                        // Definimos el marcador y hacemos que nos marque la localización del mensaje
                                        final Marker marker = new Marker(map);
                                        GeoPoint point = new GeoPoint(usuario.getLatitud(), usuario.getLongitud());
                                        marker.setIcon(markerIconD);
                                        marker.setPosition(point);

                                        // Ajustamos el tamaño, la transparencia
                                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                                        // Le ponemos el título y la descripción
                                        marker.setInfoWindow(userMarkerInfo);
                                        marker.setTitle(usuario.getNombre() + " (" + usuario.getEdad() + ")");
                                        marker.setSnippet(usuario.getDescripcion());
                                        marker.setSubDescription(gruposEnComun.replaceFirst(",", "· "));
                                        marker.setRelatedObject(usuario);

                                        // Le damos la imagen de album transformada en redonda
                                        final Transformation transformation = new RoundedTransformationBuilder()
                                                .cornerRadiusDp(360)
                                                .oval(false)
                                                .build();

                                        Picasso.with(getContext()).load(usuario.getRutaImagen()).transform(transformation).into(new Target() {

                                            @Override
                                            public void onPrepareLoad(Drawable arg0) {


                                            }

                                            @Override
                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom arg1) {
                                                // TODO Create your drawable from bitmap and append where you like.

                                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                                marker.setImage(drawable);

                                            }

                                            @Override
                                            public void onBitmapFailed(Drawable arg0) {

                                                Drawable drawable = getResources().getDrawable(R.drawable.user_icon);
                                                marker.setImage(drawable);
                                            }
                                        });

                                        marcadoresMensajes.add(marker);
                                    }
                                }
                                catch (IllegalStateException e){}
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {}
                        });
                    }
                }
                marcadoresMensajes.invalidate();
                map.invalidate();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void ajustarMapa() {

        try{
            // Ajustamos los valores
            map.setTileSource(TileSourceFactory.MAPQUESTOSM);
            map.setTilesScaledToDpi(true);

            // Activamos el "pinzado" y el multitocuh
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);

            // Declaramos una escala del mapa
            escalaOverlay = new ScaleBarOverlay(map);

            // Lo situamos en el centro del mapa
            escalaOverlay.setCentred(true);

            // Hacemos calculos para que tome la dimension de pixeles correcta
            final DisplayMetrics dm = getResources().getDisplayMetrics();
            escalaOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

            // Declaramos la brujula
            brujulaOverlay = new CompassOverlay(
                    getContext(),
                    new InternalCompassOrientationProvider(getContext()),
                    map
            );

            brujulaOverlay.enableCompass();   // Activamos la brujula

            // Mostramos los elementos en el mapa
            map.getOverlays().add(this.escalaOverlay);      // Mostramos la barra con la escala
            map.getOverlays().add(this.brujulaOverlay);     // Mostramos la brujula

            // Le damos el zoom deseado
            setZoom(12);
        }
        catch (IllegalStateException e){}
    }

    private void setZoom(int zoom) {
        mapController = map.getController();
        mapController.setZoom(zoom);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){ //Afegim una opcio "Refresh" al menu del fragment
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.osmap, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.userPosition) {
            try {
                // Lleva a la posición del usuario
                if(map != null && miPosicionOverlay != null && mapController != null){
                    mapController.animateTo( miPosicionOverlay.getMyLocation());
                }
            }
            catch (NullPointerException ex){
                Toast.makeText(getContext(), "No se ha encontrado la posición", Toast.LENGTH_LONG).show();
                return true;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

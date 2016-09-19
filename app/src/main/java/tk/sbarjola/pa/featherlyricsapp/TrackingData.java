package tk.sbarjola.pa.featherlyricsapp;

/**
 * Created by Sergi on 19/09/2016.
 */

/**
 * Created by Sergi on 19/09/2016.
 */
public class TrackingData {

    private String searchedAlbumURL;                    // URL que contiene la imagen de la caratula del album
    private String discographyStart = "no artist";      // Nombre del artista que mostraremos en la sección de discografia
    private String playingArtist = "no artist";         // Nombre del artista de la canción en reproducción
    private String playingTrack = "no track";           // Nombre de la pista en reproducción
    private String searchedArtist = "no artist";        // Nombre del artista seleccionado en discografia
    private String searchedTrack = "no track";          // Nombre de la pista seleccionada en discografia
    private String openedProfile = "no profile";        // UID del perfil a buscar

    // Constructor

    public TrackingData(){

    }

    // Getter

    public String getSearchedAlbumURL() {
        return searchedAlbumURL;
    }

    public String getDiscographyStart() {
        return discographyStart;
    }

    public String getPlayingArtist() {
        return playingArtist;
    }

    public String getPlayingTrack() {
        return playingTrack;
    }

    public String getSearchedArtist() {
        return searchedArtist;
    }

    public String getSearchedTrack() {
        return searchedTrack;
    }

    public String getOpenedProfile() {
        return openedProfile;
    }

    // Setters

    public void setSearchedAlbumURL(String searchedAlbumURL) {
        this.searchedAlbumURL = searchedAlbumURL;
    }

    public void setDiscographyStart(String discographyStart) {
        this.discographyStart = discographyStart;
    }

    public void setPlayingArtist(String playingArtist) {
        this.playingArtist = playingArtist;
    }

    public void setPlayingTrack(String playingTrack) {
        this.playingTrack = playingTrack;
    }

    public void setSearchedArtist(String searchedArtist) {
        this.searchedArtist = searchedArtist;
    }

    public void setSearchedTrack(String searchedTrack) {
        this.searchedTrack = searchedTrack;
    }

    public void setOpenedProfile(String openedProfile) {
        this.openedProfile = openedProfile;
    }
}
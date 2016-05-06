package tk.sbarjola.pa.featherlyricsapp.APIs.Spotify;

/**
 * Created by sergi on 13/01/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ArtistSpotify {

    @SerializedName("artists")
    @Expose
    private Artists artists;

    /**
     *
     * @return
     * The artists
     */
    public Artists getArtists() {
        return artists;
    }

    /**
     *
     * @param artists
     * The artists
     */
    public void setArtists(Artists artists) {
        this.artists = artists;
    }

}

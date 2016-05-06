package tk.sbarjola.pa.featherlyricsapp.APIs.Lastfm;

/**
 * Created by 46465442z on 02/05/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtistInfo {

    @SerializedName("artist")
    @Expose
    private Artist artist;

    /**
     *
     * @return
     * The artist
     */
    public Artist getArtist() {
        return artist;
    }

    /**
     *
     * @param artist
     * The artist
     */
    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "ArtistInfo{" +
                "artist=" + artist +
                '}';
    }
}
package tk.sbarjola.pa.featherlyricsapp.APIs.Lastfm;

/**
 * Created by 46465442z on 02/05/16.
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Similar {

    @SerializedName("artist")
    @Expose
    private List<Artist_> artist = new ArrayList<Artist_>();

    /**
     *
     * @return
     * The artist
     */
    public List<Artist_> getArtist() {
        return artist;
    }

    /**
     *
     * @param artist
     * The artist
     */
    public void setArtist(List<Artist_> artist) {
        this.artist = artist;
    }

}
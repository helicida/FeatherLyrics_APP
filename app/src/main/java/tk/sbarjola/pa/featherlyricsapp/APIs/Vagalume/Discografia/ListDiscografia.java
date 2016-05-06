package tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Discografia;

/**
 * Created by sergi on 22/12/15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListDiscografia {

    @SerializedName("discography")
    @Expose
    private Discography discography;

    /**
     *
     * @return
     * The discography
     */
    public Discography getDiscography() {
        return discography;
    }

    /**
     *
     * @param discography
     * The discography
     */
    public void setDiscography(Discography discography) {
        this.discography = discography;
    }

}
package tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Discografia;

/**
 * Created by sergi on 22/12/15.
 */
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Discography {

    @SerializedName("artist")
    @Expose
    private Artist artist;
    @SerializedName("item")
    @Expose
    private List<Item> item = new ArrayList<Item>();

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

    /**
     *
     * @return
     * The item
     */
    public List<Item> getItem() {
        return item;
    }

    /**
     *
     * @param item
     * The item
     */
    public void setItem(List<Item> item) {
        this.item = item;
    }

}
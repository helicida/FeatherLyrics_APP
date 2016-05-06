package tk.sbarjola.pa.featherlyricsapp.APIs.Spotify;

/**
 * Created by sergi on 13/01/16.
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Item {

    @SerializedName("genres")
    @Expose
    private List<Object> genres = new ArrayList<Object>();
    @SerializedName("href")
    @Expose
    private String href;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("images")
    @Expose
    private List<Object> images = new ArrayList<Object>();
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("popularity")
    @Expose
    private Integer popularity;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("uri")
    @Expose
    private String uri;

    /**
     *
     * @return
     * The externalUrls
     */


    /**
     * @return The genres
     */
    public List<Object> getGenres() {
        return genres;
    }

    /**
     * @param genres The genres
     */
    public void setGenres(List<Object> genres) {
        this.genres = genres;
    }

    /**
     * @return The href
     */
    public String getHref() {
        return href;
    }

    /**
     * @param href The href
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The images
     */
    public List<Object> getImages() {
        return images;
    }

    /**
     * @param images The images
     */
    public void setImages(List<Object> images) {
        this.images = images;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The popularity
     */
    public Integer getPopularity() {
        return popularity;
    }

    /**
     * @param popularity The popularity
     */
    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri The uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

}

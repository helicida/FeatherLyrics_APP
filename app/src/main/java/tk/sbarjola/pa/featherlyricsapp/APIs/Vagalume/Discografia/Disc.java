package tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Discografia;

/**
 * Created by sergi on 22/12/15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Disc {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("desc")
    @Expose
    private String desc;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("turl")
    @Expose
    private String turl;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     *
     * @param desc
     * The desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The turl
     */
    public String getTurl() {
        return turl;
    }

    /**
     *
     * @param turl
     * The turl
     */
    public void setTurl(String turl) {
        this.turl = turl;
    }

}
package tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Noticias;

/**
 * Created by sergi on 20/12/15.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class News {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("headline")
    @Expose
    private String headline;
    @SerializedName("kicker")
    @Expose
    private String kicker;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("inserted")
    @Expose
    private String inserted;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("pic_src")
    @Expose
    private String picSrc;
    @SerializedName("pic_width")
    @Expose
    private String picWidth;
    @SerializedName("pic_height")
    @Expose
    private String picHeight;
    @SerializedName("pic_caption")
    @Expose
    private String picCaption;

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
     * The headline
     */
    public String getHeadline() {
        return headline;
    }

    /**
     *
     * @param headline
     * The headline
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     *
     * @return
     * The kicker
     */
    public String getKicker() {
        return kicker;
    }

    /**
     *
     * @param kicker
     * The kicker
     */
    public void setKicker(String kicker) {
        this.kicker = kicker;
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
     * The inserted
     */
    public String getInserted() {
        return inserted;
    }

    /**
     *
     * @param inserted
     * The inserted
     */
    public void setInserted(String inserted) {
        this.inserted = inserted;
    }

    /**
     *
     * @return
     * The modified
     */
    public String getModified() {
        return modified;
    }

    /**
     *
     * @param modified
     * The modified
     */
    public void setModified(String modified) {
        this.modified = modified;
    }

    /**
     *
     * @return
     * The picSrc
     */
    public String getPicSrc() {
        return picSrc;
    }

    /**
     *
     * @param picSrc
     * The pic_src
     */
    public void setPicSrc(String picSrc) {
        this.picSrc = picSrc;
    }

    /**
     *
     * @return
     * The picWidth
     */
    public String getPicWidth() {
        return picWidth;
    }

    /**
     *
     * @param picWidth
     * The pic_width
     */
    public void setPicWidth(String picWidth) {
        this.picWidth = picWidth;
    }

    /**
     *
     * @return
     * The picHeight
     */
    public String getPicHeight() {
        return picHeight;
    }

    /**
     *
     * @param picHeight
     * The pic_height
     */
    public void setPicHeight(String picHeight) {
        this.picHeight = picHeight;
    }

    /**
     *
     * @return
     * The picCaption
     */
    public String getPicCaption() {
        return picCaption;
    }

    /**
     *
     * @param picCaption
     * The pic_caption
     */
    public void setPicCaption(String picCaption) {
        this.picCaption = picCaption;
    }
}
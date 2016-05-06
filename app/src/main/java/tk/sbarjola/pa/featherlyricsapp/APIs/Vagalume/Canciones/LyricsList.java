package tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Canciones;

/**
 * Created by 46465442z on 21/12/15.
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LyricsList {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("art")
    @Expose
    private Art art;
    @SerializedName("mus")
    @Expose
    private List<Mu> mus = new ArrayList<Mu>();
    @SerializedName("badwords")
    @Expose
    private Boolean badwords;

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The art
     */
    public Art getArt() {
        return art;
    }

    /**
     *
     * @param art
     * The art
     */
    public void setArt(Art art) {
        this.art = art;
    }

    /**
     *
     * @return
     * The mus
     */
    public List<Mu> getMus() {
        return mus;
    }

    /**
     *
     * @param mus
     * The mus
     */
    public void setMus(List<Mu> mus) {
        this.mus = mus;
    }

    /**
     *
     * @return
     * The badwords
     */
    public Boolean getBadwords() {
        return badwords;
    }

    /**
     *
     * @param badwords
     * The badwords
     */
    public void setBadwords(Boolean badwords) {
        this.badwords = badwords;
    }

}
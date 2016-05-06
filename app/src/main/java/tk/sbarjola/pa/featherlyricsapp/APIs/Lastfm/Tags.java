package tk.sbarjola.pa.featherlyricsapp.APIs.Lastfm;

/**
 * Created by 46465442z on 02/05/16.
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tags {

    @SerializedName("tag")
    @Expose
    private List<Tag> tag = new ArrayList<Tag>();

    /**
     *
     * @return
     * The tag
     */
    public List<Tag> getTag() {
        return tag;
    }

    /**
     *
     * @param tag
     * The tag
     */
    public void setTag(List<Tag> tag) {
        this.tag = tag;
    }

}
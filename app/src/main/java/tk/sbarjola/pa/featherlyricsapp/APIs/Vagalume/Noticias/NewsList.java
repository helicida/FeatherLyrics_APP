package tk.sbarjola.pa.featherlyricsapp.APIs.Vagalume.Noticias;

/**
 * Created by sergi on 20/12/15.
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class NewsList {

    @SerializedName("news")
    @Expose
    private List<News> news = new ArrayList<News>();

    /**
     *
     * @return
     * The news
     */
    public List<News> getNews() {
        return news;
    }

    /**
     *
     * @param news
     * The news
     */
    public void setNews(List<News> news) {
        this.news = news;
    }

}
package tk.sbarjola.pa.featherlyricsapp.Firebase;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by sergi on 27/03/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FirebaseItem {

    // String
    private String itemUrl; // Generos que escucha el usuario

    // Constructores

    public FirebaseItem(){}

    // Getters

    public String getItemUrl() {
        return itemUrl;
    }

    //  Setters

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }
}

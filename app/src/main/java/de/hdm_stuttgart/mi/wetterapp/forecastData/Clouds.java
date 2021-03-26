
package de.hdm_stuttgart.mi.wetterapp.forecastData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Clouds {

    @SerializedName("all")
    @Expose
    public int all;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Clouds() {
    }

    /**
     * 
     * @param all
     */
    public Clouds(int all) {
        super();
        this.all = all;
    }

}

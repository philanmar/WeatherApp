
package de.hdm_stuttgart.mi.wetterapp.forecastData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wind {

    @SerializedName("speed")
    @Expose
    public double speed;
    @SerializedName("deg")
    @Expose
    public float deg;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Wind() {
    }

    /**
     * 
     * @param deg
     * @param speed
     */
    public Wind(double speed, float deg) {
        super();
        this.speed = speed;
        this.deg = deg;
    }

}

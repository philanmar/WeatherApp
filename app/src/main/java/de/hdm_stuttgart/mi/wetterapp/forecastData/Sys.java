
package de.hdm_stuttgart.mi.wetterapp.forecastData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sys {

    @SerializedName("pod")
    @Expose
    public String pod;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Sys() {
    }

    /**
     * 
     * @param pod
     */
    public Sys(String pod) {
        super();
        this.pod = pod;
    }

}

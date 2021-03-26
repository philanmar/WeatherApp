
package de.hdm_stuttgart.mi.wetterapp.forecastData;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class List {

    @SerializedName("dt")
    @Expose
    public int dt;
    @SerializedName("main")
    @Expose
    public Main main;
    @SerializedName("weather")
    @Expose
    public java.util.List<Weather> weather = new ArrayList<Weather>();
    @SerializedName("clouds")
    @Expose
    public Clouds clouds;
    @SerializedName("wind")
    @Expose
    public Wind wind;
    @SerializedName("rain")
    @Expose
    public Rain rain;
    @SerializedName("sys")
    @Expose
    public Sys sys;
    @SerializedName("dt_txt")
    @Expose
    public String dtTxt;

    /**
     * No args constructor for use in serialization
     * 
     */
    public List() {
    }

    /**
     * 
     * @param dt
     * @param rain
     * @param dtTxt
     * @param weather
     * @param main
     * @param clouds
     * @param sys
     * @param wind
     */

    public List(int dt, Main main, java.util.List<Weather> weather, Clouds clouds, Wind wind, Rain rain, Sys sys, String dtTxt) {
        super();
        this.dt = dt;
        this.main = main;
        this.weather = weather;
        this.clouds = clouds;
        this.wind = wind;
        this.rain = rain;
        this.sys = sys;
        this.dtTxt = dtTxt;
    }

}

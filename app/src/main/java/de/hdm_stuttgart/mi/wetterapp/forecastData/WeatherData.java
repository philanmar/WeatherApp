
package de.hdm_stuttgart.mi.wetterapp.forecastData;

import java.util.ArrayList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherData {

    @SerializedName("cod")
    @Expose
    public String cod;
    @SerializedName("message")
    @Expose
    public double message;
    @SerializedName("cnt")
    @Expose
    public int cnt;
    @SerializedName("list")
    @Expose
    public java.util.List<de.hdm_stuttgart.mi.wetterapp.forecastData.List> list = new ArrayList<de.hdm_stuttgart.mi.wetterapp.forecastData.List>();
    @SerializedName("city")
    @Expose
    public City city;

    /**
     * No args constructor for use in serialization
     * 
     */
    public WeatherData() {
    }

    /**
     * 
     * @param city
     * @param cnt
     * @param cod
     * @param message
     * @param list
     */
    public WeatherData(String cod, double message, int cnt, java.util.List<de.hdm_stuttgart.mi.wetterapp.forecastData.List> list, City city) {
        super();
        this.cod = cod;
        this.message = message;
        this.cnt = cnt;
        this.list = list;
        this.city = city;
    }

}

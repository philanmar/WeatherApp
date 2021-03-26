
package de.hdm_stuttgart.mi.wetterapp.forecastData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class City {

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("coord")
    @Expose
    public Coord coord;
    @SerializedName("country")
    @Expose
    public String country;

    /**
     * No args constructor for use in serialization
     *
     */
    public City() {
    }

    /**
     *
     * @param country
     * @param coord
     * @param name
     * @param id
     */
    public City(int id, String name, Coord coord, String country) {
        super();
        this.id = id;
        this.name = name;
        this.coord = coord;
        this.country = country;
    }

}

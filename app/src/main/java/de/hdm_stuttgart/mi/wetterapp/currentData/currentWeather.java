package de.hdm_stuttgart.mi.wetterapp.currentData;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import de.hdm_stuttgart.mi.wetterapp.currentData.Clouds;
import de.hdm_stuttgart.mi.wetterapp.currentData.Coord;
import de.hdm_stuttgart.mi.wetterapp.currentData.Main;
import de.hdm_stuttgart.mi.wetterapp.currentData.Rain;
import de.hdm_stuttgart.mi.wetterapp.currentData.Sys;
import de.hdm_stuttgart.mi.wetterapp.currentData.Weather;
import de.hdm_stuttgart.mi.wetterapp.currentData.Wind;

public class currentWeather {
    @SerializedName("coord")
    public Coord coord;
    @SerializedName("sys")
    public Sys sys;
    @SerializedName("weather")
    public ArrayList<Weather> weather = new ArrayList<>();
    @SerializedName("main")
    public Main main;
    @SerializedName("wind")
    public Wind wind;
    @SerializedName("rain")
    public Rain rain;
    @SerializedName("clouds")
    public Clouds clouds;
    @SerializedName("dt")
    public float dt;
    @SerializedName("id")
    public int id;
    @SerializedName("name")
    public String name;
    @SerializedName("cod")
    public float cod;
}


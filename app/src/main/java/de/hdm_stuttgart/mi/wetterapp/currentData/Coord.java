package de.hdm_stuttgart.mi.wetterapp.currentData;

import com.google.gson.annotations.SerializedName;

class Coord {
    @SerializedName("lon")
    public float lon;
    @SerializedName("lat")
    public float lat;
}

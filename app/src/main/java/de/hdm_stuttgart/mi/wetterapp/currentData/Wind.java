package de.hdm_stuttgart.mi.wetterapp.currentData;

import com.google.gson.annotations.SerializedName;

public class Wind {
    @SerializedName("speed")
    public float speed;
    @SerializedName("deg")
    public float deg;
}

package de.hdm_stuttgart.mi.wetterapp;

import de.hdm_stuttgart.mi.wetterapp.currentData.currentWeather;
import de.hdm_stuttgart.mi.wetterapp.forecastData.WeatherData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface weatherAPI {


    //Gets current Weather via Cityname (for the UserInput based search)
    @GET("data/2.5/weather?")
    Call<currentWeather> getCurrentCityWeather(@Query("q") String cityName, @Query("lang") String language,
                                            @Query("units") String unit, @Query("APPID") String app_id);

    //Gets the Weatherforecaste via Cityname-input
    @GET("data/2.5/forecast?")
    Call<WeatherData> getForecastCityWeather(@Query("q") String cityName, @Query("lang") String language,
                                            @Query("units") String unit, @Query("APPID") String app_id);

    //Gets current Weather via GPS coordinates
    @GET("data/2.5/weather?")
    Call<currentWeather> getCurrentGPSWeather(@Query("lat") String lat, @Query("lon") String lon,
                                            @Query("lang") String language, @Query("units") String unit, @Query("APPID") String app_id);

    //Gets WEather Forecast via GPS coordinates
    @GET("data/2.5/forecast?")
    Call<WeatherData> getForecastGPSWeather(@Query("lat") String lat, @Query("lon") String lon,
                                            @Query("lang") String language, @Query("units") String unit, @Query("APPID") String app_id);

    //Gets current Weather Via ID (used for the saved favorites of the user)
    @GET("data/2.5/weather?")
    Call<currentWeather> getCurrentIDWeather(@Query("id") String id,
                                             @Query("lang") String language, @Query("units") String unit, @Query("APPID") String app_id);

    //Get weather forecast via ID (same as above)
    @GET("data/2.5/forecast?")
    Call<WeatherData> getForecastIDWeather(@Query("id") String id,
                                            @Query("lang") String language, @Query("units") String unit, @Query("APPID") String app_id);

}



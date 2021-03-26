package de.hdm_stuttgart.mi.wetterapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import de.hdm_stuttgart.mi.wetterapp.currentData.currentWeather;
import de.hdm_stuttgart.mi.wetterapp.forecastData.WeatherData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final static String BaseUrl = "https://api.openweathermap.org/";
    private final static String AppId = "abee831fc74974e432232f475cb73ea5";

    private static double lat;
    private static double lon;
    private static String unit = "metric";  // Set used Units to Metric, so, Output via API will be in C°

    protected String cityName = "";
    protected String cityID = "";
    protected int LOCATION_CODE = 1;

    private ListView favoriteList;
    private RecyclerView forecastRecyclerView;
    protected Map<String, String> favoriteMap = new HashMap<>();    // HashMap to save Favorites with ID as Key
                                                                    // and Name as Value.
                                                                    // This Way there's no Problem with duplicates
    protected DrawerLayout favoriteDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private String activityTitle;
    protected ImageView iconImageView;
    protected ImageButton favButton;
    protected EditText cityInput;

    private Helper helper = new Helper();      //Initialize the Helper Class

    protected TextView cityTextView, currentTempTextView, weatherDescriptionTextView, minTempTextview, maxTempTextview,
            humidityTextView, windSpeedTextView, windDirectionTextView, currentWeatherText, forecastText;

    gpsHelper gpsHelper = new gpsHelper(MainActivity.this);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    weatherAPI GPSWeather = retrofit.create(weatherAPI.class);
    weatherAPI CityWeather = retrofit.create(weatherAPI.class);



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //Force Portrait Mode for the Application

        findViewById(R.id.progress).setVisibility(View.GONE);   //Hide ProgressBar on Application start


        //Initialization of used TextViews
        cityTextView = findViewById(R.id.cityNameTextView);
        currentTempTextView = findViewById(R.id.temperatureTextView);
        minTempTextview = findViewById(R.id.minTempTextview);
        maxTempTextview = findViewById(R.id.maxTempTextview);
        humidityTextView = findViewById(R.id.humidityTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);
        windDirectionTextView = findViewById(R.id.windDirectionTextView);
        weatherDescriptionTextView = findViewById(R.id.weatherDescriptionTextView);
        currentWeatherText = findViewById(R.id.currentWeatherText);
        forecastText = findViewById(R.id.foreCastText);


        //Initialization of other UI Elements
        iconImageView = findViewById(R.id.iconImageView);
        favoriteList = findViewById(R.id.navList);
        favoriteDrawerLayout = findViewById(R.id.drawer_layout);
        favButton = findViewById(R.id.favoriteButton);
        forecastRecyclerView = findViewById(R.id.forecastRecyclerview);


        // hide weatherIcon on startup
        iconImageView.setVisibility(View.GONE);

        //Get Application Name as String
        activityTitle = getTitle().toString();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        forecastRecyclerView.setLayoutManager(layoutManager);

        // Snaphelper used to make recyclerview items to screen, so if user isn't toching the display, always one full
        // Recyclerview Item is shown on the screen
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(forecastRecyclerView);

        findViewById(R.id.weatherLayout).setVisibility(View.GONE);      //Hide WeatherLayout on Application-Start


        // Retrieves saved Favorites from the SharedPreferences if there are any, otherwise the favorite Drawer will remain empty
        if(helper.getFavoriteHashMap(this)!=null){
            favoriteMap = helper.getFavoriteHashMap(this);
            drawerItems();

        }

        setupDrawer();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //Check if Application has Permission to use Location Services. If not, request permission from User
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            helper.requestLocationPermissionFromUser(MainActivity.this);
        }


        // Lookingglas Icon reacts on click and searches for entered city
        findViewById(R.id.citySearchButton).setOnClickListener(v -> {

            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            cityInputSearch();
        });


        //OnKeyListener for UserInput, so pressing the Enter button on the Keyboard starts a Search via entered City-Name
        findViewById(R.id.citynameInput).setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        cityInputSearch();
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });


        // FloatingActionButton with Location Icon searches at click for Weather at current position
        findViewById(R.id.gpsButton).setOnClickListener(view -> {
            findViewById(R.id.weatherLayout).setVisibility(View.GONE);
            getCurrentDataWithGPS();
            getForecastDataWithGPS();
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            helper.checkFavButtonAndCityName(MainActivity.this);
        });

        /*  Heart Icon reacts on clicks and saves currently shown city in Favorite List, so User can
        *   simply use favorite drawer to look up weather at specific locations
        */
        favButton.setOnClickListener(view -> {
            if (cityID.length() > 1) {
                favoriteMap.put(cityID, cityName);
                drawerItems();
                helper.saveFavoriteHashMap(favoriteMap, MainActivity.this);
                helper.checkFavButtonAndCityName(MainActivity.this);

            }
        });
    }


    /**
     * Searches for the cityname, which was entered by user in the cityInput-EditText
     * Also checks, if API can find WeatherData for given Input. If theres no data,
     * hide the last Weatherdata-Output and show entered text as Hint in the EditText-field
     */
    protected void cityInputSearch(){

        cityInput = findViewById(R.id.citynameInput);
        cityName = String.valueOf(cityInput.getText());

        if (cityName.equals("")) {
            cityInput.setHint("Stadt eingeben");
            findViewById(R.id.weatherLayout).setVisibility(View.GONE);       //Hide last shown Weather-Data
        }
        else
        {
            cityInput.setHint("Stadt eingeben");    //Set last Entry as Hint, so user can see what was entered
        }
        cityInput.setText("");      //Remove entry, so that only hint is shown and User doesn't has to remove his last input by himself

        if (cityName.length()>0) {  //search only for a City, if there is any Input
            findViewById(R.id.weatherLayout).setVisibility(View.GONE);
            getCurrentDataWithCityName();
            getForecastDataWithCityName();
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Berechtigung gewährt", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Berechtigungen verweigert", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * Retrieves the current Weatherdata from Openweathermap by using GPS Coordinates
     *
     */
    private void getCurrentDataWithGPS() {

        findViewById(R.id.weatherLayout).setVisibility(View.GONE);      //Hide last Weatherdata, so Progressbar is shown correctly
        gpsHelper.getLocation();

        // Get Location Data
        lat = gpsHelper.getLatitude();
        lon = gpsHelper.getLongitude();

        Call<currentWeather> call = GPSWeather.getCurrentGPSWeather(Double.toString(lat),
                Double.toString(lon), "de", unit, AppId);


        call.enqueue(new Callback<currentWeather>() {
            @Override
            public void onResponse(@NonNull Call<currentWeather> call, @NonNull Response<currentWeather> response) {

                if (response.code() == 200) {

                    currentWeather weatherResponse = response.body();
                    assert weatherResponse != null;

                    showCurrentData(weatherResponse);
                    findViewById(R.id.progress).setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(@NonNull Call<currentWeather> call, @NonNull Throwable t) {
                helper.showNoConnectionDialog(MainActivity.this);
                Log.e("No Connection", t.toString());
            }
        });
    }


    private void getForecastDataWithGPS() {

        /*  No new Location Request needed, since current Weather GPS Data, already makes a request
        *   Latitude and Longitude should be the same for Current Weather and the Forecast
        */

        findViewById(R.id.weatherLayout).setVisibility(View.GONE);      //Hide last Weatherdata, so Progressbar is shown correctly
        Call<WeatherData> call = GPSWeather.getForecastGPSWeather(Double.toString(lat),
                Double.toString(lon), "de", unit, AppId);

        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(@NonNull Call<WeatherData> call, @NonNull Response<WeatherData> response) {
                if (response.code() == 200) {
                    WeatherData weatherResponse = response.body();
                    assert weatherResponse != null;

                    showForecastData(weatherResponse);

                    // Get ID and name of city, so that user can save them
                    cityID = String.valueOf(weatherResponse.city.id);
                    cityName = String.valueOf(weatherResponse.city.name);

                    findViewById(R.id.progress).setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherData> call, @NonNull Throwable t) {

                //Already handled in getCurrentDataWithGPS(), if currentData gets Connection Error, then Forecast-Data most
                // likely gets the same error, so it is just needed to handle it once
            }
        });
    }




    /**
     * Retrieves the current Weatherdata from OpenWeathermap by using a City Name
     *
     * If no city with given input is found, show an AlertDialog to inform the user
     * This is only needed in one of the Functions needed, where user searches via cityname-input
     */
    private void getCurrentDataWithCityName() {

        findViewById(R.id.weatherLayout).setVisibility(View.GONE);      //Hide last Weatherdata, so Progressbar is shown correctly
        Call<currentWeather> call = CityWeather.getCurrentCityWeather(cityName,"de", unit, AppId);

        call.enqueue(new Callback<currentWeather>() {
            @Override
            public void onResponse(@NonNull Call<currentWeather> call, @NonNull Response<currentWeather> response) {

                if (response.code() == 200) {

                    currentWeather weatherResponse = response.body();
                    assert weatherResponse != null;

                    showCurrentData(weatherResponse);
                    findViewById(R.id.progress).setVisibility(View.GONE);

                }
                else{
                    helper.showNoCityFoundDialog(MainActivity.this, cityName);
                }
            }

            @Override
            public void onFailure(@NonNull Call<currentWeather> call, @NonNull Throwable t) {
                helper.showNoConnectionDialog(MainActivity.this);
                Log.e("No Connection", t.toString());
            }
        });
    }

    /**
     * Retrieves the forecast Weatherdata from OpenWeathermap by using a City Name
     */
    private void getForecastDataWithCityName() {

        findViewById(R.id.weatherLayout).setVisibility(View.GONE);      //Hide last Weatherdata, so Progressbar is shown correctly
        Call<WeatherData> call = CityWeather.getForecastCityWeather(cityName,"de", unit, AppId);

        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(@NonNull Call<WeatherData> call, @NonNull Response<WeatherData> response) {

                if (response.code() == 200) {

                    WeatherData weatherResponse = response.body();
                    assert weatherResponse != null;

                    showForecastData(weatherResponse);

                    // Get ID and name of city, so that user can save them
                    cityID = String.valueOf(weatherResponse.city.id);
                    cityName = String.valueOf(weatherResponse.city.name);
                    findViewById(R.id.progress).setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherData> call, @NonNull Throwable t) {

                //Already handled in getCurrentDataWithCityName(), if currentData gets Connection Error, then Forecast-Data most
                // likely gets the same error, so it is just needed to handle it once

            }
        });
    }


    /**
     * @param id Retrieves the City-ID from the Favorites
     *
     *  Get the current Weather Data via City-ID, which are saved in the SharedPreferences
     */
    private void getCurrentDataWithCityID(String id) {

        findViewById(R.id.weatherLayout).setVisibility(View.GONE);      //Hide last Weatherdata, so Progressbar is shown correctly
        Call<currentWeather> call = CityWeather.getCurrentIDWeather(id,"de", unit, AppId);

        call.enqueue(new Callback<currentWeather>() {
            @Override
            public void onResponse(@NonNull Call<currentWeather> call, @NonNull Response<currentWeather> response) {

                if (response.code() == 200) {

                    currentWeather weatherResponse = response.body();
                    assert weatherResponse != null;
                    showCurrentData(weatherResponse);
                    findViewById(R.id.progress).setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(@NonNull Call<currentWeather> call, @NonNull Throwable t) {
                helper.showNoConnectionDialog(MainActivity.this);
                Log.e("No Connection", t.toString());
            }
        });
    }

    /**
     * @param id Retrieves the City-ID from the Favorites
     *
     *  Get the Weatherforecast Data via City-ID, which are saved in the SharedPreferences
     */
    protected void getForecastDataWithID(String id) {
        findViewById(R.id.weatherLayout).setVisibility(View.GONE);

        Call<WeatherData> call = GPSWeather.getForecastIDWeather(id,"de", unit, AppId);

        call.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(@NonNull Call<WeatherData> call, @NonNull Response<WeatherData> response) {
                if (response.code() == 200) {
                    WeatherData weatherResponse = response.body();
                    assert weatherResponse != null;

                    showForecastData(weatherResponse);
                    findViewById(R.id.progress).setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherData> call, @NonNull Throwable t) {
                //Already handled in getCurrentDataWithCityID(), if currentData gets Connection Error, then Forecast-Data most
                // likely gets the same error, so it is just needed to handle it once
            }
        });
    }


    /**
     * Fills the Drawer Menu with Entrys from the saved Favorite List
     * and manages clicks on saved Favorites, so that the Weather for clicked city will be shown.
     * Is called every time, something at the Favorite List is changed (either add or delete Favorite)
     */
    private void drawerItems() {

        // Sorts Favorite List, in case user adds a city to favorites, so it doesn't appear on a random position in the list
        favoriteMap = helper.sortByValue(favoriteMap);

        final List<String> favoriteCitynameList = new ArrayList<>(favoriteMap.values());
        final List<String> favoriteCityIDList = new ArrayList<>(favoriteMap.keySet());
        String[] favoriteList = favoriteCitynameList.toArray(new String[0]);

        cityInput = findViewById(R.id.citynameInput);
        ArrayAdapter<String> drawerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favoriteList);
        this.favoriteList.setAdapter(drawerAdapter);

        // select Favorite on short click and show weatherdata for given city
        this.favoriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               getCurrentDataWithCityID(favoriteCityIDList.get(position));
               getForecastDataWithID(favoriteCityIDList.get(position));
               findViewById(R.id.progress).setVisibility(View.VISIBLE);
               cityInput.setHint("Stadt eingeben");
               favoriteDrawerLayout.closeDrawers();

            }
        });


        // Makes it posible to delete favorites from list by long click on Favorite
        // shows Dialog first, to confirm User really wants to remove favorite
        this.favoriteList.setOnItemLongClickListener((adapterView, view, i, l) -> {

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(favoriteCitynameList.get(i) + " löschen?")
                    .setIcon(R.drawable.ic_delete_black_36dp)
                    .setMessage("Möchtest du " + favoriteCitynameList.get(i) + " aus deinen Favoriten entfernen?")
                    .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            favoriteMap.remove(favoriteCityIDList.get(i));
                            helper.saveFavoriteHashMap(favoriteMap, MainActivity.this);
                            drawerItems();
                        }
                    })
                    .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            return true;
        });
    }

    //Initializes the Favorite-Drawer
    private void setupDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, favoriteDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            // Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Objects.requireNonNull(getSupportActionBar()).setTitle("Favoriten");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

            }

            // Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                Objects.requireNonNull(getSupportActionBar()).setTitle(activityTitle);
                helper.checkFavButtonAndCityName(MainActivity.this);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);
        favoriteDrawerLayout.addDrawerListener(drawerToggle);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menumain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * @param weatherData Retrieves the Data from the API call and updates the UI with the new Data
     */
    private void showForecastData(WeatherData weatherData){
        RecyclerView.Adapter forecastAdapter = new ForecastAdapter(weatherData);
        forecastRecyclerView.setAdapter(forecastAdapter);
    }


    /**
     * @param weatherData Retrieves the Weather POJO filled with all the Weather Data
     * Fills all the TextViews with the Data from the WeatherData POJO
     */
    private void showCurrentData(currentWeather weatherData){
        String cityString = weatherData.name + ", " + weatherData.sys.country;
        String temperature = "Aktuelle Temperatur: " + weatherData.main.temp + "C°";
        String maxTemp = "Maximaltemperatur: " + Math.round(weatherData.main.temp_max) + "C°";
        String minTemp = "Minimaltemperatur: " + Math.round(weatherData.main.temp_min) + "C°";
        String humidity = "Luftfeuchtigkeit: " + weatherData.main.humidity + "%";
        String windSpeed = "Windgeschwindigkeit: " + helper.meterPerSecondToKilometerPerHour(weatherData.wind.speed) + "km/h";
        String windDirection = "Windrichtung: " + helper.getWindDirection(weatherData.wind.deg);

        currentTempTextView.setText(temperature);
        minTempTextview.setText(minTemp);
        maxTempTextview.setText(maxTemp);
        humidityTextView.setText(humidity);
        windSpeedTextView.setText(windSpeed);
        windDirectionTextView.setText(windDirection);
        weatherDescriptionTextView.setText(weatherData.weather.get(0).description);
        helper.chooseWeatherIcon(weatherData.weather.get(0).id, weatherData.main.temp, this);
        cityTextView.setText(cityString);

        cityID = String.valueOf(weatherData.id);
        cityName = String.valueOf(weatherData.name);
        findViewById(R.id.weatherLayout).setVisibility(View.VISIBLE);
        helper.checkFavButtonAndCityName(this);
    }


}
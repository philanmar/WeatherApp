package de.hdm_stuttgart.mi.wetterapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class Helper extends AppCompatActivity {


    /**
     * @param iconID An weather-ID-code from the API-Call, which will be converted to the right Icon for the Weather
     * @param mainActivity Link to MainActivity, otherwise there would be no access to all the layout elements
     */
    /*
    * Function recieves an ID from the API Call (iconText)
    * and changes the WeatherIcon to the right one
    *
    * If theres a problem, default is a placeholder symbol, which will not be shown
    * */
    protected void chooseWeatherIcon(int iconID, double temp, MainActivity mainActivity) {
        mainActivity.iconImageView.setVisibility(View.VISIBLE);
        Log.d("Icon-ID: ", iconID + " ");
        switch (iconID) {

            case(200): case(201): case(202): case(210): case(211): case(212): case(221):
            case(230): case (231): case(232):
                mainActivity.iconImageView.setImageResource(R.drawable.w00);
                break;

            case(300): case(301): case(500):
                mainActivity.iconImageView.setImageResource(R.drawable.w11);
                break;

            case(611): case(616):
                mainActivity.iconImageView.setImageResource(R.drawable.w05);
                break;

            case(311): case(312): case(310): case(501): case (521):
                mainActivity.iconImageView.setImageResource(R.drawable.w09);
                break;

            case(313): case(314): case(321): case(502): case(520):
            case(302):
                mainActivity.iconImageView.setImageResource(R.drawable.w12);
                break;

            case (503): case (504): case (522): case (531):
                mainActivity.iconImageView.setImageResource(R.drawable.w02);
                break;

            case (511):
                mainActivity.iconImageView.setImageResource(R.drawable.w07);
                break;

            case (600):
                mainActivity.iconImageView.setImageResource(R.drawable.w18);
                break;

            case (620):
                mainActivity.iconImageView.setImageResource(R.drawable.w13);
                break;

            case (601): case (621):
                mainActivity.iconImageView.setImageResource(R.drawable.w14);
                break;

            case (602): case (622):
                mainActivity.iconImageView.setImageResource(R.drawable.w16);
                break;

            case (612): case (615):
                mainActivity.iconImageView.setImageResource(R.drawable.w06);
                break;

            case (701): case (702): case (711): case (741): case (721): case (751):
            case (761):
                mainActivity.iconImageView.setImageResource(R.drawable.w20);
                break;

            case (731): case (781): case (771):
                mainActivity.iconImageView.setImageResource(R.drawable.w23);
                break;

            case (800):

                if (isNight(getCurrentTimeString())) {

                    mainActivity.iconImageView.setImageResource(R.drawable.w31);
                } else {

                    // Check if it's going to be very hot (over 30C°), if yes, show special Sun Icon
                    if(temp>30){
                        mainActivity.iconImageView.setImageResource(R.drawable.w36);
                    }
                    else {
                        mainActivity.iconImageView.setImageResource(R.drawable.w32);
                    }
                }
                break;

            case (801):

                if (isNight(getCurrentTimeString())) {

                    mainActivity.iconImageView.setImageResource(R.drawable.w33);
                } else {

                    mainActivity.iconImageView.setImageResource(R.drawable.w34);
                }
                break;

            case (802):
                if (isNight(getCurrentTimeString())) {

                    mainActivity.iconImageView.setImageResource(R.drawable.w29);
                } else {

                    mainActivity.iconImageView.setImageResource(R.drawable.w30);
                }
                break;

            case(803):
                if(isNight(getCurrentTimeString())) {

                    mainActivity.iconImageView.setImageResource(R.drawable.w27);
                }
                else{

                    mainActivity.iconImageView.setImageResource(R.drawable.w28);
                }
                break;

            case (804):
                mainActivity.iconImageView.setImageResource(R.drawable.w26);
                break;


            //If theres a Weather-Code, which isn't coded here, set a placeholder-icon and hide it instantly
            default:
                mainActivity.iconImageView.setImageResource(R.drawable.w44);
                mainActivity.iconImageView.setVisibility(View.GONE);

        }
    }


    /**
     * Checks, if a city is already saved in the favorites
     * If there is an entry, change the heart-Icon, if not, use the empty heart icon
     *
     * Also checks, if the CityName is "Earth". That can happen, when GPS was activated seconds before
     * searching by GPS. If the Name is "Earth", than no WeatherData shall be shown, but an Alert is shown,
     * that something went wrong
     *
     * @param mainActivity Link to MainActivity, otherwise there would be no access to all the layout elements
     */
    protected void checkFavButtonAndCityName(MainActivity mainActivity){

          if (mainActivity.favoriteMap.containsKey(mainActivity.cityID)){
             mainActivity.favButton.setImageResource(R.drawable.ic_favorite_36dp);
          }
          else{
              mainActivity.favButton.setImageResource(R.drawable.ic_favorite_border_36dp);
          }

          if(mainActivity.cityName.equals("Earth") | mainActivity.cityName.equals("Erde")){
              mainActivity.findViewById(R.id.weatherLayout).setVisibility(View.GONE);

              AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
              alertDialog.setIcon(R.drawable.ic_location_disabled_black_36dp);
              alertDialog.setTitle("Standort nicht abrufbar");
              alertDialog.setMessage("Es gab leider Probleme und wir konnten deinen Standort nicht feststellen.\n" +
                      "Bitte versuche es später nochmal oder gib eine Stadt ein.");
              alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                      new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int which) {
                              dialog.dismiss();
                          }
                      });
              alertDialog.show();
          }
        }


    /**
     * @param mainActivity Link to Mainactivity, otherwise there is no acces to layout Elements
     *
     * Function provides an User-Dialog, if there is a problem with getting Data from the API,
     * for example if the Device has no Internet-Connection
     */
        protected void showNoConnectionDialog(MainActivity mainActivity){

            mainActivity.findViewById(R.id.weatherLayout).setVisibility(View.GONE);
            mainActivity.findViewById(R.id.progress).setVisibility(View.GONE);

            AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
            alertDialog.setIcon(R.drawable.ic_error_36dp);
            alertDialog.setTitle("Probleme mit der Verbindung");
            alertDialog.setMessage("Es konnten leider keine Wetterdaten abgerufen werden\n" +
                    "Bitte überprüfe deine Internetverbindung und versuche es erneut.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    /**
     * @param obj The HashMap that shall be saved in the SharedPreferences
     * @param mainActivity Link to MainActivity, otherwise there would be no access to all the layout elements
     *
     * Puts the saved favorites in a HashMap, this way we don't have to deal with duplicate Entrys :)
     * Also saves the Favorites in the sharedPreferences of the device, after they are converted into json format
     */
    protected void saveFavoriteHashMap(Object obj, MainActivity mainActivity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString("favorites",json);
        editor.apply();     // This line commits the Data to the SharedPreferences
    }


    /**
     * @param mainActivity  Link to MainActivity, otherwise it wouldn't work :)
     * @return Returns the HashMap with previously saved favorites from the SharedPreferences
     *
     * Loads the json with the saved favorites from the sharedPreferences and converts it into a Hashmap
     * which will be used to populate the favorite Drawer
     */
    protected HashMap<String,String> getFavoriteHashMap(MainActivity mainActivity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        Gson gson = new Gson();
        String json = prefs.getString("favorites","");
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        HashMap<String, String> favoriteMap = gson.fromJson(json, type);



        return sortByValue(favoriteMap);
    }

    /**
     * @param map HashMap with Favorite Entrys, which shall be sorted alphabetical
     * @param <K> Key of the HashMap Entrys, contains the City-ID for the openweathermap API
     * @param <V> Value of the HashMap Entrys, Contains the citynames, which are ascociated with the city IDs
     * @return Returns a HashMap, which ist sorted alphabetically, so it's easier for the User to find his favorites in the List
     *
     * Function converts a unsorted (alphabetically) HashMap into a HashMap, which ist sorted alphabetically by the Values of the Map.
     * This means, the Cities are sorted alphabetically by their Name, so the Favorite list in the favorite Drawer is much easier to use
     *
     * If there are no saved favorites in the SharedPreferences which could be sorted, then return a new, empty HashMap
     */
    protected <K, V> HashMap<K, V> sortByValue(Map<K, V> map) {

        if(map!=null) {
            List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
            Collections.sort(list, new Comparator<Object>() {
                @SuppressWarnings("unchecked")
                public int compare(Object o1, Object o2) {
                    return ((Comparable<V>) ((Map.Entry<K, V>) (o1)).getValue()).compareTo(((Map.Entry<K, V>) (o2)).getValue());
                }
            });

            HashMap<K, V> result = new LinkedHashMap<>();
            for (Iterator<Map.Entry<K, V>> entryIterator = list.iterator(); entryIterator.hasNext(); ) {
                Map.Entry<K, V> entry = entryIterator.next();
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }
        else{
            return new HashMap<>();
        }

    }


    /**
     * @param deg Get the Degree wo the Wind
     * @return  Returns the Wind Direction as a String
     *
     * Converterfunction, to make the wind-direction data more readable for the user, since it wouldn't be very clear what'a meant with
     * a wind direction of 234 Degree.
     */
    protected String getWindDirection(float deg){
        String direction = "";
        if (deg >= 348.75 || deg<= 11.25){
            direction = "Norden";
        }
        if (deg >= 11.25 && deg <= 33.75){
            direction = "Nord-Nord-Ost";
        }
        if (deg >= 33.75 && deg<= 56.25){
            direction = "Nord-Ost";
        }
        if (deg >= 56.25 && deg <= 78.75){
            direction = "Ost-Nord-Ost";
        }
        if (deg >= 78.75 && deg<=101.25){
            direction = "Osten";
        }
        if (deg >= 101.25 && deg <= 123.75){
            direction = "Ost-Süd-Ost";
        }
        if (deg >= 123.75 && deg<= 146.25){
            direction = "Süd-Ost";
        }
        if (deg >= 146.25 && deg <= 168.75){
            direction = "Süd-Süd-Ost";
        }
        if (deg >= 168.75 && deg<= 191.25){
            direction = "Süden";
        }
        if (deg >= 191.25 && deg <= 213.75){
            direction = "Süd-Süd-West";
        }
        if (deg >= 213.75 && deg<= 236.25){
            direction = "Süd-West";
        }
        if (deg >= 236.25 && deg <= 258.75){
            direction = "West-Süd-West";
        }
        if (deg >= 258.75 && deg<= 281.25){
            direction = "Westen";
        }
        if (deg >= 281.25 && deg <= 303.75){
            direction = "West-Nord-West";
        }
        if (deg >= 303.75 && deg<=326.25){
            direction = "Nord-West";
        }
        if (deg >= 326.25 && deg <= 348.75){
            direction = "Nord-Nord-West";
        }
        return direction;
    }


    /**
     * @param time Get's the Time as String in the Format "HH:mm" (Hours:Minutes)
     * @return Returns True, if given time is in the Night, and False if it is Daytime
     *
     * Function used to determine, if it's night or not, so the correct symbol can be used (either with sun oder moon)
     */
    protected boolean isNight(String time){
        return time.equals("01:00") || time.equals("04:00") ||
                time.equals("07:00") || time.equals("22:00");
    }


    /**
     * @return String
     *
     * Returns the current time as a String in the format HH:mm (Hours:Minutes)
     * Used by function isNight() to check, if a symbol with a moon should be used oder not
     */
    protected String getCurrentTimeString(){
        return new SimpleDateFormat("kk:mm", Locale.GERMANY).format(new Date());
    }


    /**
     * @param speed Speed in meters per second
     * @return Speed in km per hour
     * Simple Converter-Function to convert m/s to km/h, because the API only gives the speed in m/s
     * but it's better to show it in km/h
     *
     */
    protected double meterPerSecondToKilometerPerHour(double speed){
        return Math.round(speed*3600/1000);
    }


    /**
     * @param mainActivity Pass Mainactivity to function, so Alertdialog can be created
     * @param cityName Searched name of the city from User-Input, which could not be found
     *
     * Shows an AlertDialog to the user to inform, that there was no Match for the input found via API
     * Also hides the WheaterLayout and Progressbar, otherwise both could be shown, until user makes his next input
     *
     */
    protected void showNoCityFoundDialog(MainActivity mainActivity, String cityName){

        mainActivity.findViewById(R.id.progress).setVisibility(View.GONE);
        mainActivity.findViewById(R.id.weatherLayout).setVisibility(View.GONE);

        AlertDialog alertDialog = new AlertDialog.Builder(mainActivity).create();
        alertDialog.setIcon(R.drawable.ic_location_off_black_36dp);
        alertDialog.setTitle("Stadt wurde nicht gefunden");
        alertDialog.setMessage("Leider konnte keine Stadt mit dem Namen '"+ cityName +"'\n" +
                "gefunden werden.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    /**
     * @param mainActivity PAss MainActivity, so AlertDialog is working
     *y
     * Shows User an AlertDialog to inform him, that this App requests Location-Permission from him
     * If the User Agrees, the user will be shown a dialog from his device, where the user can grant Location Permission for this Application
     *
     * If he Disagrees, the Dialog will be dismissed and he can still use the App, but the Button for Using GPS-Location will be hidden
     */
    protected void requestLocationPermissionFromUser(MainActivity mainActivity) {


        new AlertDialog.Builder(mainActivity)
                .setTitle("Berechtigungen benötigt")
                .setIcon(R.drawable.ic_perm_device_information_black_36dp)
                .setMessage("Diese App braucht Zugriff auf die Positionsdaten, um dir das Wetter am aktuellen Standort anzeigen zu können. " +
                        "\n" + "Bitte im Folgenden Fenster dafür die Berechtigungen gewähren.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(mainActivity,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, mainActivity.LOCATION_CODE);
                        mainActivity.findViewById(R.id.gpsButton).setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("Abrechen", (dialog, which) -> {
                    dialog.dismiss();
                    mainActivity.findViewById(R.id.gpsButton).setVisibility(View.GONE);
                })
                .create().show();

    }
}


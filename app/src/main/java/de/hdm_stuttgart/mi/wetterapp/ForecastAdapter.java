package de.hdm_stuttgart.mi.wetterapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.recyclerview.widget.RecyclerView;
import de.hdm_stuttgart.mi.wetterapp.forecastData.WeatherData;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.MyViewHolder> {
    private WeatherData forecastData;
    private Helper helper = new Helper();


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView maxTemp, minTemp, description, windSpeed, date, timeTextview, windDirection;
        ImageView icon;
        LinearLayout linearLayout;


        MyViewHolder(LinearLayout v) {
            super(v);
        }
    }

    /**
     * @param weatherData Passes the WeatherData to the Adpater, so the ForeCast-Recyclerview can be filled with
     *                    the Weather of the next 5 Days
     */
    ForecastAdapter(WeatherData weatherData) {
        forecastData = weatherData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ForecastAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        LinearLayout root = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecastweather, parent, false);

        MyViewHolder vh = new MyViewHolder(root);
        vh.date = root.findViewById(R.id.dateTextview);
        vh.timeTextview = root.findViewById(R.id.timeTextView);
        vh.description = root.findViewById(R.id.forecastDescription);
        vh.minTemp = root.findViewById(R.id.minForecastTemp);
        vh.maxTemp = root.findViewById(R.id.maxForecastTemp);
        vh.windSpeed = root.findViewById(R.id.forecastWindspeed);
        vh.icon = root.findViewById(R.id.forecastIcon);
        vh.windDirection = root.findViewById(R.id.forecastWindDirection);
        vh.linearLayout = root.findViewById(R.id.forecastLinearLayout);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String outputDate = "";
        String outputTime = "";
        String dateString = forecastData.list.get(position).dtTxt;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.GERMANY);

        format.setTimeZone(TimeZone.getTimeZone("UTC"));    // Sets Timezone for Formatter to UTC (which is the timezone of the Weather API Data)
        try {


            // Split DateText in Date and Time in to seperate Strings, so they can be used seperately for checking time oder checking date only
            Date date = format.parse(dateString);

            SimpleDateFormat formatDate = new SimpleDateFormat("EEEE, dd.MM.yyyy", Locale.GERMANY); // Just get the current Date from the dateString
            SimpleDateFormat formatTime = new SimpleDateFormat("kk:mm", Locale.GERMANY);    // Get only the time from the dateString
            outputDate = formatDate.format(date);
            outputTime = formatTime.format(date);


            if(outputTime.equals("24:00")){
                outputTime = "00:00";       //Change String, so a new Day doesn't start with 24:00 as time
            }

            // Check if Forecastdata is from the past, if yes, hide the Data, otherwise show data
            if (date.before(new Date())) {

                // Invalidate RecyclerView Item if ForecastData is from the past,
                // otherwise theres sometimes an empty cardview at the beginning of the
                // Recyclerview
                holder.linearLayout.invalidate();

            }
            else {

                // If ForecastData is in the future, then the recyclerview Item will be set visible
                holder.linearLayout.setVisibility(View.VISIBLE);

            }

        }
        catch (ParseException e){
            Log.e("Error while Parsing Date Text", String.valueOf(e));
        }

        String timeString = outputTime + " Uhr:";
        String minTempString = "Minimaltemperatur: " + Math.round(forecastData.list.get(position).main.tempMin) + "C°";
        String maxTempString = "Maximaltemperatur: " + Math.round(forecastData.list.get(position).main.tempMax) + "C°";
        String windSpeedString = "Windgeschwindigkeit: " + helper.meterPerSecondToKilometerPerHour(forecastData.list.get(position).wind.speed) + "km/h ";
        String windDirectionString = "Windrichtung: " + helper.getWindDirection(forecastData.list.get(position).wind.deg);

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.date.setText(outputDate);
        holder.timeTextview.setText(timeString);
        holder.description.setText(forecastData.list.get(position).weather.get(0).description);
        holder.minTemp.setText(minTempString);
        holder.maxTemp.setText(maxTempString);
        holder.windSpeed.setText(windSpeedString);
        holder.windDirection.setText(windDirectionString);



        /* Switch-Case to find right Icon for Weather Condition
        * also checks in a few cases, if it's nighttime and give out the right icon
        * so that theres no sun in the Night :)
        */
        switch(forecastData.list.get(position).weather.get(0).id){

            case(200): case(201): case(202): case(210): case(211): case(212): case(221):
            case(230): case (231): case(232):
                holder.icon.setImageResource(R.drawable.w00);
                break;

            case(300): case(301): case(500):
                holder.icon.setImageResource(R.drawable.w11);
                break;

            case(611): case(616):
                holder.icon.setImageResource(R.drawable.w05);
                break;

            case(311): case(312): case(310): case(501): case (521):
                holder.icon.setImageResource(R.drawable.w09);
                break;

            case(313): case(314): case(321): case(502): case(520):
            case(302):
                holder.icon.setImageResource(R.drawable.w12);
                break;

            case(503): case(504): case(522): case(531):
                holder.icon.setImageResource(R.drawable.w02);
                break;

            case(511):
                holder.icon.setImageResource(R.drawable.w07);
                break;

            case(600):
                holder.icon.setImageResource(R.drawable.w18);
                break;

            case(620):
                holder.icon.setImageResource(R.drawable.w13);
                break;

            case(601): case(621):
                holder.icon.setImageResource(R.drawable.w14);
                break;

            case(602): case(622):
                holder.icon.setImageResource(R.drawable.w16);
                break;

            case(612): case(613): case(615):
                holder.icon.setImageResource(R.drawable.w06);
                break;

            case(701): case(702): case(711): case(741): case(721): case(751): case(761):
                holder.icon.setImageResource(R.drawable.w20);
                break;

            case(731): case(781): case(771):
                holder.icon.setImageResource(R.drawable.w23);
                break;

            case(800):

                if(helper.isNight(outputTime)){

                    holder.icon.setImageResource(R.drawable.w31);
                }
                else {

                    // Check if it's going to be very hot (over 30C°), if yes, show special Sun Icon to
                    // show it to the User
                    if(forecastData.list.get(position).main.temp>30){
                        holder.icon.setImageResource(R.drawable.w36);
                    }
                    else {
                        holder.icon.setImageResource(R.drawable.w32);
                    }
                }
                break;

            case(801):
                if(helper.isNight(outputTime)) {

                    holder.icon.setImageResource(R.drawable.w33);
                }
                else{

                    holder.icon.setImageResource(R.drawable.w34);
                }
                break;

            case(802):
                if(helper.isNight(outputTime)) {

                    holder.icon.setImageResource(R.drawable.w29);
                }
                else{

                    holder.icon.setImageResource(R.drawable.w30);
                }
                break;

            case(803):
                if(helper.isNight(outputTime)) {

                    holder.icon.setImageResource(R.drawable.w27);
                }
                else{

                    holder.icon.setImageResource(R.drawable.w28);
                }
                break;
            case(804):
                holder.icon.setImageResource(R.drawable.w26);
                break;


            default:
                holder.icon.setImageResource(R.drawable.w44);
                holder.icon.setVisibility(View.GONE);

        }

    }

    @Override
    public int getItemCount() {
        return forecastData.list.size();
    }


}

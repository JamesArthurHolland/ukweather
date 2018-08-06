package ukweather.ukweather.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import ukweather.ukweather.City;

/**
 * Created by jamie on 04/08/18.
 */

public class WeatherForecast
{
    @SerializedName("list")
    public ArrayList<WeatherCondition> conditions = new ArrayList<WeatherCondition>();


}

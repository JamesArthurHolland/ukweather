package ukweather.ukweather.model;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamie on 06/08/18.
 */

public class CurrentWeather
{
    @SerializedName("dt")
    public LocalDateTime dateTime;
    @SerializedName("name")
    public String name;
    @SerializedName("main")
    public WeatherCondition.TemperatureInfo temperatureInfo;
    @SerializedName("weather")
    public List<WeatherCondition.MetaInfo> metaInfo = new ArrayList<>();
    @SerializedName("rain")
    public WeatherCondition.PrecipitationInfo rainInfo = new WeatherCondition.PrecipitationInfo();
    @SerializedName("snow")
    public WeatherCondition.PrecipitationInfo snowInfo = new WeatherCondition.PrecipitationInfo();
    @SerializedName("clouds")
    public WeatherCondition.CloudInfo cloudInfo = new WeatherCondition.CloudInfo();
    @SerializedName("sys")
    public SunInfo sunInfo = new SunInfo();

    public static final class SunInfo
    {
        @SerializedName("sunrise")
        public LocalDateTime sunrise;
        @SerializedName("sunset")
        public LocalDateTime sunset;
    }

}

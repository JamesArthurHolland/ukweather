package ukweather.ukweather.model;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jamie on 04/08/18.
 */

public class WeatherCondition
{
    public static final String[] times = {"1am", "4am", "7am", "10am", "1pm", "4pm", "7pm", "10pm"};

    @SerializedName("dt")
    public LocalDateTime dateTime;
    @SerializedName("main")
    public TemperatureInfo temperatureInfo;
    @SerializedName("weather")
    public List<MetaInfo> metaInfo = new ArrayList<>();
    @SerializedName("rain")
    public PrecipitationInfo rainInfo = new PrecipitationInfo();
    @SerializedName("snow")
    public PrecipitationInfo snowInfo = new PrecipitationInfo();
    @SerializedName("clouds")
    public CloudInfo cloudInfo = new CloudInfo();

    public static final class TemperatureInfo {
        @SerializedName("temp")
        public float temperature;
        @SerializedName("temp_min")
        public float minimalTemperature;
        @SerializedName("temp_max")
        public float maximalTemperature;

        @Override
        public String toString() {
            return "Main{" +
                "temperature=" + temperature +
                ", minimalTemperature=" + minimalTemperature +
                ", maximalTemperature=" + maximalTemperature +
                '}';
        }
    }

    public static final class MetaInfo
    {
        @SerializedName("id")
        public int id;
        @SerializedName("main")
        public String name;
        @SerializedName("description")
        public String description;
        @SerializedName("icon")
        public String icon;
    }

    public static final class PrecipitationInfo {
        @SerializedName("3h")
        public double value;

        @Override
        public String toString() {
            return "PrecipitationInfo{" +
                "value=" + value +
                '}';
        }
    }


    public static final class CloudInfo {
        @SerializedName("all")
        public int percentage;

        @Override
        public String toString() {
            return "CloudInfo{" +
                "percentage=" + percentage +
                '}';
        }
    }
}

package ukweather.ukweather;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.threeten.bp.format.DateTimeFormatter;

import ukweather.ukweather.model.CurrentWeather;

public class CurrentWeatherFragment extends Fragment implements WeatherActivity.CurrentDayWeatherForecastListener
{
    TextView dateTextView;
    TextView nameTextView;
    TextView tempMaxTextView;
    TextView tempMinTextView;
    TextView sunRiseTextView;
    TextView sunsetTextView;
    LinearLayout forecastLayout;

    public CurrentWeatherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_weather, container, false);
        nameTextView = rootView.findViewById(R.id.city_name);
        dateTextView = rootView.findViewById(R.id.date);
        tempMaxTextView = rootView.findViewById(R.id.temp_max);
        tempMinTextView = rootView.findViewById(R.id.temp_min);
        sunRiseTextView = rootView.findViewById(R.id.sun_rise);
        sunsetTextView = rootView.findViewById(R.id.sun_set);
        forecastLayout = rootView.findViewById(R.id.forecast_wrapper);
        return rootView;
    }

    @Override
    public void onWeatherUpdated(CurrentWeather forecast) {
        nameTextView.setText(forecast.name);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        String date = forecast.dateTime.format(formatter);
        dateTextView.setText(date);
        tempMaxTextView.setText("" + TemperatureUtils.kevlinToCelsius(forecast.temperatureInfo.maximalTemperature) + " Celsius");
        tempMinTextView.setText("" + TemperatureUtils.kevlinToCelsius(forecast.temperatureInfo.minimalTemperature) + " Celsius");
        DateTimeFormatter twelveHourFormat = DateTimeFormatter.ofPattern("hh:mm a");
        sunRiseTextView.setText("" + forecast.sunInfo.sunrise.format(twelveHourFormat));
        sunsetTextView.setText("" + forecast.sunInfo.sunset.format(twelveHourFormat));
        forecastLayout.setVisibility(View.VISIBLE);
    }
}

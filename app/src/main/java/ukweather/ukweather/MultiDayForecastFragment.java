package ukweather.ukweather;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.davidmoten.rtree.geometry.Line;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import ukweather.ukweather.model.WeatherCondition;
import ukweather.ukweather.model.WeatherForecast;

import static java.lang.Math.floor;


public class MultiDayForecastFragment extends Fragment implements WeatherActivity.MultiDayWeatherForecastListener
{
    private TableLayout mForecastTable;
    private LinearLayout mForecastLayout;
    private TextView mCityName;

    public MultiDayForecastFragment() {
    }

    public void onWeatherUpdated(City city, WeatherForecast weatherForecast)
    {
        ArrayList<WeatherCondition> weatherConditions = weatherForecast.conditions;

        TreeMap<String, ArrayList<WeatherCondition>> days = getDaysConditions(weatherConditions);

        boolean firstTableRowInserted = false;
        for(Map.Entry<String,ArrayList<WeatherCondition>> entry : days.entrySet()) {
            TableRow row = new TableRow(getContext());
            if(firstTableRowInserted) {
                TextView dateTextView = new TextView(getContext());
                LocalDate localDate = LocalDate.parse(entry.getKey());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
                String date = localDate.format(formatter);

                dateTextView.setText(date);
                row.addView(dateTextView);
                ArrayList<WeatherCondition> dayConditions = entry.getValue();
                setConditionsForRemainingRows(row, dayConditions);
            }
            else {
                TextView dateTextView = new TextView(getContext());
                dateTextView.setText("Today");
                row.addView(dateTextView);
                ArrayList<WeatherCondition> dayConditions = entry.getValue();
                setEmptyCellsInFirstRow(row, dayConditions);
                setConditionsForRemainingRows(row, dayConditions);
                firstTableRowInserted = true;
            }
            mForecastTable.addView(row);
        }

        mForecastLayout.setVisibility(View.VISIBLE);
        mCityName.setText(city.name);
    }

    private TreeMap<String, ArrayList<WeatherCondition>> getDaysConditions(ArrayList<WeatherCondition> weatherConditions) {
        TreeMap<String, ArrayList<WeatherCondition>> days = new TreeMap<>();
        while(weatherConditions.isEmpty() == false) {
            WeatherCondition condition = weatherConditions.remove(0);
            String date = condition.dateTime.toLocalDate().toString();

            ArrayList<WeatherCondition> dayConditions = days.get(date);
            if(dayConditions == null) {
                dayConditions = new ArrayList<>();
            }
            dayConditions.add(condition);
            days.put(date, dayConditions);
        }
        return days;
    }

    private void setEmptyCellsInFirstRow(TableRow row, ArrayList<WeatherCondition> dayConditions) {
        int numberOfEmptyCells = WeatherCondition.times.length - dayConditions.size();
        for(int i = 0; i < numberOfEmptyCells; i++) {
            TextView textView = new TextView(getContext());
            row.addView(textView);
        }
    }

    private void setConditionsForRemainingRows(TableRow row, ArrayList<WeatherCondition> dayConditions) {
        for(WeatherCondition condition : dayConditions) {
            TextView textView = new TextView(getContext());
            setWeatherIconForTextView(condition, textView);
            textView.setGravity(Gravity.CENTER);
            Typeface custom_font = Typeface.createFromAsset(getContext().getAssets(),  "fonts/weathericons-regular-webfont.ttf");
            textView.setTypeface(custom_font);
            row.addView(textView);
        }
    }

    private void setWeatherIconForTextView(WeatherCondition condition, TextView textView) {
        int code = (int) floor(condition.metaInfo.get(0).id / 100);
        switch(code) {
            case 2:
                textView.setText(R.string.wi_thunderstorm);
                break;
            case 3:
                textView.setText(R.string.wi_rain);
                break;
            case 5:
                textView.setText(R.string.wi_rain);
                break;
            case 6:
                textView.setText(R.string.wi_snow);
                break;
            case 7:
                textView.setText(R.string.wi_smog);
                break;
            case 8:
                textView.setText(R.string.wi_day_sunny);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_multi_day_forecast, container, false);

        mForecastTable = (TableLayout) rootView.findViewById(R.id.forecast_table);
        mForecastLayout = (LinearLayout) rootView.findViewById(R.id.forecast_wrapper);
        mCityName = (TextView) rootView.findViewById(R.id.city_name);

        return rootView;
    }
}

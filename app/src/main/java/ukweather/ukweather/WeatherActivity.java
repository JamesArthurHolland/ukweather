package ukweather.ukweather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ukweather.ukweather.model.WeatherCondition;
import ukweather.ukweather.model.WeatherForecast;
import ukweather.ukweather.retrofitClient.RetrofitClient;

import static java.lang.Math.floor;

public class WeatherActivity extends AppCompatActivity
{
    private ArrayList<City> mCities;
    private RTree<String, Point> mRtree;
    private TableLayout mForecastTable;

    @Inject
    RetrofitClient mRetrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        switchToGrantPermissionsActivityIfNoLocationPermissions();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ((UKWeatherApp) getApplication()).getDiComponent().inject(this);

        mForecastTable = (TableLayout) findViewById(R.id.forecast_table);



//        parseCitiesFromAssetsFile();
//        mRtree = RTree.create();
//        createRTreeFromCities();
//
//        PointDouble guessPoint = PointDouble.create(54.769876, -6.092390);
//
//
////        String name = list.size();
//
//        AutoCompleteTextView citySearch = findViewById(R.id.city_search);
//        ArrayAdapter<City> citySearchAutoCompleteAdapter = new ArrayAdapter<City>(
//            this,
//            android.R.layout.simple_dropdown_item_1line, mCities
//        );
//        citySearch.setAdapter(citySearchAutoCompleteAdapter);
//
//        Button useCurrentLocation = findViewById(R.id.use_current_location);
//        useCurrentLocation.setOnClickListener((View view) -> {
//            List<Entry<String, Point>> list = mRtree.nearest(guessPoint, Integer.MAX_VALUE, 1).toList().toBlocking().single();
//            int size = list.size();
//            int id = Integer.parseInt(list.get(0).value());
//            if (size > 0) {
//                Log.d("WeatherActivity id is", "" + id);
//            } else {
//                // TODO show error message
//            }
//        });

        Button useCurrentLocation = findViewById(R.id.use_current_location);
        useCurrentLocation.setOnClickListener((View view) -> {
            mRetrofitClient.getWeather("2651210")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherForecast -> updateWeatherForecast(weatherForecast));
        });



    }

    private void updateWeatherForecast(WeatherForecast weatherForecast)
    {
        ArrayList<WeatherCondition> weatherConditions = weatherForecast.conditions;

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

            Log.d("WeatherActivity", condition.dateTime.toLocalDate().toString());
            Log.d("WeatherActivity2", "" + condition.dateTime.toLocalDate().toEpochDay());
            Log.d("WeatherActivity", "" + condition.dateTime.toLocalDate().toEpochDay());
        }
        Log.d("RetrofitClient", "asdfasdf");

        boolean firstTableRowInserted = false;
        for(Map.Entry<String,ArrayList<WeatherCondition>> entry : days.entrySet()) {
            TableRow row = new TableRow(this);
            if(firstTableRowInserted) {
                TextView dateTextView = new TextView(this);
                LocalDate localDate = LocalDate.parse(entry.getKey());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
                String date = localDate.format(formatter);

                dateTextView.setText(date);
                row.addView(dateTextView);
                ArrayList<WeatherCondition> dayConditions = entry.getValue();
                setConditionsForRemainingRows(row, dayConditions);
            }
            else {
                TextView dateTextView = new TextView(this);
                dateTextView.setText("Today"); // TODO
                row.addView(dateTextView);
                ArrayList<WeatherCondition> dayConditions = entry.getValue();
                setEmptyCellsInFirstRow(row, dayConditions);
                setConditionsForRemainingRows(row, dayConditions);
                firstTableRowInserted = true;
            }
            mForecastTable.addView(row);
        }
    }

    private void setEmptyCellsInFirstRow(TableRow row, ArrayList<WeatherCondition> dayConditions) {
        int numberOfEmptyCells = WeatherCondition.times.length - dayConditions.size();
        for(int i = 0; i < numberOfEmptyCells; i++) {
            TextView textView = new TextView(this);
            row.addView(textView);
        }
    }

    private void setConditionsForRemainingRows(TableRow row, ArrayList<WeatherCondition> dayConditions) {
        for(WeatherCondition condition : dayConditions) {
            TextView textView = new TextView(this);
            setWeatherIconForTextView(condition, textView);
            textView.setGravity(Gravity.CENTER);
            Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/weathericons-regular-webfont.ttf");
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

    private void parseCitiesFromAssetsFile() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(City.class, new CityDeserializer());
        Gson gson = gsonBuilder.create();

        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("ukcities.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            mCities = gson.fromJson(bufferedReader, new TypeToken<ArrayList<City>>()
            {
            }.getType()); // line 6
            Log.d("Weather Activity", "DBG");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createRTreeFromCities() {
        for (City city : mCities) {
            int id = city.id;
            Point point = city.coordinates;
            mRtree = mRtree.add("" + id, Geometries.point(point.x(), point.y()));
        }
    }

    private void switchToGrantPermissionsActivityIfNoLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(this, GrantPermissionsActivity.class);
            startActivity(i);
        }
    }

}
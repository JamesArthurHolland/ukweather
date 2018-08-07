package ukweather.ukweather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.internal.PointDouble;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ukweather.ukweather.model.CurrentWeather;
import ukweather.ukweather.model.WeatherForecast;
import ukweather.ukweather.retrofitClient.RetrofitClient;

public class WeatherActivity extends AppCompatActivity
{
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;
    private City mSelectedCity;

    @Inject
    RetrofitClient mRetrofitClient;

    @Inject
    CityService mCityService;

    @Inject
    SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ((UKWeatherApp) getApplication()).getDiComponent().inject(this);


        if (sharedPreferencesManager.getSkippedPermissions() == false) {
            switchToGrantPermissionsActivityIfNoLocationPermissions();
        }


        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager();

        AutoCompleteTextView citySearch = findViewById(R.id.city_search);
        ArrayAdapter<City> citySearchAutoCompleteAdapter = new ArrayAdapter<City>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            mCityService.mCities
        );
        citySearch.setAdapter(citySearchAutoCompleteAdapter);
        citySearch.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            Object item = parent.getItemAtPosition(position);
            if(item instanceof City) {
                mSelectedCity = (City) item;
                getWeather();
            }
        });

        Button useCurrentLocation = findViewById(R.id.use_current_location);
        if (sharedPreferencesManager.getSkippedPermissions()) {
            useCurrentLocation.setEnabled(false);
        }
        useCurrentLocation.setOnClickListener((View view) -> {
            mSelectedCity = mCityService.getNearestCity(getGPS());
            citySearch.setText(mSelectedCity.name);
            getWeather();
        });
    }

    private void getWeather()
    {
        if(mSelectedCity == null) {
            return;
        }
        int current = mViewPager.getCurrentItem();
        if(current == 0) { // Current Weather
            mRetrofitClient.getCurrentWeatherInfo("" + mSelectedCity.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherForecast -> updateCurrentWeather(weatherForecast));
        }
        else if(current == 1) {
            mRetrofitClient.get5DayWeatherInfo("" + mSelectedCity.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weatherForecast -> updateWeatherForecast(mSelectedCity, weatherForecast));
        }
    }

    private PointDouble getGPS() {
        LocationManager lm = (LocationManager) getSystemService(
            Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        Location locations = null;

        for (int i = providers.size() - 1; i >= 0; i--) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locations = lm.getLastKnownLocation(providers.get(i));
                if (locations != null) break;
            }
        }
        double lat;
        double lon;
        if (locations != null) {
            lat = locations.getLatitude();
            lon = locations.getLongitude();
            return PointDouble.create(lat, lon);
        }
        return null;
    }

    private void updateCurrentWeather(CurrentWeather forecast)
    {
        int id = mViewPager.getCurrentItem();
        Fragment currentFragment = mAdapter.getItem(id);

        if(currentFragment instanceof CurrentDayWeatherForecastListener) {
            mAdapter.notifyDataSetChanged();
            ((CurrentDayWeatherForecastListener)currentFragment).onWeatherUpdated(forecast);
        }
    }

    private void updateWeatherForecast(City city, WeatherForecast forecast)
    {
        int id = mViewPager.getCurrentItem();
        Fragment currentFragment = mAdapter.getItem(id);

        if(currentFragment instanceof MultiDayWeatherForecastListener) {
            mAdapter.notifyDataSetChanged();
            ((MultiDayWeatherForecastListener)currentFragment).onWeatherUpdated(city, forecast);
        }
    }

    private void setupViewPager() {
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new CurrentWeatherFragment(), "Current Weather");
        mAdapter.addFragment(new MultiDayForecastFragment(), "5 day forecast");
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getWeather();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
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

    public interface CurrentDayWeatherForecastListener
    {
        void onWeatherUpdated(CurrentWeather forecast);
    }

    public interface MultiDayWeatherForecastListener
    {
        void onWeatherUpdated(City city, WeatherForecast forecast);
    }
}
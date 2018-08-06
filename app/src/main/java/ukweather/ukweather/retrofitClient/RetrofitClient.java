package ukweather.ukweather.retrofitClient;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ukweather.ukweather.City;
import ukweather.ukweather.CityDeserializer;
import ukweather.ukweather.GsonHelper;
import ukweather.ukweather.model.WeatherCondition;
import ukweather.ukweather.model.WeatherForecast;

@Singleton
public class RetrofitClient
{
    private static GetWeatherService mGetWeatherService;
    private static final String URL = "http://api.openweathermap.org/data/2.5/";
    private static final String APP_ID = "07a102dad65e5e4f23a938a7c4866e3e";

    @Inject
    public RetrofitClient(Context context)
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, GsonHelper.LDT_DESERIALIZER);
        Gson gson = gsonBuilder.create();

        mGetWeatherService = new Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GetWeatherService.class);
    }

    public Flowable<WeatherForecast> getWeather(String id)
    {
        return mGetWeatherService.getWeatherInfo(id, APP_ID);

//            .enqueue(new Callback<WeatherForecast>(){
//            @Override
//            public void onResponse(Call<WeatherForecast> call, Response<WeatherForecast> response) {
//                WeatherForecast forecast = response.body();
//                Log.d("RetrofitClient", "" + response.body().toString());
//
//                for(WeatherCondition condition : forecast.list) {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    System.out.println(condition.dateTime.format(formatter));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<WeatherForecast> call, Throwable t) {
//                t.printStackTrace();
//                Log.d("RetrofitClient", "FAILED");
//            }
//        });

    }
}

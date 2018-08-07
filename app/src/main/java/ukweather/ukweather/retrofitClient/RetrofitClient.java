package ukweather.ukweather.retrofitClient;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.threeten.bp.LocalDateTime;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ukweather.ukweather.GsonHelper;
import ukweather.ukweather.NetworkUtils;
import ukweather.ukweather.model.CurrentWeather;
import ukweather.ukweather.model.WeatherForecast;

@Singleton
public class RetrofitClient
{
    private static GetWeatherService mGetWeatherService;
    private static final String URL = "http://api.openweathermap.org/data/2.5/";
    private static final String APP_ID = "07a102dad65e5e4f23a938a7c4866e3e";

    private Context mContext;

    @Inject
    public RetrofitClient(Context context)
    {
        mContext = context;
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(new File(context.getCacheDir(), "http"), SIZE_OF_CACHE);
        OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(new RewriteRequestInterceptor())
            .cache(cache)
            .build();


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

    public Flowable<WeatherForecast> get5DayWeatherInfo(String id)
    {
        return mGetWeatherService.get5DayWeatherInfo(id, APP_ID);
    }

    public Flowable<CurrentWeather> getCurrentWeatherInfo(String id)
    {
        return mGetWeatherService.getCurrentWeatherInfo(id, APP_ID);
    }

    public class RewriteRequestInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            int maxStale = 60 * 60; // 1 hour
            Request request;
            if (NetworkUtils.isNetworkConnected(mContext)) {
                request = chain.request();
            } else {
                request = chain.request().newBuilder().header("Cache-Control", "max-stale=" + maxStale).build();
            }
            return chain.proceed(request);
        }
    }
}

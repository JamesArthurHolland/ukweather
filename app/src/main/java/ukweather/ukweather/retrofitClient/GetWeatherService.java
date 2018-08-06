package ukweather.ukweather.retrofitClient;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ukweather.ukweather.model.WeatherCondition;
import ukweather.ukweather.model.WeatherForecast;

/**
 * Created by jamie on 04/08/18.
 */

public interface GetWeatherService
{
    @GET("forecast")
    Flowable<WeatherForecast> getWeatherInfo (@Query("id") String id,
                                              @Query("appid") String appid);
}

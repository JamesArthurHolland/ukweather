package ukweather.ukweather.di;

import javax.inject.Singleton;

import dagger.Component;
import ukweather.ukweather.GrantPermissionsActivity;
import ukweather.ukweather.WeatherActivity;
import ukweather.ukweather.retrofitClient.RetrofitClient;

@Singleton
@Component(modules={AppModule.class})
public interface DiComponent
{
    void inject(RetrofitClient retrofitClient);
    void inject(GrantPermissionsActivity activity);
    void inject(WeatherActivity activity);
}
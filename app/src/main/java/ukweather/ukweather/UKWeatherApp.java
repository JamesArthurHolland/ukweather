package ukweather.ukweather;

import android.app.Application;

import ukweather.ukweather.di.AppModule;
import ukweather.ukweather.di.DaggerDiComponent;
import ukweather.ukweather.di.DiComponent;


public class UKWeatherApp extends Application
{
    private DiComponent mDIComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mDIComponent = DaggerDiComponent.builder()
            .appModule(new AppModule(this))
            .build();
    }

    public DiComponent getDiComponent() {
        return mDIComponent;
    }
}

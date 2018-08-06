package ukweather.ukweather.di;

import android.app.Application;
import android.content.Context;

import ukweather.ukweather.UKWeatherApp;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule
{

    Context mContext;

    public AppModule(Context application) {
        mContext = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        if (mContext instanceof UKWeatherApp) {
            return (UKWeatherApp) mContext;
        }
        return null;
    }

    @Provides
    @Singleton
    Context providesApplicationContext() {
        return mContext;
    }
}
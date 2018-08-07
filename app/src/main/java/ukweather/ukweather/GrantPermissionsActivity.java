package ukweather.ukweather;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import javax.inject.Inject;

public class GrantPermissionsActivity extends AppCompatActivity
{
    @Inject
    SharedPreferencesManager sharedPreferencesManager;

    protected void switchToWeatherActivity()
    {
        Intent i = new Intent(this, WeatherActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_landing);
        ((UKWeatherApp) getApplication()).getDiComponent().inject(this);

        final RxPermissions rxPermissions = new RxPermissions(GrantPermissionsActivity.this);
        RxView.clicks(findViewById(R.id.enable_location))
            .compose(rxPermissions.ensure(Manifest.permission.ACCESS_COARSE_LOCATION))
            .subscribe(granted -> {
                sharedPreferencesManager.setSkippedPermissions(false);
                switchToWeatherActivity();
                finish();
            });

        RxView.clicks(findViewById(R.id.not_now))
            .subscribe(granted -> {
                sharedPreferencesManager.setSkippedPermissions(true);
                switchToWeatherActivity();
            });
    }
}

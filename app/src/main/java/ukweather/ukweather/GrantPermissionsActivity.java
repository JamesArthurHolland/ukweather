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

public class GrantPermissionsActivity extends AppCompatActivity
{
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;

    protected void switchToWeatherActivity()
    {
        Intent i = new Intent(this, WeatherActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_landing);

        final RxPermissions rxPermissions = new RxPermissions(GrantPermissionsActivity.this); // where this is an Activity or Fragment instance
        // Must be done during an initialization phase like onCreate
        RxView.clicks(findViewById(R.id.enable_location))
            .compose(rxPermissions.ensure(Manifest.permission.ACCESS_COARSE_LOCATION))
            .subscribe(granted -> {
                if(granted) {
                    Log.d("PermActivity", "granted");
                }
                else{
                    Log.d("PermActivity", "denied");
                }
                switchToWeatherActivity();
            });

        RxView.clicks(findViewById(R.id.not_now))
            .subscribe(granted -> {
                switchToWeatherActivity();
            });
    }



//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (ContextCompat.checkSelfPermission(this,
//            Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED) {
//
//            locationManager.requestLocationUpdates(provider, 400, 1, this);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (ContextCompat.checkSelfPermission(this,
//            Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED) {
//
//            locationManager.removeUpdates(this);
//        }
//    }
}

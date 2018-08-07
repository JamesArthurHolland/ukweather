package ukweather.ukweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by jamie on 06/08/18.
 */
@Singleton
public class SharedPreferencesManager
{
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private static final String SP_KEY_SKIPPED_PERMISSIONS = "SP_KEY_SKIPPED_PERMISSIONS";


    @Inject
    public SharedPreferencesManager(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public boolean getSkippedPermissions()
    {
        return sharedPreferences.getBoolean(SP_KEY_SKIPPED_PERMISSIONS, false);
    }

    public void setSkippedPermissions(boolean skippedPermissions)
    {
        editor.putBoolean(SP_KEY_SKIPPED_PERMISSIONS, skippedPermissions);
        editor.commit();
    }
}

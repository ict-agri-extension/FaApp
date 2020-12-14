package sindh.agriculureextension.fadiary.network;

import android.content.Context;
import android.content.SharedPreferences;

import sindh.agriculureextension.fadiary.database.Queries;

public class LocationData {

    private Context context;
    private SharedPreferences preferences;
    public LocationData(Context context) {
        this.context = context;
        preferences=context.getSharedPreferences(LocationData.class.getName(),Context.MODE_PRIVATE);
    }

    private double lat;
    private double lang;

    public double getLat() {
        return lat=preferences.getFloat(Queries._LAT,0);
    }

    public void setLat(double lat) {
        this.lat = lat;
        preferences.edit().putFloat(Queries._LANG, (float) lat).apply();
    }

    public double getLang() {
        return lang=preferences.getFloat(Queries._LAT,0);

    }

    public void setLang(double lang) {
        this.lang = lang;
        preferences.edit().putFloat(Queries._LANG, (float) lang).apply();
    }
}

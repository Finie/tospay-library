package net.tospay.auth.utils;

import android.content.Context;
import android.content.SharedPreferences;

import net.tospay.auth.R;
import net.tospay.auth.model.TospayUser;

import java.util.Calendar;
import java.util.Date;

/**
 * Class providing app specific sharedPreference settings.
 */
public class SharedPrefManager {

    public static final String KEY_REMEMBER_ME = "remember_me";
    private static final String PREF_ACTIVE_USER = "pref_active_user";
    private static final String KEY_TOKEN_EXPIRY = "token_expiry";
    private static final String KEY_REFRESHED_TOKEN = "token";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String ONLINE_PIN_SET = "online_pin ";

    private static SharedPreferences sharedPref;
    private Context context;

    public SharedPrefManager(Context context) {
        this.context = context;
    }

    public static SharedPrefManager getInstance(Context context) {
        return new SharedPrefManager(context);
    }

    /**
     * Get active user info.
     *
     * @return user or null if nobody logged in.
     */
    public TospayUser getActiveUser() {
        SharedPreferences prefs = getSettings();
        String json = prefs.getString(PREF_ACTIVE_USER, "");
        if (json.isEmpty() || "null".equals(json)) {
            return null;
        } else {
            return Utils.getGsonParser().fromJson(json, TospayUser.class);
        }
    }

    /**
     * Set active user.
     *
     * @param user active user or null for disable user.
     */
    public void setActiveUser(TospayUser user) {
        if (user != null) {
            if (user.getExpiredAt() != null) {
                setTokenExpiryTime(user.getExpiredAt());
            }
        }

        String json = Utils.getGsonParser().toJson(user);
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(PREF_ACTIVE_USER, json);
        editor.apply();
    }

    /**
     * Request access token
     *
     * @return - token
     */
    public String getAccessToken() {
        if (getActiveUser() != null) {
            return getActiveUser().getToken();
        }

        return null;
    }

    /**
     * Returns user bearer refresh token
     *
     * @return -token
     */
    public String getBearerRefreshToken() {
        if (getActiveUser() != null) {
            return getActiveUser().getRefreshToken();
        }

        return null;
    }

    /**
     * Obtain preferences instance.
     *
     * @return base instance of app SharedPreferences.
     */
    private SharedPreferences getSettings() {
        if (sharedPref == null) {
            sharedPref = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
        }

        return sharedPref;
    }

    /**
     * Read stored value
     *
     * @param name - key
     * @param def  - default value
     * @return boolean
     */
    public boolean read(String name, boolean def) {
        SharedPreferences prefs = getSettings();
        return prefs.getBoolean(name, def);
    }

    /**
     * save shared pref data
     *
     * @param name  - key
     * @param value - value
     */
    public void save(String name, boolean value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    /**
     * Read stored value
     *
     * @param name - key
     * @param def  - default value
     * @return boolean
     */
    public String read(String name, String def) {
        SharedPreferences prefs = getSettings();
        return prefs.getString(name, def);
    }

    /**
     * save shared pref data
     *
     * @param name  - key
     * @param value - value
     */
    public void save(String name, String value) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(name, value);
        editor.apply();
    }

    /**
     * Token expiry time
     *
     * @param expireAt - long time
     */
    private void setTokenExpiryTime(long expireAt) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putLong(KEY_TOKEN_EXPIRY, expireAt);
        editor.apply();
    }

    /**
     * Checks token expiry status
     *
     * @return -boolean
     */
    public boolean isTokenExpiredOrAlmost() {
        SharedPreferences prefs = getSettings();
        long expireAt = prefs.getLong(KEY_TOKEN_EXPIRY, 0);
        if (expireAt == 0) {
            return true;
        } else {
            Date date = new Date(expireAt * 1000L);
            long diff = date.getTime() - Calendar.getInstance().getTime().getTime();
            return diff < 5;
        }
    }

    public String getRefreshedToken() {
        SharedPreferences prefs = getSettings();
        return prefs.getString(KEY_REFRESHED_TOKEN, null);
    }

    public void setRefreshedToken(String refreshedToken) {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_REFRESHED_TOKEN, refreshedToken);
        editor.apply();
    }
}

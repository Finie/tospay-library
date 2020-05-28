package net.tospay.auth.utils;

import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class PhoneUtils {

    private static final String TAG = "PhoneUtils";

    /**
     * Parses data from PhoneStateListener.LISTEN_CELL_LOCATION.onCellLocationChanged
     * http://developer.android.com/reference/android/telephony/cdma/CdmaCellLocation.html
     *
     * @param location GsmCellLocation
     * @return JSON
     */
    public static String gsmCellLocationJSON(GsmCellLocation location) {

        final Calendar calendar = Calendar.getInstance();
        final JSONObject json = new JSONObject();

        if (location != null) {
            try {
                json.put("cid", location.getCid());
                json.put("lac", location.getLac());
                json.put("psc", location.getPsc());
            } catch (JSONException exc) {
                Log.e(TAG, "gsmCellLocationJSON: " + exc.toString());
            }
        }

        return json.toString();
    }
}

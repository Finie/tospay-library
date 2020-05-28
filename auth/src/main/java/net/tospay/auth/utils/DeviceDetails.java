package net.tospay.auth.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import net.tospay.auth.model.transfer.ReadDevice;
import net.tospay.auth.ui.device_model.CellTower;
import net.tospay.auth.ui.device_model.LocationReq;
import net.tospay.auth.ui.device_model.WifiAccessPoint;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.os.Build.*;

public class DeviceDetails {

    private static int homeMobileCountryCode, homeMobileNetworkCode;
    private static String carrier, macAddress, signalStrength, radioType;
    private static Boolean considerIp = false;
    private static double signal_noise_ratio;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = " ";

    @RequiresApi(api = VERSION_CODES.P)
    public static LocationReq getLocationRequest(Activity activity) {

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        String networkOperator = telephonyManager.getNetworkOperator();
        if (networkOperator != null) {
            homeMobileCountryCode = Integer.parseInt(networkOperator.substring(0, 3));
            homeMobileNetworkCode = Integer.parseInt(networkOperator.substring(3));
        }
        final int phonetype = telephonyManager.getPhoneType();
        carrier = telephonyManager.getNetworkOperatorName();

        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddress = wInfo.getMacAddress();

        if (ActivityCompat.checkSelfPermission(activity, READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE, ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            signalStrength = telephonyManager.getSignalStrength().toString();
            PhoneStateListener mPhoneStateListener = new PhoneStateListener();
            telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            final GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
            LocationReq request = new LocationReq();
            if (cellLocation != null) {
                request.setCarrier(carrier);
                request.setHomeMobileCountryCode(homeMobileCountryCode);
                request.setRadioType(String.valueOf(phonetype));
                request.setHomeMobileNetworkCode(homeMobileNetworkCode);
                CellTower cellTower = new CellTower();
                cellTower.setCellId(cellLocation.getCid());
                cellTower.setLocationAreaCode(cellLocation.getLac());
                cellTower.setMobileCountryCode(homeMobileCountryCode);
                cellTower.setMobileNetworkCode(homeMobileNetworkCode);
                List<CellTower> cellTowerList = new ArrayList<>();
                cellTowerList.add(cellTower);
                request.setCellTowers(cellTowerList);
                WifiAccessPoint accessPoint = new WifiAccessPoint();
                accessPoint.setMacAddress(macAddress);
                WifiAccessPoint accessPoint1 = new WifiAccessPoint();
                accessPoint1.setMacAddress(macAddress);
                List<WifiAccessPoint> accessPointList = new ArrayList<>();
                accessPointList.add(accessPoint);
                accessPointList.add(accessPoint1);
                request.setWifiAccessPoints(accessPointList);
                return request;

            } else {

                WifiAccessPoint accessPoint = new WifiAccessPoint();
                accessPoint.setMacAddress(macAddress);
                WifiAccessPoint accessPoint1 = new WifiAccessPoint();
                accessPoint1.setMacAddress(macAddress);
                List<WifiAccessPoint> accessPointList = new ArrayList<>();
                accessPointList.add(accessPoint);
                accessPointList.add(accessPoint1);
                request.setWifiAccessPoints(accessPointList);
                return request;
            }
        }
        LocationReq request = new LocationReq();
        WifiAccessPoint accessPoint = new WifiAccessPoint();
        accessPoint.setMacAddress(macAddress);
        WifiAccessPoint accessPoint1 = new WifiAccessPoint();
        accessPoint1.setMacAddress(macAddress);
        List<WifiAccessPoint> accessPointList = new ArrayList<>();
        accessPointList.add(accessPoint);
        accessPointList.add(accessPoint1);
        request.setWifiAccessPoints(accessPointList);
        return request;
    }


    @SuppressLint("HardwareIds")
    @RequiresApi(api = VERSION_CODES.O)
    public static ReadDevice readDevice(Activity activity) {
        ReadDevice readDevice = new ReadDevice();
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);

        } else {
            try {
                readDevice.setImei(telephonyManager.getImei());
                List<String> ipaddress = new ArrayList<>();
                ipaddress.add(getIPAddress(true));
                readDevice.setIp(ipaddress);
                readDevice.setNetworkCountryIso(telephonyManager.getNetworkCountryIso());
                readDevice.setOsVersion(Build.VERSION.RELEASE);
                readDevice.setPhone(telephonyManager.getLine1Number());
                if (Build.SERIAL != null && !Build.SERIAL.equalsIgnoreCase("unknown")) {
                    readDevice.setPhoneSerial(Build.SERIAL);
                } else {
                    readDevice.setPhoneSerial(Build.getSerial());
                }

                readDevice.setSimCardSerial(telephonyManager.getSimSerialNumber());
                return readDevice;
            }catch (Exception exception){
                if (exception.getMessage().equalsIgnoreCase("getImeiForSlot: The user 10354 does not meet the requirements to access device identifiers.")
                        && exception.getMessage().contains("getImeiForSlot: The user 10354 does not meet the requirements to access device identifiers.")){

                    readDevice.setImei("");
                    List<String> ipaddress = new ArrayList<>();
                    ipaddress.add(getIPAddress(true));
                    readDevice.setIp(ipaddress);
                    readDevice.setNetworkCountryIso(telephonyManager.getNetworkCountryIso());
                    readDevice.setOsVersion(Build.VERSION.RELEASE);
                    readDevice.setPhone(telephonyManager.getLine1Number());
                    if (Build.SERIAL != null && !Build.SERIAL.equalsIgnoreCase("unknown")) {
                        readDevice.setPhoneSerial("");
                    } else {
                        readDevice.setPhoneSerial("");
                    }
                    readDevice.setSimCardSerial("");
                    return readDevice;
                }

                readDevice.setImei("");
                List<String> ipaddress = new ArrayList<>();
                ipaddress.add(getIPAddress(true));
                readDevice.setIp(ipaddress);
                readDevice.setNetworkCountryIso(telephonyManager.getNetworkCountryIso());
                readDevice.setOsVersion(Build.VERSION.RELEASE);
                readDevice.setPhone(telephonyManager.getLine1Number());
                if (Build.SERIAL != null && !Build.SERIAL.equalsIgnoreCase("unknown")) {
                    readDevice.setPhoneSerial("");
                } else {
                    readDevice.setPhoneSerial("");
                }
                readDevice.setSimCardSerial("");
                return readDevice;
            }

        }
        return null;
    }


    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }
}

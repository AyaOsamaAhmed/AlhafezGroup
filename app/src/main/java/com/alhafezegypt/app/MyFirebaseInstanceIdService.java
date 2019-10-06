package com.alhafezegypt.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shady on 11/8/16.
 */
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String FRIENDLY_ENGAGE_TOPIC = "friendly_engage";
    private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";

    private LocationData getLocation() {
        LocationData locationData = null;
        GPSTracker gps = new GPSTracker(MainActivity.activityContext);

        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            locationData = new LocationData(latitude+"", longitude+"");
        }

        return locationData;
    }

    /**
     * The Application's current Instance ID token is no longer valid
     * and thus a new one must be requested.
     */
    @Override
    public void onTokenRefresh() {

        MainActivity.mainActivity.runOnUiThread(new Runnable() {
            public void run() {

                // If you need to handle the generation of a token, initially or
                // after a refresh this is where you should do that.
                String token = FirebaseInstanceId.getInstance().getToken();

                LocationData locationData = getLocation();
                String log;
                String lat;

                if (locationData == null) {
                    lat = "";
                    log = "";
                } else {
                    lat = locationData.getLatitude();
                    log = locationData.getLongitude();
                }

                sendRegData(token, lat, log);
                saveId(token);


                // Once a token is generated, we subscribe to topic.
                FirebaseMessaging.getInstance()
                        .subscribeToTopic(FRIENDLY_ENGAGE_TOPIC);

            }
        });

    }

    private synchronized void sendRegData(String token, String latitude, String longitude) {

        Retrofit adapter = new Retrofit.Builder()
                .baseUrl("http://alhafez-egypt.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AdvSvc advSvc = adapter.create(AdvSvc.class);
        advSvc.addRegisterUser(token, getPackageName()+"", getAppName()+"", "", latitude+"", longitude+"",getAppVer() ).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    int code = response.code();
                    if (code == 200) {
                        String data = response.body().string().toString();

                    }else {
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private synchronized String getAppVer() {
        PackageManager manager = getApplicationContext().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return info.versionName;
    }

    private synchronized String getAppName() {


        Resources appR = getApplicationContext().getResources();
        CharSequence appName = appR.getText(appR.getIdentifier("app_name",
                "string", getApplicationContext().getPackageName()));

        return appName.toString();
    }

    private void saveId(String token) {
        final SharedPreferences prefs = getGCMPreferences(getApplicationContext());
        int appVersion = getAppVersion(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, token);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private synchronized static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private synchronized SharedPreferences getGCMPreferences(Context context) {

        return context.getSharedPreferences("adv", Context.MODE_PRIVATE);
    }


}

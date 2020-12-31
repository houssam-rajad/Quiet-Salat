package com.quiet.salat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.Normalizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CustomizeFragment.SaveListener {


    private static final String KEY_SAVE_CITY = "Unknown";
    static final String KEY_SAVE_DURATION = "30";
    private static boolean city_check=false;
    private RequestQueue mQueue;
    LocationManager locationManager;
    public Location location;
    static public String cityName,tempDuration="30";
    private TextView txt1, txt2, txt3, txt4, txt5,txt6;
    private ImageButton settingButton,findButton,subhButton,duhrButton,asrButton,maghribButton,ishaeButton;
    private ImageView locationIcon;
    private boolean isServiceEnabled=false;
    FragmentManager fm;
    CustomizeFragment customizeFragment;
    FragmentTransaction fragmentTransaction;

    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity);
        init();

    }
    public String modify_cityname(String city)
    {
        if(!city_check)
        {
            city_check = true;
            city = Normalizer.normalize(city, Normalizer.Form.NFD);
            city = city.replaceAll("[^\\p{ASCII}]", "");
            city = city.replace(" Province","");
            city = city.replace(" Municipality","");
            city = city.replace(" Governorate","");
            int index = city.indexOf("-");
            if(index!=-1)
            city = city.substring(0, index);
            city = city.replace(" ", "-");
        }
        return city;
    }
    public void init()
    {

        mQueue=Volley.newRequestQueue(this);
        txt1 = findViewById(R.id.Tsubh);
        txt2 = findViewById(R.id.Tduhar);
        txt3 = findViewById(R.id.Tasr);
        txt4 = findViewById(R.id.Tmaghrib);
        txt5 = findViewById(R.id.Tishae);
        txt6 = findViewById(R.id.currentlocation);
        subhButton=findViewById(R.id.subhswitch);
        duhrButton=findViewById(R.id.duhr_switch);
        asrButton=findViewById(R.id.asr_switch);
        maghribButton=findViewById(R.id.maghrib_switch);
        ishaeButton=findViewById(R.id.ishae_switch);
        findButton=findViewById(R.id.location);
        locationIcon=findViewById(R.id.locationIcon);
        settingButton=findViewById(R.id.setting);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);
        loadData();
        isServiceEnabled=(SalatData.shared.salatList.size()!=0)?!check_switches_and():check_switches_and();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        fm=getSupportFragmentManager();

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
    public void customize(View v)
    {
        settingButton.startAnimation(AnimationUtils.loadAnimation(this,R.anim.rotate));
        customizeFragment=new CustomizeFragment();
        fragmentTransaction=fm.beginTransaction();
        fragmentTransaction.addToBackStack("setting");
        Bundle bundle = new Bundle();
        bundle.putString("dur",tempDuration);
        customizeFragment.setArguments(bundle);
        customizeFragment.show(fm,"show");
        fragmentTransaction.commit();
    }



    public void saveLastUpdate() {
        SharedPreferences sharedPreferences=getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor savedInstanceState = sharedPreferences.edit();
        Gson gson = new Gson();
        String json=gson.toJson(SalatData.shared.salatList);
        savedInstanceState.putString("list",json);
        savedInstanceState.putString(KEY_SAVE_DURATION,tempDuration);
        savedInstanceState.putString(KEY_SAVE_CITY,cityName);
        savedInstanceState.apply();
    }

    public void loadData() {

        SharedPreferences sharedPreferences=getPreferences(Context.MODE_PRIVATE);
        Gson gson =new Gson();
        String json=sharedPreferences.getString("list","") == "null" ? "[]" : sharedPreferences.getString("list","[]");
        Type type=new TypeToken<ArrayList<Salat>>(){}.getType();
        txt6.setText(sharedPreferences.getString(KEY_SAVE_CITY,"Unknown"));
        tempDuration=sharedPreferences.getString(KEY_SAVE_DURATION,"30");


            SalatData.shared.salatList=gson.fromJson(json,type);
        if(SalatData.shared.salatList !=null && !SalatData.shared.salatList.isEmpty())
        {
            txt1.setText(SalatData.shared.salatList.get(0).time);
            txt2.setText(SalatData.shared.salatList.get(1).time);
            txt3.setText(SalatData.shared.salatList.get(2).time);
            txt4.setText(SalatData.shared.salatList.get(3).time);
            txt5.setText(SalatData.shared.salatList.get(4).time);
            if(SalatData.shared.salatList.get(0).isEnabled )  subhButton.setBackgroundResource(R.drawable.enabled) ; else subhButton.setBackgroundResource(R.drawable.disabled);
            if(SalatData.shared.salatList.get(1).isEnabled )  duhrButton.setBackgroundResource(R.drawable.enabled) ; else duhrButton.setBackgroundResource(R.drawable.disabled);
            if(SalatData.shared.salatList.get(2).isEnabled )  asrButton.setBackgroundResource(R.drawable.enabled) ; else asrButton.setBackgroundResource(R.drawable.disabled);
            if(SalatData.shared.salatList.get(3).isEnabled )  maghribButton.setBackgroundResource(R.drawable.enabled) ; else maghribButton.setBackgroundResource(R.drawable.disabled);
            if(SalatData.shared.salatList.get(4).isEnabled )  ishaeButton.setBackgroundResource(R.drawable.enabled) ; else ishaeButton.setBackgroundResource(R.drawable.disabled);

            }
        System.out.println(SalatData.shared.salatList.size());




    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void cusSave(String dur) {
        customizeFragment.dismiss();
        tempDuration=dur;
        saveLastUpdate();loadData();
        settingButton.clearAnimation();
    }

    public class BackGroundProcessing extends AsyncTask<LocationManager, LocationManager,Integer> {
        Boolean isDataRetrieved=false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            txt6.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.bounce));
            findButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotate));
            locationIcon.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.bounce));
            txt1.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.blink_anim));
            txt2.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.blink_anim));
            txt3.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.blink_anim));
            txt4.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.blink_anim));
            txt5.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.blink_anim));
        }

        @Override
        protected Integer doInBackground(LocationManager... locationManagers) {

            if(isNetworkAvailable()) {
                double longitude = 0.0;
                double latitude = 0.0;
                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double longitude0 = 0.0;
                        double latitude0 = 0.0;
                        if (location != null) {
                            longitude0 = location.getLongitude();
                            latitude0 = location.getLatitude();
                        }
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US);
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude0, longitude0, 1);
                            if (addresses.size() != 0) {
                                cityName = addresses.get(0).getAdminArea();
                                city_check = false;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        saveLastUpdate();

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                locationManagers[0] = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                //check whether gps is enabled   heeeeeeeeeeeeeeeeeeeeeeeeeeeere

                 boolean isGPSEnabled = locationManagers[0].isProviderEnabled(LocationManager.GPS_PROVIDER);

               if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Please Enable GPS", Toast.LENGTH_SHORT).show();

                }
            if (isGPSEnabled) {
                locationManagers[0].requestLocationUpdates(LocationManager.GPS_PROVIDER, 600, 1000,locationListener, Looper.getMainLooper());
                Location location = locationManagers[0].getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null){
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US);
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        if(addresses.size()!=0) {
                            cityName = addresses.get(0).getAdminArea();
                            city_check = false;
                        }
                    }catch (IOException e){e.printStackTrace();}
                    saveLastUpdate();
                }

            }

                boolean isNETWORKEnabled = locationManagers[0].isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (isNETWORKEnabled && location == null) {
                    locationManagers[0].requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5000, locationListener, Looper.getMainLooper());
                    Location location = locationManagers[0].getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    if (location != null) {
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();

                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US);
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            if (addresses.size() != 0) {
                                cityName = addresses.get(0).getAdminArea();
                                city_check = false;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        saveLastUpdate();
                    }

                } else {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Please enable GPS", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                SharedPreferences savedCityName = getPreferences(Context.MODE_PRIVATE);
                String citynameModified = savedCityName.getString(KEY_SAVE_CITY, "Unknown");
                citynameModified = modify_cityname(citynameModified);
                if (!citynameModified.equals("nowhere") && !citynameModified.equals("Unknown")) {
                    String url = "https://api.pray.zone/v2/times/today.json?city="+citynameModified;
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {

                                        JSONObject res = response.getJSONObject("results");
                                        JSONArray jsonArray = res.getJSONArray("datetime");
                                        JSONObject times = jsonArray.getJSONObject(0);
                                        JSONObject salats = times.getJSONObject("times");
                                        boolean Morroco_check= res.getJSONObject("location").getString("country").equals("Morocco");

                                        Salat subh = new Salat(1, !Morroco_check?salats.getString("Fajr"):addHours(salats.getString("Fajr")), false);
                                        Salat duhr = new Salat(2, !Morroco_check?salats.getString("Dhuhr"):addHours(salats.getString("Dhuhr")), false);
                                        Salat asr = new Salat(3, !Morroco_check?salats.getString("Asr"):addHours(salats.getString("Asr")), false);
                                        Salat maghrib = new Salat(4, !Morroco_check?salats.getString("Maghrib"):addHours(salats.getString("Maghrib")), false);
                                        Salat isha = new Salat(5, !Morroco_check?salats.getString("Isha"):addHours(salats.getString("Isha")), false);
                                        if (SalatData.shared.salatList.size() != 0) {
                                            subh.isEnabled = SalatData.shared.salatList.get(0).isEnabled;
                                            duhr.isEnabled = SalatData.shared.salatList.get(1).isEnabled;
                                            asr.isEnabled = SalatData.shared.salatList.get(2).isEnabled;
                                            maghrib.isEnabled = SalatData.shared.salatList.get(3).isEnabled;
                                            isha.isEnabled = SalatData.shared.salatList.get(4).isEnabled;

                                        }
                                        SalatData.shared.salatList.clear();
                                        SalatData.shared.salatList.add(subh);
                                        SalatData.shared.salatList.add(duhr);
                                        SalatData.shared.salatList.add(asr);
                                        SalatData.shared.salatList.add(maghrib);
                                        SalatData.shared.salatList.add(isha);
                                        saveLastUpdate();
                                        System.out.println(SalatData.shared.salatList.size());
                                        Log.d("1", "retrieved time success !!");
                                        loadData();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.d("JSON", "onErrorResponse: cnx lost ");

                                    }

                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Log.d("JSON", "onErrorResponse: Couldnt retrieve data ");
                        }
                    });
                    mQueue.add(request);
                }
            }
            else
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"please Make sure You have a ConneXion",Toast.LENGTH_SHORT).show();
                    }
                });

        return isDataRetrieved?1:0; }

        @Override
        protected void onPostExecute(Integer  o) {

            txt6.setText(cityName);
            locationIcon.clearAnimation();
            findButton.clearAnimation();
            txt1.clearAnimation();
            txt2.clearAnimation();
            txt3.clearAnimation();
            txt4.clearAnimation();
            txt5.clearAnimation();
        }
    }


    public void fetchData(View view) {
       if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        new BackGroundProcessing().execute(locationManager);
       else
           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},123);

    }
    public String addHours(String timer )
    {
        try {
            SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm");
            Date date=dateFormat.parse(timer);
            date.setTime(date.getTime()+60*60*1000);
            String dateString=dateFormat.format(date.getTime());
            return dateString;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timer;


    }

    public void enableService()
    {
        if(!isServiceEnabled)
        {
            Intent my_intent=new Intent(this,ActiveService.class);
            my_intent.putExtra("dur",tempDuration);
        startService(my_intent);
        isServiceEnabled=true;}
    }
    public void disableService()
    {
        if(check_switches_and()) {
            stopService(new Intent(this, ActiveService.class));
            isServiceEnabled=false;
        }
    }
    public static boolean check_switches_and()
    {
        if(SalatData.shared.salatList.size()!=0)
            if (!SalatData.shared.salatList.get(0).isEnabled && !SalatData.shared.salatList.get(1).isEnabled && !SalatData.shared.salatList.get(2).isEnabled && !SalatData.shared.salatList.get(3).isEnabled && !SalatData.shared.salatList.get(4).isEnabled)
                return true;


            return false;
    }

    public void switchBtnTapped(View view) {
        if (SalatData.shared.salatList.size() == 0) {
            Toast.makeText(getApplicationContext(), "please make sure prayer times are updated", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            case R.id.subhswitch:
                if (SalatData.shared.salatList.get(0).isEnabled) {
                    SalatData.shared.salatList.get(0).isEnabled = false;
                    subhButton.setBackgroundResource(R.drawable.disabled);
                    subhButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fadein));
                    disableService();
                } else {
                    SalatData.shared.salatList.get(0).isEnabled = true;
                    subhButton.setBackgroundResource(R.drawable.enabled);
                    subhButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fadein));
                    enableService();

                }
                saveLastUpdate();
                break;
            case R.id.duhr_switch:
                if (SalatData.shared.salatList.get(1).isEnabled) {
                    SalatData.shared.salatList.get(1).isEnabled = false;
                    duhrButton.setBackgroundResource(R.drawable.disabled);
                    duhrButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fadein));
                    disableService();
                } else {
                    SalatData.shared.salatList.get(1).isEnabled = true;
                    duhrButton.setBackgroundResource(R.drawable.enabled);
                    duhrButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fadein));
                    enableService();
                }
                saveLastUpdate();
                break;
            case R.id.asr_switch:
                if (SalatData.shared.salatList.get(2).isEnabled) {
                    SalatData.shared.salatList.get(2).isEnabled = false;
                    asrButton.setBackgroundResource(R.drawable.disabled);
                    asrButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fadein));
                    disableService();
                } else {
                    SalatData.shared.salatList.get(2).isEnabled = true;
                    asrButton.setBackgroundResource(R.drawable.enabled);
                    asrButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fadein));
                    enableService();
                }
                saveLastUpdate();
                break;

            case R.id.maghrib_switch:
                if (SalatData.shared.salatList.get(3).isEnabled) {
                    SalatData.shared.salatList.get(3).isEnabled = false;
                    maghribButton.setBackgroundResource(R.drawable.disabled);
                    maghribButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fadein));
                    disableService();
                } else {
                    SalatData.shared.salatList.get(3).isEnabled = true;
                    maghribButton.setBackgroundResource(R.drawable.enabled);
                    maghribButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fadein));
                    enableService();
                }
                saveLastUpdate();
                break;

            case R.id.ishae_switch:
                if (SalatData.shared.salatList.get(4).isEnabled) {
                    SalatData.shared.salatList.get(4).isEnabled = false;
                    ishaeButton.setBackgroundResource(R.drawable.disabled);
                    ishaeButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fadein));
                    disableService();
                } else {
                    SalatData.shared.salatList.get(4).isEnabled = true;
                    ishaeButton.setBackgroundResource(R.drawable.enabled);
                    ishaeButton.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.fadein));
                    enableService();
                }
                saveLastUpdate();
                break;
            default:
                break;
        }}
}
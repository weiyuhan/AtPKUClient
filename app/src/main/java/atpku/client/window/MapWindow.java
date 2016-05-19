package atpku.client.window;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import atpku.client.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JIANG YUMENG on 2016/5/14.
 * Main map class.
 */
public class MapWindow extends Activity implements
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener,
        AMap.OnMapLoadedListener {
    private LocationManager lm;
    private MapView mapView;
    public static AMap aMap;
    public static Timer timer;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        timer = new Timer();
        timer.schedule(task, 0, 2000);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();
        ToggleButton tb = (ToggleButton) findViewById(R.id.changeMapTypeButton);
        tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                else
                    aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            }
        });
        //检查权限
        checkPermission(LocationManager.GPS_PROVIDER, 0, 0);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 8,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location loc) {
                        updatePosition(loc);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        refreshmyinfo();
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        checkPermission(LocationManager.GPS_PROVIDER, 0, 0);
                        refreshmyinfo();
                        updatePosition(lm.getLastKnownLocation(provider));
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        refreshmyinfo();
                    }
                });
    }
    public void refreshmyinfo() {
        Thread getinfo = new getmyinfoThread();
        getinfo.start();
        try {
            getinfo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private class getmyinfoThread extends Thread {
        public void run() {

        }
    }
    private void updatePosition(Location location) {
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cu = CameraUpdateFactory.changeLatLng(pos);
        aMap.moveCamera(cu);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(pos);
        Marker me = aMap.addMarker(markerOptions);
        me.setTitle("me");
    }
    private void init() {
        aMap = mapView.getMap();
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnMapLoadedListener(this);
    }
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    public boolean onMarkerClick(final Marker marker) {
        if(marker.getTitle().equals("me")) {
            ComponentName comp = new ComponentName(MapWindow.this,
                    UserInfoWindow.class);
            Intent intent = new Intent();
            intent.setComponent(comp);
            startActivity(intent);
        }
        return true;
    }
    public void onInfoWindowClick(Marker marker) {

    }
    public void onMapLoaded() {

    }
    TimerTask task = new TimerTask() {
        @Override
        public void run() {

        }
    };
}
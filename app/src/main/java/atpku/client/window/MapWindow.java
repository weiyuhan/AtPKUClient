package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.amap.api.maps.*;
import com.amap.api.maps.model.*;
import com.amap.api.location.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import atpku.client.R;

/**
 * Created by JIANG YUMENG on 2016/5/14.
 * Main map class.
 */
public class MapWindow extends Activity implements ListView.OnItemClickListener, AMapLocationListener, LocationSource
{
    private MapView mapView;
    public static AMap aMap;

    public ActionBar actionBar;

    public ListView slideMenu;

    public DrawerLayout drawerLayout;

    public static boolean isLogin = false;

    public static double pkuLng = 116.31059288978577;
    public static double pkuLat = 39.99183503192985;
    public static LatLng pkuPos = new LatLng(pkuLat,pkuLng);

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        slideMenu = (ListView)findViewById(R.id.left_drawer);
        slideMenu.setOnItemClickListener(this);
        refreshSlideMenu();

        actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pkuPos, 16));

    }


    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void refreshSlideMenu()
    {
        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this, R.layout.drawer_list_item1);
        if(!isLogin)
        {
            adapter.add("登录");
            adapter.add("使用说明");
        }
        else
        {
            adapter.add("登出");
            adapter.add("使用说明");
            adapter.add("用户信息");
            adapter.add("高级搜索");
            adapter.add("发送信息");
        }

        slideMenu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        switch (position)
        {
            case 0:
                if(isLogin)
                {
                    isLogin = false;
                    refreshSlideMenu();
                }
                else
                {
                    Intent intent = new Intent(this, LoginWindow.class);
                    startActivity(intent);
                }
                break;
            case 1: {
                Intent intent = new Intent(this, HelpWindow.class);
                startActivity(intent);
            }
                break;
            case 2: {
                Intent intent = new Intent(this, UserInfoWindow.class);
                startActivity(intent);
            }
                break;
            case 3: {
                Intent intent = new Intent(this, SearchMsgWindow.class);
                startActivity(intent);
            }
                break;
            case 4:{
                Intent intent = new Intent(this, SendMsgWindow.class);
                startActivity(intent);
            }
                break;
            default:
                break;

        }
    }

    public void switchMapType()
    {
        if(aMap.getMapType() == AMap.MAP_TYPE_NORMAL)
        {
            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        }
        else
        {
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        }
    }

    private void init()
    {
        if(aMap == null)
        {
            aMap = mapView.getMap();
            ;
            aMap.setMyLocationEnabled(true);
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.getUiSettings().setCompassEnabled(true);


            aMap.setLocationSource(this);// 设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        }
    }
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "hahah", Toast.LENGTH_LONG).show();
        drawerLayout.closeDrawers();
        refreshSlideMenu();
        mapView.onResume();
    }
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    public boolean onOptionsItemSelected(MenuItem mi)
    {
        if(mi.isCheckable())
        {
            mi.setChecked(true);
        }

        switch (mi.getItemId())
        {
            case R.id.action_switch:
                switchMapType();
                break;
            default:
                break;
        }
        return true;

    }

    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                //System.out.println("!#%$################################################################################");
                LatLng currPos = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                double distance = AMapUtils.calculateLineDistance(currPos, pkuPos);
                if(distance < 1000)
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                else
                {
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(pkuPos));
                }
            }
        }
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
//设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
//设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
//设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(true);
//设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(5000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
}
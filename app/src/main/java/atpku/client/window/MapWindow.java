package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.amap.api.maps.*;
import com.amap.api.maps.model.*;
import com.amap.api.location.*;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushSettings;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import atpku.client.R;
import atpku.client.util.BitMapTarget;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.Message;
import atpku.client.model.Place;
import atpku.client.model.PostResult;
import atpku.client.model.User;

/**
 * Created by JIANG YUMENG on 2016/5/14.
 * Main map class.
 */
public class MapWindow extends Activity implements
        ListView.OnItemClickListener, AMapLocationListener, LocationSource,
        SearchView.OnQueryTextListener, AMap.OnMarkerClickListener, AMap.OnCameraChangeListener,
        AMap.OnMapClickListener
{
    private MapView mapView;
    public static AMap aMap;

    public ActionBar actionBar;

    public ListView slideMenu;

    public DrawerLayout drawerLayout;

    public SearchView search;

    public static boolean isLogin = false;


    private static double pkuLng = 116.31059288978577;
    private static double pkuLat = 39.99183503192985;
    private static LatLng pkuPos = new LatLng(pkuLat,pkuLng);
    private static float defaultZoom = 16;
    private static double maxDistance = 2000;
    private static float minZoom = 15;

    private LatLng currPos;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private com.android.volley.RequestQueue volleyQuque;
    private Marker shownMarker = null;

    private static String cookie = null;

    public static Map<String, Place> places;
    public static Map<String, Marker> markers;

    public static User user = new User();

    public static String deviceid;



    public static String getCookie()
    {
        if(cookie != null)
            return cookie;
        return "";
    }

    public static void setCookie(String cookie)
    {
        MapWindow.cookie = cookie;
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        PushSettings.enableDebugMode(getApplicationContext(), true);

        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "ZEug10Z4X0y5ek5ll0wplTIV");


        currPos = pkuPos;

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        slideMenu = (ListView)findViewById(R.id.left_drawer);
        slideMenu.setOnItemClickListener(this);
        refreshSlideMenu();

        actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(true);;

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        volleyQuque = Volley.newRequestQueue(this);
        init(); // 初始化地图

        SharedPreferences prefs = getSharedPreferences("userInfo", 1);
        user = JSON.parseObject(prefs.getString("userInfoJson", "{}"), User.class);

        //loadAvatarIcon();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshPlaces();
            }
        }, 120000, 120000); // for each 2 min, refresh all messages about places
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        search = (SearchView)searchItem.getActionView();
        search.setOnQueryTextListener(this);


        boolean ret =  super.onCreateOptionsMenu(menu);


        return ret;
    }

    public void loadAvatarIcon()
    {
        BitMapTarget target = new BitMapTarget();
        String avatarUrl = MapWindow.user.avatarIntoCycle();
        Picasso.with(this).load(avatarUrl).into(target);
        Drawable drawable = new BitmapDrawable(target.bitmap);
        actionBar.setIcon(drawable);
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
            if (user.getIsAdmin())
            {
                adapter.add("管理用户");
                adapter.add("处理举报");
            }
        }

        slideMenu.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id)   //  侧滑菜单
    {
        switch (position)
        {
            case 0:
                logOutOrLogIn();
                break;
            case 1: {  //使用说明
                Intent intent = new Intent(this, HelpWindow.class);
                startActivity(intent);
            }
                break;
            case 2:
                startUserInfoWindow();
                break;
            case 3: {  //高级搜索
                Intent intent = new Intent(this, SearchMsgWindow.class);
                startActivity(intent);
            }
                break;
            case 4:{   //发信息
                Place nearPlace = null;
                double minDistance = 50000;
                for(String placename:MapWindow.places.keySet())
                {
                    Place place = places.get(placename);
                    LatLng placePos = new LatLng(place.getLat(), place.getLng());
                    double distance = AMapUtils.calculateLineDistance(currPos, placePos);
                    if(distance < minDistance)
                    {
                        minDistance = distance;
                        nearPlace = place;
                    }
                }
                Intent intent = new Intent(this, SendMsgWindow.class);
                Bundle bundle = new Bundle();
                if(nearPlace != null)
                    bundle.putSerializable("placeId", nearPlace.getId());
                else
                    bundle.putSerializable("placeId", -1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
                break;
            case 5:{
                Intent intent = new Intent(this, UserListWindow.class);
                startActivity(intent);
            }
            break;
            case 6:{
                Intent intent = new Intent(this, ReportHandlingWindow.class);
                startActivity(intent);
            }
            break;
            default:
                break;

        }
    }

    public void startUserInfoWindow()
    {
        Intent intent = new Intent(MapWindow.this, UserInfoWindow.class);
        startActivity(intent);
        System.out.println(user);
    }

    public void logOutOrLogIn()
    {
        if(isLogin) //登出
        {
            Map<String, String> params = new HashMap<String, String>();
            params.put("deviceid", MapWindow.deviceid);
            StringRequestWithCookie stringRequest = new StringRequestWithCookie(StringRequest.Method.POST,"http://139.129.22.145:5000/logout",
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            PostResult result = JSON.parseObject(response, PostResult.class);
                            if(result.success)
                            {
                                Toast.makeText(MapWindow.this, result.message , Toast.LENGTH_LONG).show();
                                MapWindow.isLogin = false;
                                refreshSlideMenu();
                                SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editPrefs = prefs.edit();
                                editPrefs.remove("Cookie");
                                editPrefs.apply();
                            }
                            else
                            {
                                Toast.makeText(MapWindow.this, result.message, Toast.LENGTH_LONG).show();
                            }
                            Log.d("TAG", response);
                        }
                    }, params);
            volleyQuque.add(stringRequest);
        }
        else //登录
        {
            Intent intent = new Intent(this, LoginWindow.class);
            startActivity(intent);
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

    private void init()   //初始化高德地图
    {
        if(aMap == null)
        {
            aMap = mapView.getMap();

            aMap.setMyLocationEnabled(true);
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);
            aMap.getUiSettings().setCompassEnabled(true);

            aMap.setOnMarkerClickListener(this);// 设置Marker点击监听
            aMap.setLocationSource(this);// 设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pkuPos, defaultZoom));
            aMap.setOnCameraChangeListener(this);
            aMap.setOnMapClickListener(this);
            refreshMarkers();
        }
    }
    protected void onResume() {
        super.onResume();
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

        switch (mi.getItemId()) {
            case R.id.action_switch:
                switchMapType();
                break;
            case R.id.action_refresh: {
                refreshPlaces();
            }
                break;
            case R.id.action_search:{
                if (search != null)
                    search.setOnQueryTextListener(this);
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void refreshPlaces()
    {
        // 获取所有place并刷新所有Marker的显示
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.GET,"http://139.129.22.145:5000/places?lngbeg=-180&lngend=180&latbeg=-90&latend=90",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if(result.success)
                        {
                            String jsonPlaces = result.data;
                            List<Place> places = JSON.parseArray(jsonPlaces, Place.class);
                            System.out.println(places);
                            if(MapWindow.places == null) {
                                MapWindow.places = new HashMap<String, Place>();
                            }
                            if(MapWindow.markers == null)
                            {
                                MapWindow.markers = new HashMap<String, Marker>();
                            }
                            for(final Place place: places)
                            {
                                MapWindow.places.put(place.getName(), place);
                                StringRequestWithCookie globalMsgRequest = new StringRequestWithCookie(Request.Method.GET,
                                        "http://139.129.22.145:5000/hotmessages/" + String.valueOf(place.getId()),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response)
                                            {
                                                String placename = place.getName();
                                                PostResult result = JSON.parseObject(response, PostResult.class);
                                                System.out.println(result);
                                                if (result.success)
                                                {
                                                    List<Message> messages = JSON.parseArray(result.data, Message.class);;
                                                    place.setGlobalMessages(messages);
                                                    Marker marker = MapWindow.markers.get(placename);
                                                    if(marker != null && messages != null)
                                                    {
                                                        marker.setSnippet(place.snippetString());
                                                    }
                                                }
                                                Log.d("TAG", response);
                                            }
                                        }, null);
                                volleyQuque.add(globalMsgRequest);
                            }
                            refreshMarkers();
                        }
                        else
                        {
                            Toast.makeText(MapWindow.this, result.message, Toast.LENGTH_LONG).show();
                        }
                        Log.d("TAG", response);
                    }
                }, null);
        volleyQuque.add(stringRequest);
    }

    public void refreshMarkers() {
        if(MapWindow.markers == null) {
            MapWindow.markers = new HashMap<String, Marker>();
        }
        if(MapWindow.places == null)
        {
            MapWindow.places = new HashMap<String, Place>();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng pos;
        Set<String> keys = MapWindow.places.keySet();
        for(String placename: keys) {
            Place place = MapWindow.places.get(placename);
            pos = new LatLng(place.getLat(), place.getLng());
            markerOptions.position(pos);
            markerOptions.title(placename);
            Marker marker = aMap.addMarker(markerOptions);
            MapWindow.markers.put(placename, marker);
            marker.setSnippet(place.snippetString());
        }
    }

    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                currPos = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                double distance = AMapUtils.calculateLineDistance(currPos, pkuPos);
                if(distance < maxDistance)
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                else
                {
                    currPos = pkuPos;
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


    public boolean onQueryTextChange(String newText)
    {
        return true;
    }

    public boolean onQueryTextSubmit(String query)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        // 按标题搜索
        params.put("title", query);
        Intent intent = new Intent(MapWindow.this, SearchResultWindow.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("params", params);
        bundle.putSerializable("caller", "MapWindow");
        intent.putExtras(bundle);
        startActivity(intent);
        return false;
    }

    public boolean onMarkerClick(final Marker marker) {
        String placename = marker.getTitle();
        Place place = MapWindow.places.get(placename);
        int placeid = place.getId();
        if(marker.isInfoWindowShown()) {
            Intent intent = new Intent(this, PlaceWindow.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("id", placeid);
            bundle.putSerializable("name", placename);
            intent.putExtras(bundle);
            startActivity(intent);
            marker.hideInfoWindow();
            return true;
        }
        else {
            shownMarker = marker;
            return false;
        }
    }

    //对正在移动地图事件回调
    public void onCameraChange(CameraPosition cameraPosition)
    {
    }
    //对移动地图结束事件回调
    public void onCameraChangeFinish(CameraPosition cameraPosition)
    {
        LatLng target = cameraPosition.target;
        double distance = AMapUtils.calculateLineDistance(target, pkuPos);
        if(distance >= maxDistance)
        {
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(pkuPos));
        }
        float zoom = cameraPosition.zoom;
        if(zoom <= minZoom)
        {
            aMap.moveCamera(CameraUpdateFactory.zoomTo(minZoom));
        }
    }
    public void onMapClick(LatLng latLng) {
        if(shownMarker != null) {
            shownMarker.hideInfoWindow();
            shownMarker = null;
        }
    }
}
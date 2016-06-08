package atpku.client.window;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.alibaba.fastjson.JSON;
import com.amap.api.maps.*;
import com.amap.api.maps.model.*;
import com.amap.api.location.*;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.Message;
import atpku.client.model.Place;
import atpku.client.model.PostResult;
import atpku.client.model.User;
import atpku.client.util.ThemeUtil;

/**
 * Created by JIANG YUMENG on 2016/5/14.
 * Main map class.
 */
public class MapWindow extends AppCompatActivity implements
        AMapLocationListener, LocationSource,
        SearchView.OnQueryTextListener, AMap.OnMarkerClickListener, AMap.OnCameraChangeListener,
        AMap.OnMapClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static boolean mapShow;

    private MapView mapView;
    public AMap aMap;

    public ActionBar actionBar;

    public NavigationView slideMenu;

    public DrawerLayout drawerLayout;

    public SearchView searchView;

    public static boolean isLogin = false;

    public static boolean started = false;

    private static double pkuLng = 116.31059288978577;
    private static double pkuLat = 39.99183503192985;
    private static LatLng pkuPos = new LatLng(pkuLat, pkuLng);
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
    private ImageView drawerAvatar;
    private TextView drawerUsername;
    private TextView drawerEmail;


    public static String getCookie() {
        if (cookie != null)
            return cookie;
        return "";
    }

    public static void setCookie(String cookie) {
        MapWindow.cookie = cookie;
    }

    public void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        if (!started) {
            started = true;
            PushSettings.enableDebugMode(getApplicationContext(), true);
            PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, "ZEug10Z4X0y5ek5ll0wplTIV");
            //启动百度推送服务，等待PushTestReceiver的回调函数Onbind给MapWindow.deviceid赋初值，异步的
        }

        currPos = pkuPos;

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        slideMenu = (NavigationView) findViewById(R.id.navigation);
        slideMenu.setNavigationItemSelectedListener(this);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.mipmap.logo);
        actionBar.setDisplayUseLogoEnabled(true);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        init();

        drawerAvatar = (ImageView) findViewById(R.id.drawer_avatar);
        drawerUsername = (TextView) findViewById(R.id.drawer_username);
        drawerEmail = (TextView) findViewById(R.id.drawer_email);

        volleyQuque = Volley.newRequestQueue(this);

        refreshSlideMenu();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshPlaces();
            }
        }, 120000, 120000); // for each 2 min, refresh all messages about places



    }

    public void refreshUser() {
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(StringRequest.Method.GET, "http://139.129.22.145:5000/profile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            MapWindow.user = JSON.parseObject(result.data, User.class);
                            System.out.println(MapWindow.user);
                            SharedPreferences prefs = getSharedPreferences("userInfo", 1);
                            SharedPreferences.Editor mEditor = prefs.edit();
                            mEditor.putString("userInfoJson", result.data);
                            mEditor.apply();
                            mEditor.commit();
                        }
                        refreshSlideMenu();
                        Log.d("TAG", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Snackbar.make(findViewById(R.id.map_layout), "请检查网络连接", Snackbar.LENGTH_INDEFINITE).show();
                    }
                }, null);
        volleyQuque.add(stringRequest);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.menu_map, menu);
        boolean ret = super.onCreateOptionsMenu(menu);
        return ret;
    }

    public void loadDrawer() {
        if (MapWindow.isLogin && MapWindow.user != null) {
            drawerAvatar.setVisibility(View.VISIBLE);
            Picasso.with(this).load(MapWindow.user.avatarIntoCycle()).resize(144, 144).into(drawerAvatar);
            drawerUsername.setText(MapWindow.user.nickname);
            drawerEmail.setText(MapWindow.user.email);
        } else {
            drawerAvatar.setVisibility(View.INVISIBLE);
            drawerEmail.setText("");
            drawerUsername.setText("请登录");
        }
    }

    public void refreshSlideMenu() {
        loadDrawer();
        slideMenu.getMenu().clear(); //clear old inflated items.
        slideMenu.inflateMenu(R.menu.menu_drawer);
        slideMenu.setNavigationItemSelectedListener(this);
        Menu menu = slideMenu.getMenu();
        if (user != null && !user.isAdmin) {
            MenuItem menuItem = menu.findItem(R.id.drawer_manager);
            menuItem.setVisible(false);
        } else {
            MenuItem menuItem = menu.findItem(R.id.drawer_manager);
            menuItem.setVisible(true);
        }
        if (!MapWindow.isLogin) //未登录时
        {
            MenuItem menuItem = menu.findItem(R.id.drawer_logout);
            menuItem.setVisible(false);
            menuItem = menu.findItem(R.id.drawer_userinfo);
            menuItem.setVisible(false);
            menuItem = menu.findItem(R.id.drawer_setting);
            menuItem.setVisible(false);
        } else {
            MenuItem menuItem = menu.findItem(R.id.drawer_login);
            menuItem.setVisible(false);
        }
    }

    public void sendMsgHandler(View view) {
        startSendMsgWindow();
    }

    public void startSendMsgWindow() {
        if (!MapWindow.isLogin) {
            Snackbar.make(findViewById(R.id.map_layout), "请登录后再发送", Snackbar.LENGTH_INDEFINITE)
                    .setAction("登录", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            logOutOrLogIn();
                        }
                    }).setActionTextColor(getResources().getColor(R.color.white)).show();
            return;
        }
        Place nearPlace = null;
        double minDistance = 50000;
        for (String placename : MapWindow.places.keySet()) {
            Place place = places.get(placename);
            LatLng placePos = new LatLng(place.getLat(), place.getLng());
            double distance = AMapUtils.calculateLineDistance(currPos, placePos);
            if (distance < minDistance) {
                minDistance = distance;
                nearPlace = place;
            }
        }
        Intent intent = new Intent(this, SendMsgWindow.class);
        Bundle bundle = new Bundle();
        if (nearPlace != null)
            bundle.putSerializable("placeId", nearPlace.getId());
        else
            bundle.putSerializable("placeId", -1);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void startUserInfoWindow() {
        Intent intent = new Intent(MapWindow.this, UserInfoWindow.class);
        startActivity(intent);
        System.out.println(user);
    }

    public void logOutOrLogIn() {
        if (isLogin) //登出
        {
            Map<String, String> params = new HashMap<String, String>();
            params.put("deviceid", MapWindow.deviceid);
            StringRequestWithCookie stringRequest = new StringRequestWithCookie(StringRequest.Method.POST, "http://139.129.22.145:5000/logout",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            PostResult result = JSON.parseObject(response, PostResult.class);
                            if (result.success) {
                                MapWindow.isLogin = false;
                                refreshSlideMenu();
                                SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editPrefs = prefs.edit();
                                editPrefs.remove("Cookie");
                                editPrefs.apply();
                                Snackbar.make(findViewById(R.id.map_layout), result.message, Snackbar.LENGTH_LONG).show();
                            }
                            else
                            {
                                MapWindow.isLogin = false;
                                refreshSlideMenu();
                                Snackbar.make(findViewById(R.id.map_layout), "未正常登出", Snackbar.LENGTH_LONG).show();
                            }
                            Log.d("TAG", response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Snackbar.make(findViewById(R.id.map_layout), "登出失败，请检查网络连接", Snackbar.LENGTH_INDEFINITE).show();
                        }
                    }, params);
            volleyQuque.add(stringRequest);
        } else //登录
        {
            Intent intent = new Intent(this, LoginWindow.class);
            startActivity(intent);
        }
    }

    public void switchMapType() {
        if (aMap.getMapType() == AMap.MAP_TYPE_NORMAL) {
            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
        } else {
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        }
    }

    private void init()   //初始化高德地图
    {
        if(aMap == null) {
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

        }

        refreshMarkers();
    }

    protected void onResume() {
        super.onResume();
        mapView.onResume();
        drawerLayout.closeDrawers();
        if (MapWindow.isLogin) {
            refreshUser();
        }
        else
            refreshSlideMenu();
        if (ThemeUtil.themeChanged) {
            ThemeUtil.themeChanged = false;
            //mapShow = false;
            finish();
            Intent intent = new Intent(getApplicationContext(), MapWindow.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            markers.clear();
            startActivity(intent);
        }
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
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.isCheckable()) {
            menuItem.setChecked(true);
        }
        switch (menuItem.getItemId()) {
            case R.id.drawer_help: {  //使用说明
                Intent intent = new Intent(this, HelpWindow.class);
                startActivity(intent);
            }
            break;
            case R.id.drawer_setting: {   //发信息
                Intent intent = new Intent(this, SettingWindow.class);
                startActivity(intent);
            }
            break;
            case R.id.drawer_search: {  //高级搜索
                Intent intent = new Intent(this, SearchMsgWindow.class);
                startActivity(intent);
            }
            break;
            case R.id.drawer_userinfo: {  //用户信息
                startUserInfoWindow();
            }
            break;
            case R.id.drawer_login: {  //登录
                logOutOrLogIn();
            }
            break;
            case R.id.drawer_logout: {  //登录
                logOutOrLogIn();
            }
            break;
            case R.id.drawer_manageUser: {
                Intent intent = new Intent(this, UserListWindow.class);
                startActivity(intent);
            }
            break;
            case R.id.drawer_manageReport: {
                Intent intent = new Intent(this, ReportHandlingWindow.class);
                startActivity(intent);
            }
            break;
            default:
                break;
        }
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.isCheckable()) {
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
            case R.id.action_search: {
                //mi.expandActionView();
                searchView = (SearchView) MenuItemCompat.getActionView(mi);
                if (searchView != null)
                    searchView.setOnQueryTextListener(this);
            }
            break;
            default:
                break;
        }
        return true;
    }

    public void refreshPlaces() {
        // 获取所有place并刷新所有Marker的显示
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.GET, "http://139.129.22.145:5000/places?lngbeg=-180&lngend=180&latbeg=-90&latend=90",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            String jsonPlaces = result.data;
                            List<Place> places = JSON.parseArray(jsonPlaces, Place.class);
                            System.out.println(places);
                            if (MapWindow.places == null) {
                                MapWindow.places = new HashMap<String, Place>();
                            }
                            if (MapWindow.markers == null) {
                                MapWindow.markers = new HashMap<String, Marker>();
                            }
                            for (final Place place : places) {
                                MapWindow.places.put(place.getName(), place);
                                StringRequestWithCookie globalMsgRequest = new StringRequestWithCookie(Request.Method.GET,
                                        "http://139.129.22.145:5000/hotmessages/" + String.valueOf(place.getId()),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                String placename = place.getName();
                                                PostResult result = JSON.parseObject(response, PostResult.class);
                                                System.out.println(result);
                                                if (result.success) {
                                                    List<Message> messages = JSON.parseArray(result.data, Message.class);
                                                    place.setGlobalMessages(messages);
                                                    MapWindow.places.put(place.getName(), place);
                                                    Marker marker = MapWindow.markers.get(placename);
                                                    if (marker != null && messages.size() > 0) {
                                                        marker.setSnippet(messages.get(0).snippetString());
                                                    }
                                                }
                                                Log.d("TAG", response);
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError volleyError) {
                                                Snackbar.make(findViewById(R.id.map_layout), "请检查网络连接", Snackbar.LENGTH_INDEFINITE).show();
                                            }
                                        }, null);
                                volleyQuque.add(globalMsgRequest);
                            }
                        } else {
                            Snackbar.make(findViewById(R.id.map_layout), result.message, Snackbar.LENGTH_LONG).show();
                        }
                        Log.d("TAG", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Snackbar.make(findViewById(R.id.map_layout), "请检查网络连接", Snackbar.LENGTH_INDEFINITE).show();
                    }
                }, null);
        volleyQuque.add(stringRequest);
    }

    public void refreshMarkers() {
        if (MapWindow.markers == null) {
            MapWindow.markers = new HashMap<String, Marker>();
        }
        if (MapWindow.places == null) {
            MapWindow.places = new HashMap<String, Place>();
        }
        Set<String> keys = MapWindow.places.keySet();
        for (String placename : keys) {
            Place place = MapWindow.places.get(placename);
            Marker marker = MapWindow.markers.get(placename);
            if(marker == null)
            {
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng pos;
                pos = new LatLng(place.getLat(), place.getLng());
                markerOptions.position(pos);
                markerOptions.title(placename);
                setMarkerIcon(markerOptions, place.type);
                System.out.println("addMarker " + placename);
                marker = aMap.addMarker(markerOptions);
                markers.put(placename, marker);
            }
            marker.setSnippet(place.snippetString());
        }
    }

    public static void setMarkerIcon(MarkerOptions markerOptions, String type) {
        if (type.equals("building"))
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.building_school));
        else if (type.equals("canteen"))
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.building_canteen));
        else if (type.equals("tower"))
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.building_tower));
        else if (type.equals("gate"))
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.building_gate));
        else if (type.equals("hospital"))
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.building_hospital));
        else
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.building_school));
    }

    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                currPos = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                double distance = AMapUtils.calculateLineDistance(currPos, pkuPos);
                if (distance < maxDistance)
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                else {
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


    public boolean onQueryTextChange(String newText) {
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
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
        if (marker.isInfoWindowShown() || shownMarker == marker) {
            System.out.println("Double click on marker.");
            Intent intent = new Intent(this, PlaceWindow.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("id", placeid);
            bundle.putSerializable("name", placename);
            intent.putExtras(bundle);
            startActivity(intent);
            marker.hideInfoWindow();
            shownMarker = null;
        } else {
            System.out.println("Single click on marker.");
            marker.showInfoWindow();
            shownMarker = marker;
        }
        return true;
    }

    //对正在移动地图事件回调
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    //对移动地图结束事件回调
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LatLng target = cameraPosition.target;
        double distance = AMapUtils.calculateLineDistance(target, pkuPos);
        if (distance >= maxDistance) {
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(pkuPos));
        }
        float zoom = cameraPosition.zoom;
        if (zoom <= minZoom) {
            aMap.moveCamera(CameraUpdateFactory.zoomTo(minZoom));
        }
    }

    public void onMapClick(LatLng latLng) {
        if (shownMarker != null) {
            shownMarker.hideInfoWindow();
            shownMarker = null;
        }
    }

    public long mExitTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Snackbar.make(findViewById(R.id.map_layout), "再按一次退出程序", Snackbar.LENGTH_LONG).show();
            mExitTime = System.currentTimeMillis();
            return;
        }
        mapShow = false;
        android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
        System.exit(0);
    }
}
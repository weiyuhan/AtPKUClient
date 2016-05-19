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

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

import atpku.client.R;

/**
 * Created by JIANG YUMENG on 2016/5/14.
 * Main map class.
 */
public class MapWindow extends Activity implements ListView.OnItemClickListener
{
    private LocationManager lm;
    private MapView mapView;
    public static AMap aMap;

    public ActionBar actionBar;

    public ListView slideMenu;

    public DrawerLayout drawerLayout;

    public static boolean isLogin = false;

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

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        init();

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
            case 1:
                break;
            case 2:
                Intent intent = new Intent(this, UserInfoWindow.class);
                startActivity(intent);
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
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
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
}
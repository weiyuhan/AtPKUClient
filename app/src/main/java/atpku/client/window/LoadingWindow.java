package atpku.client.window;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.maps.model.Marker;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atpku.client.R;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.Message;
import atpku.client.model.Place;
import atpku.client.model.PostResult;

/**
 * Created by wyh on 2016/5/19.
 */
public class LoadingWindow extends Activity
{
    private com.android.volley.RequestQueue volleyQuque;
    public static int loadingPlaceIndex = 0;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActionBar().hide(); // 隐藏actionBar
        setContentView(R.layout.loading);

        SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);
        String cookie = prefs.getString("Cookie", "");
        System.out.println("mycookie : " + cookie);
        MapWindow.setCookie(cookie);
        volleyQuque = Volley.newRequestQueue(this);
        initPlaces();
        /*
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {

                Intent mainIntent = new Intent(LoadingWindow.this, MapWindow.class);
                startActivity(mainIntent);
                String cookie = MapWindow.getCookie();
                if(!cookie.equals(""))
                {
                    MapWindow.isLogin = true;
                }
                finish();
            }
        }, 3000); //3000 for release
        */
    }
    public void initPlaces()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("lngbeg", "-180");
        params.put("lngend", "180");
        params.put("latbeg", "-90");
        params.put("latend", "90");
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
                            final int count = MapWindow.places.keySet().size();
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
                                                    Message message = JSON.parseObject(result.data, Message.class);;
                                                    place.setGlobalMessage(message);
                                                    Marker marker = MapWindow.markers.get(placename);
                                                    if(marker != null && message != null)
                                                    {
                                                        marker.setSnippet(place.snippetString());
                                                    }
                                                }
                                                Log.d("TAG", response);
                                                loadingPlaceIndex++;
                                                if(loadingPlaceIndex >= count)
                                                {
                                                    LoadingWindow.this.finish();
                                                }
                                            }
                                        }, null);
                                volleyQuque.add(globalMsgRequest);
                            }
                            Intent mainIntent = new Intent(LoadingWindow.this, MapWindow.class);
                            startActivity(mainIntent);
                            String cookie = MapWindow.getCookie();
                            if(!cookie.equals(""))
                            {
                                MapWindow.isLogin = true;
                            }
                        }
                        else
                        {
                            Toast.makeText(LoadingWindow.this, result.message, Toast.LENGTH_LONG).show();
                            finish();
                        }
                        Log.d("TAG", response);
                    }
                }, params);
        volleyQuque.add(stringRequest);
    }
}

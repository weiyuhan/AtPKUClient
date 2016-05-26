package atpku.client.util;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import atpku.client.window.MapWindow;

/**
 * Created by wyh on 2016/5/23.
 */
public class StringRequestWithCookie extends StringRequest
{
    private Map<String, String> params;
    public StringRequestWithCookie(int method, String url, Response.Listener<String> listener, Map<String,String> params)
    {
        super(method,url,listener,new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        this.params = params;
    }
    public StringRequestWithCookie(String url, Response.Listener<String> listener, Map<String,String> params)
    {
        super(url,listener,new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        this.params = params;
    }
    public StringRequestWithCookie(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String,String> params)
    {
        super(method,url,listener,errorListener);
        this.params = params;
    }
    public StringRequestWithCookie(String url, Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String,String> params)
    {
        super(url,listener,errorListener);
        this.params = params;
    }
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap localHashMap = new HashMap();
        localHashMap.put("cookie", MapWindow.getCookie());
        return localHashMap;
    }
    protected Map<String, String> getParams()
    {
        if(params == null)
            return new HashMap<String, String>();
        else
            return params;
    }

    protected Response<String> parseNetworkResponse(
            NetworkResponse response) {
        // TODO Auto-generated method stub
        try {
            String dataString = new String(response.data, "UTF-8");
            return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

}

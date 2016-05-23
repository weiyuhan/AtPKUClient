package atpku.client.httputil;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import atpku.client.window.MapWindow;

/**
 * Created by wyh on 2016/5/23.
 */
public class StringRequestWithCookie extends StringRequest
{
    private Map<String, String> params;
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
}

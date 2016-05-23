package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import atpku.client.R;
import atpku.client.model.PostResult;


public class LoginWindow extends Activity
{
    public EditText email;
    public EditText password;
    public Button loginButton;
    public Button registButton;
    public ActionBar actionBar;

    private RequestQueue volleyQuque;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = (EditText)findViewById(R.id.login_email);
        password = (EditText)findViewById(R.id.login_password);
        loginButton = (Button)findViewById(R.id.loginButton);
        registButton = (Button)findViewById(R.id.registButton);

        volleyQuque = Volley.newRequestQueue(this);
    }

    public void loginHandler(View source) //登录请求
    {
        Editable edt = email.getText();
        // you can add a check about whether user input @ here
        edt.append("@pku.edu.cn");
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST,"http://139.129.22.145:5000/login",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        System.out.println(result.toString());
                        if(result.success)
                        {
                            Toast.makeText(LoginWindow.this, "登录成功", Toast.LENGTH_LONG).show();
                            LoginWindow.this.finish();
                            MapWindow.isLogin = true;
                        }
                        else
                        {
                            Toast.makeText(LoginWindow.this, "登录失败 ：" + result.message, Toast.LENGTH_LONG).show();
                            MapWindow.isLogin = false;
                        }
                        Log.d("TAG", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //System.out.println("error : " + error.getMessage());
                        Log.e("TAG", error.getMessage(), error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                // TODO Auto-generated method stub
                try {

                    Map<String, String> responseHeaders = response.headers;
                    String rawCookies = responseHeaders.get("Set-Cookie");
                    System.out.println("getcookie : " + rawCookies);
                    String[] splitedRaw = rawCookies.split(";");
                    String Cookie = splitedRaw[0];

                    SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor mEditor = prefs.edit();
                    mEditor.putString("Cookie", Cookie);
                    mEditor.apply();

                    String dataString = new String(response.data, "UTF-8");
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };
        volleyQuque.add(stringRequest);
    }
    public void regisHandler(View source) //注册页面
    {
        Intent intent = new Intent(LoginWindow.this, RegisterWindow.class);
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem mi)
    {
        if(mi.isCheckable())
        {
            mi.setChecked(true);
        }

        switch (mi.getItemId())
        {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                break;
        }
        return true;

    }
}

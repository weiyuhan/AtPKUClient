package atpku.client.window;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
import atpku.client.model.User;
import atpku.client.util.ThemeUtil;

public class LoginWindow extends AppCompatActivity {
    public TextInputLayout email;
    public TextInputLayout password;
    public Button loginButton;
    public ActionBar actionBar;

    private RequestQueue volleyQuque;

    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = (TextInputLayout) findViewById(R.id.login_email);
        password = (TextInputLayout) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.loginButton);

        volleyQuque = Volley.newRequestQueue(this);
    }

    public void loginHandler(View source) //登录请求
    {
        Editable editEmail = email.getEditText().getText();
        if (editEmail.length() < 1) {
            email.setErrorEnabled(true);
            email.setError("请输入用户名");
            return;
        } else
            email.setErrorEnabled(false);
        if (password.getEditText().getText().length() < 1) {
            password.setErrorEnabled(true);
            password.setError("请输入密码");
            return;
        } else
            password.setErrorEnabled(false);
        if (!editEmail.toString().contains("@"))
            editEmail.append("@pku.edu.cn");
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, "http://139.129.22.145:5000/login",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        System.out.println(result.toString());
                        Snackbar.make(findViewById(R.id.login_layout), result.message, Snackbar.LENGTH_LONG).show();
                        if (result.success) {
                            User user = JSON.parseObject(result.data, User.class);
                            if (user.avatar == null)
                                user.avatar = "http://public-image-source.img-cn-shanghai.aliyuncs.com/avatar33201652203559.jpg";
                            MapWindow.user = user;
                            SharedPreferences prefs = getSharedPreferences("userInfo", 1);
                            SharedPreferences.Editor mEditor = prefs.edit();
                            mEditor.putString("userInfoJson", result.data);
                            mEditor.apply();
                            LoginWindow.this.finish();
                            MapWindow.isLogin = true;
                        } else {
                            MapWindow.isLogin = false;
                        }
                        Log.d("TAG", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Snackbar.make(findViewById(R.id.login_layout), "登录失败，请检查网络连接", Snackbar.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email.getEditText().getText().toString());
                params.put("password", password.getEditText().getText().toString());
                params.put("deviceid", MapWindow.deviceid);
                System.out.println(params);
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
                    if (rawCookies != null) {
                        String[] splitedRaw = rawCookies.split(";");
                        String Cookie = splitedRaw[0];

                        SharedPreferences prefs = getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor mEditor = prefs.edit();
                        mEditor.putString("Cookie", Cookie);
                        mEditor.apply();

                        MapWindow.setCookie(rawCookies);
                    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.isCheckable()) {
            mi.setChecked(true);
        }

        switch (mi.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_register:
                regisHandler(null);
                break;
            default:
                break;
        }
        return true;

    }
}

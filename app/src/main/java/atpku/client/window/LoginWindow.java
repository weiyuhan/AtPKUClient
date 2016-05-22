package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import atpku.client.R;


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
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST,"http://139.129.22.145:5000",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        System.out.println("response : " + response);
                        //Toast.makeText(LoginWindow.this, "it works!", Toast.LENGTH_LONG).show();
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

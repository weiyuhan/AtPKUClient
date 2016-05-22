package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import atpku.client.R;

public class RegisterWindow extends Activity
{
    public EditText studentNum;
    public EditText username;
    public EditText password;
    public EditText confirmPasswd;
    public RadioButton male;
    public RadioButton female;
    public RadioButton secret_sex;
    public Button submitButton;

    public ActionBar actionBar;

    private RequestQueue volleyQuque;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        studentNum = (EditText)findViewById(R.id.regist_studentNum);
        username = (EditText)findViewById(R.id.register_username);
        password = (EditText)findViewById(R.id.register_password);
        confirmPasswd = (EditText)findViewById(R.id.regist_confirmpasswd);
        male = (RadioButton)findViewById(R.id.register_male);
        female = (RadioButton)findViewById(R.id.register_female);
        secret_sex = (RadioButton)findViewById(R.id.register_secret);
        submitButton = (Button)findViewById(R.id.regist_submitButton);

        volleyQuque = Volley.newRequestQueue(this);
    }

    public void registSubmitHandler(View source)
    {
        final String stuNumStr = studentNum.getText().toString();
        if (stuNumStr.length() < 1)
        {
            Toast.makeText(this, "请输入学号", Toast.LENGTH_SHORT);
            return;
        }
        final String userNameStr = username.getText().toString();
        if (userNameStr.length() < 1)
        {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT);
            return;
        }
        final String passwordStr = password.getText().toString();
        if (passwordStr.length() < 1)
        {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT);
            return;
        }
        String confirmpwStr = confirmPasswd.getText().toString();
        if (passwordStr.length() < 1)
        {
            Toast.makeText(this, "请确认密码", Toast.LENGTH_SHORT);
            return;
        }
        else if(!confirmpwStr.equals(passwordStr))
        {
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_LONG);
            return;
        }
        String genderStrTmp = null;
        if(male.isActivated())
            genderStrTmp = "m";
        else
            genderStrTmp = "f";
        final String genderStr = genderStrTmp;

        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST,"http://139.129.22.145:5000",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        System.out.println("response : " + response);
                        //Toast.makeText(RegisterWindow.this, "it works!", Toast.LENGTH_LONG).show();
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
                }){
            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的参数
                Map<String, String> params = new HashMap<String, String>();
                params.put("nickname", userNameStr);
                params.put("email", stuNumStr + ".pku.edu.cn");
                params.put("password", passwordStr);
                params.put("gender", genderStr);
                return params;
            }
        };
        volleyQuque.add(stringRequest);


    }

    public void registerBackHandler(View source)
    {
        super.onBackPressed();
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

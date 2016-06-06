package atpku.client.window;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import atpku.client.R;
import atpku.client.model.PostResult;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.util.ThemeUtil;

public class RegisterWindow extends AppCompatActivity {
    public TextInputLayout studentNum;
    public TextInputLayout username;
    public TextInputLayout password;
    public TextInputLayout confirmPasswd;
    public RadioButton male;
    public RadioButton female;
    public Button submitButton;

    public ActionBar actionBar;

    private RequestQueue volleyQuque;

    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        studentNum = (TextInputLayout) findViewById(R.id.regist_studentNum);
        username = (TextInputLayout) findViewById(R.id.register_username);
        password = (TextInputLayout) findViewById(R.id.register_password);
        confirmPasswd = (TextInputLayout) findViewById(R.id.regist_confirmpasswd);
        male = (RadioButton) findViewById(R.id.register_male);
        female = (RadioButton) findViewById(R.id.register_female);
        submitButton = (Button) findViewById(R.id.regist_submitButton);

        volleyQuque = Volley.newRequestQueue(this);
    }

    public void registSubmitHandler(View source) {
        final String stuNumStr = studentNum.getEditText().getText().toString();
        if (stuNumStr.length() < 1) {
            studentNum.setErrorEnabled(true);
            studentNum.setError("请输入学号");
            return;
        } else {
            try {
                int num = Integer.parseInt(stuNumStr);
            } catch (NumberFormatException e) {
                studentNum.setError("请输入正确的学号");
                return;
            }
            studentNum.setErrorEnabled(false);
        }
        final String userNameStr = username.getEditText().getText().toString();
        if (userNameStr.length() < 1) {
            username.setErrorEnabled(true);
            username.setError("请输入用户名");
            return;
        } else
            username.setErrorEnabled(false);
        final String passwordStr = password.getEditText().getText().toString();
        if (passwordStr.length() < 1) {
            password.setErrorEnabled(true);
            password.setError("请输入密码");
            return;
        } else
            password.setErrorEnabled(false);
        String confirmpwStr = confirmPasswd.getEditText().getText().toString();
        if (passwordStr.length() < 1) {
            confirmPasswd.setErrorEnabled(true);
            confirmPasswd.setError("请确认密码");
            return;
        } else if (!confirmpwStr.equals(passwordStr)) {
            confirmPasswd.setErrorEnabled(true);
            confirmPasswd.setError("两次输入的密码不一致");
            return;
        } else
            confirmPasswd.setErrorEnabled(false);
        String genderStrTmp = null;
        if (male.isChecked())
            genderStrTmp = "m";
        else
            genderStrTmp = "f";
        final String genderStr = genderStrTmp;

        Map<String, String> params = new HashMap<String, String>();
        params.put("nickname", userNameStr);
        params.put("email", stuNumStr + "@pku.edu.cn");
        params.put("password", passwordStr);
        params.put("gender", genderStr);
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(StringRequest.Method.POST,
                "http://139.129.22.145:5000/register",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        System.out.println(result.toString());
                        Snackbar.make(findViewById(R.id.registLayout), result.message, Snackbar.LENGTH_LONG).show();
                        if (result.success) {
                            SharedPreferences prefs = getSharedPreferences("userinfo", 1);
                            SharedPreferences.Editor mEditor = prefs.edit();
                            mEditor.putString("stunum", stuNumStr);
                            mEditor.putString("nickname", userNameStr);
                            mEditor.putString("gender", genderStr);
                            mEditor.apply();
                            RegisterWindow.this.finish();
                        }
                        Log.d("TAG", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Snackbar.make(findViewById(R.id.registLayout), "请检查网络连接", Snackbar.LENGTH_LONG).show();
                    }
                }, params);
        volleyQuque.add(stringRequest);
    }

    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.isCheckable()) {
            mi.setChecked(true);
        }

        switch (mi.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                break;
        }
        return true;

    }
}

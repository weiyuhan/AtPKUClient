package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;

import atpku.client.R;
import atpku.client.model.Message;
import atpku.client.model.PostResult;
import atpku.client.util.StringRequestWithCookie;

/**
 * Created by JIANG YUMENG on 2016/5/28.
 */
public class EditMyInfoWindow extends Activity {
    public EditText username;
    public RadioButton male;
    public RadioButton female;
    public RadioButton secret_sex;
    public ActionBar actionBar;
    private RequestQueue volleyQuque;

    private boolean needSubmit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editmyinfo);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        username = (EditText)findViewById(R.id.editmyinfo_username);
        male = (RadioButton)findViewById(R.id.editmyinfo_male);
        female = (RadioButton)findViewById(R.id.editmyinfo_female);
        secret_sex = (RadioButton)findViewById(R.id.editmyinfo_secret);

        setTitle("修改个人资料");

        volleyQuque = Volley.newRequestQueue(this);
    }

    public void editMyInfoSubmitHandler(View source) {
        needSubmit = false;

        HashMap<String, String> params = new HashMap<String, String>();
        if (!username.getText().toString().equals("")) {
            needSubmit = true;
            params.put("nickname", username.getText().toString());
        }
        String genderStr = null;
        if(male.isChecked()) {
            System.out.println("male");
            needSubmit = true;
            genderStr = "m";
            params.put("gender", genderStr);
        }
        else if(female.isChecked()) {
            System.out.println("female");
            needSubmit = true;
            genderStr = "f";
            params.put("gender", genderStr);
        }
        else if(secret_sex.isChecked()){
            genderStr = "fuck!";
            Toast.makeText(EditMyInfoWindow.this, "服务器不支持私密性别！", Toast.LENGTH_LONG).show();
            // 服务器不支持私密性别！
        }

        if(needSubmit) {
            StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                    "http://139.129.22.145:5000/profile",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            PostResult result = JSON.parseObject(response, PostResult.class);
                            if(result.success) {
                                Toast.makeText(EditMyInfoWindow.this, result.message, Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else {
                                Toast.makeText(EditMyInfoWindow.this, result.message, Toast.LENGTH_LONG).show();
                            }
                            Log.d("TAG", response);
                        }
                    }, params);
            volleyQuque.add(stringRequest);
        }
        else {
            Toast.makeText(EditMyInfoWindow.this, "没有可以提交的东西！", Toast.LENGTH_LONG).show();
        }
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

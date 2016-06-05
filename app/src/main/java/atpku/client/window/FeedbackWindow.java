package atpku.client.window;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import atpku.client.R;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.PostResult;
import atpku.client.util.ThemeUtil;

/**
 * Created by JIANG YUMENG on 2016/5/14.
 * Allow user to submit feedback.
 */
public class FeedbackWindow extends AppCompatActivity {
    public ActionBar actionBar;
    private com.android.volley.RequestQueue volleyQuque;
    private Button bt;

    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        bt = (Button) findViewById(R.id.feedback_submit);

        volleyQuque = Volley.newRequestQueue(this);
    }

    public void FeedbackSubmitHandler(View source) //发送反馈
    {
        Map<String, String> params = new HashMap<String, String>();
        EditText et = (EditText) findViewById(R.id.feedbackContent);
        if (et.getText().toString().equals("")) {
            Toast.makeText(FeedbackWindow.this, "请填写内容再发送", Toast.LENGTH_LONG).show();
            return;
        }
        bt.setEnabled(false);
        bt.setText("发送中");
        params.put("content", et.getText().toString());
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(StringRequest.Method.POST, "http://139.129.22.145:5000/feedback",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        bt.setEnabled(true);
                        bt.setText("提交反馈");
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            Toast.makeText(FeedbackWindow.this, "发送成功", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(FeedbackWindow.this, result.message, Toast.LENGTH_LONG).show();
                        }
                        Log.d("TAG", response);
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

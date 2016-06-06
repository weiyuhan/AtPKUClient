package atpku.client.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atpku.client.R;
import atpku.client.model.Feedback;
import atpku.client.model.PostResult;
import atpku.client.model.User;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.util.ThemeUtil;

/**
 * Created by wyh on 2016/6/6.
 */
public class MyFeedbackWindow extends AppCompatActivity
{
    public ActionBar actionBar;
    private com.android.volley.RequestQueue volleyQuque;
    public ListView feedbackList;

    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfeedback);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        feedbackList = (ListView)findViewById(R.id.myfeedback_list);

        volleyQuque = Volley.newRequestQueue(this);

        getFeedback();
    }

    protected void onResume() {
        super.onResume();
        getFeedback();
    }

    public void addFeedbackHandler(View view)
    {
        Intent intent = new Intent(this, FeedbackWindow.class);
        startActivity(intent);
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

    public void getFeedback() {
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(StringRequest.Method.GET, "http://139.129.22.145:5000/feedbacks",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            List<Feedback> feedbacks = JSON.parseArray(result.data, Feedback.class);
                            System.out.println(feedbacks);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyFeedbackWindow.this,
                                    android.R.layout.simple_expandable_list_item_1);
                            for (Feedback feedback : feedbacks) {
                                adapter.add(feedback.toShowString());
                            }
                            feedbackList.setAdapter(adapter);
                        }
                        Log.d("TAG", response);
                    }
                }, null);
        volleyQuque.add(stringRequest);
    }
}
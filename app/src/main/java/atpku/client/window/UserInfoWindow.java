package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import atpku.client.R;
import atpku.client.httputil.StringRequestWithCookie;
import atpku.client.model.Feedback;
import atpku.client.model.PostResult;
import atpku.client.model.User;

/**
 * Created by JIANG YUMENG on 2016/5/14.
 * Show user info.
 */
public class UserInfoWindow extends Activity
{
    public TextView studentNum;
    public TextView username;
    public TextView status;
    public TextView joinTime;
    public TextView commentReceived;
    public TextView likeReceived;
    public TextView dislikeReceived;
    public TextView reportReceived;
    public ListView feedbackList;

    public ActionBar actionBar;
    private RequestQueue volleyQuque;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        studentNum = (TextView)findViewById(R.id.userInfo_studentNum);
        username = (TextView)findViewById(R.id.userInfo_username);
        status = (TextView)findViewById(R.id.userInfo_status);
        joinTime = (TextView)findViewById(R.id.userInfo_joinTime);
        commentReceived = (TextView)findViewById(R.id.userInfo_commentReceived);
        likeReceived = (TextView)findViewById(R.id.userInfo_likeReceived);
        dislikeReceived = (TextView)findViewById(R.id.userInfo_dislikeReceived);
        reportReceived = (TextView)findViewById(R.id.userInfo_reportReceived);
        feedbackList = (ListView)findViewById(R.id.userInfo_feedbackList);

        volleyQuque = Volley.newRequestQueue(this);

        Intent intent = this.getIntent();
        User user = (User)intent.getSerializableExtra("user");
        System.out.println(user);
        if(user != null)
        {
            studentNum.setText(studentNum.getText() + user.email);
            username.setText(username.getText() + user.nickname);
            if(user.isBanned)
                status.setText(status.getText() + "禁言");
            else
                status.setText(status.getText() + "正常");
            String[] time = user.date_joined.split("T");
            joinTime.setText(joinTime.getText() + time[0] + " " + time[1]);
            commentReceived.setText(commentReceived.getText() + String.valueOf(user.commentReceived));
            likeReceived.setText(likeReceived.getText() + String.valueOf(user.likeReceived));
            dislikeReceived.setText(dislikeReceived.getText() + String.valueOf(user.dislikeReceived));
            reportReceived.setText(reportReceived.getText() + String.valueOf(user.reportReceived));
        }

    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_userinfo, menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.action_feedback: {
                Intent intent = new Intent(UserInfoWindow.this, FeedbackWindow.class);
                startActivity(intent);
            }
                break;
            case R.id.action_mymsg:{
                Intent intent = new Intent(UserInfoWindow.this, SearchResultWindow.class);
                startActivity(intent);
            }
                break;
            case R.id.action_myfeedback:
                getFeedback();
                break;
            default:
                break;
        }
        return true;

    }

    public void getFeedback()
    {
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(StringRequest.Method.GET,"http://139.129.22.145:5000/feedbacks",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if(result.success)
                        {
                            Toast.makeText(UserInfoWindow.this, "成功", Toast.LENGTH_LONG);
                            List<Feedback> feedbacks = JSON.parseArray(result.data, Feedback.class);
                            System.out.println(feedbacks);
                            ArrayAdapter<String> adapter =  new ArrayAdapter<String>(UserInfoWindow.this,
                                    android.R.layout.simple_expandable_list_item_1);
                            for(Feedback feedback:feedbacks)
                            {
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

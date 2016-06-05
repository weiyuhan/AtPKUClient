package atpku.client.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import atpku.client.R;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.Feedback;
import atpku.client.model.PostResult;
import atpku.client.model.User;

/**
 * Created by JIANG YUMENG on 2016/5/14.
 * Show user info.
 */
public class UserInfoWindow extends AppCompatActivity {
    public TextView studentNum;
    public TextView username;
    public TextView status;
    public TextView joinTime;
    public TextView commentReceived;
    public TextView likeReceived;
    public TextView dislikeReceived;
    public TextView reportReceived;
    public ListView feedbackList;

    public ImageView avatarView;

    private User user;

    public ActionBar actionBar;
    private RequestQueue volleyQuque;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        studentNum = (TextView) findViewById(R.id.userInfo_studentNum);
        username = (TextView) findViewById(R.id.userInfo_username);
        status = (TextView) findViewById(R.id.userInfo_status);
        joinTime = (TextView) findViewById(R.id.userInfo_joinTime);
        commentReceived = (TextView) findViewById(R.id.userInfo_commentReceived);
        likeReceived = (TextView) findViewById(R.id.userInfo_likeReceived);
        dislikeReceived = (TextView) findViewById(R.id.userInfo_dislikeReceived);
        reportReceived = (TextView) findViewById(R.id.userInfo_reportReceived);
        feedbackList = (ListView) findViewById(R.id.userInfo_feedbackList);
        avatarView = (ImageView) findViewById(R.id.userInfo_avatar);

        volleyQuque = Volley.newRequestQueue(this);
    }

    public void refreshUserInfo() {
        user = MapWindow.user;
        if (user.avatar == null)
            user.avatar = "http://public-image-source.img-cn-shanghai.aliyuncs.com/avatar33201652203559.jpg";
        System.out.println(user);
        if (user != null) {
            studentNum.setText("邮箱：" + user.email);
            username.setText("用户名：" + user.nickname);
            if (user.gender.equals("m"))
                username.setText(username.getText() + " ♂");
            else if (user.gender.equals("f"))
                username.setText(username.getText() + " ♀");
            if (user.isBanned)
                status.setText("状态：" + "禁言");
            else
                status.setText("状态：" + "正常");
            String[] time = user.date_joined.split("T");
            joinTime.setText("加入时间：" + time[0] + " " + time[1]);
            commentReceived.setText("收到评论数：" + String.valueOf(user.commentReceived));
            likeReceived.setText("收到过的赞：" + String.valueOf(user.likeReceived));
            dislikeReceived.setText("收到过的踩：" + String.valueOf(user.dislikeReceived));
            reportReceived.setText("被举报次数：" + String.valueOf(user.reportReceived));

            Picasso.with(this).load(user.avatar).placeholder(R.mipmap.image_loading).error(R.mipmap.default_avatar_1).resize(200, 200).into(avatarView);
        }
    }

    protected void onResume() {
        super.onResume();
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(StringRequest.Method.GET, "http://139.129.22.145:5000/profile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            MapWindow.user = JSON.parseObject(result.data, User.class);
                            refreshUserInfo();
                        } else {
                            Toast.makeText(UserInfoWindow.this, result.message, Toast.LENGTH_LONG).show();
                        }
                        Log.d("TAG", response);
                    }
                }, null);
        volleyQuque.add(stringRequest);
        getFeedback();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.menu_userinfo, menu);
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
            case R.id.action_feedback: {
                Intent intent = new Intent(UserInfoWindow.this, FeedbackWindow.class);
                startActivity(intent);
            }
            break;
            case R.id.action_mymsg: {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("nickname", user.getNickname());
                Intent intent = new Intent(UserInfoWindow.this, SearchResultWindow.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("params", params);
                bundle.putSerializable("caller", "UserInfoWindow");
                intent.putExtras(bundle);
                startActivity(intent);
            }
            break;
            case R.id.action_editmyinfo:
                Intent intent = new Intent(UserInfoWindow.this, EditMyInfoWindow.class);
                startActivity(intent);
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
                            Toast.makeText(UserInfoWindow.this, "成功", Toast.LENGTH_LONG);
                            List<Feedback> feedbacks = JSON.parseArray(result.data, Feedback.class);
                            System.out.println(feedbacks);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserInfoWindow.this,
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

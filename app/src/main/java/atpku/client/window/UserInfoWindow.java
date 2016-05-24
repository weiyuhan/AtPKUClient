package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import atpku.client.R;
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
            joinTime.setText(joinTime.getText() + user.date_joined);
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

                break;
            default:
                break;
        }
        return true;

    }
}

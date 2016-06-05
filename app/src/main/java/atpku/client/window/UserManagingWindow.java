package atpku.client.window;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atpku.client.R;
import atpku.client.model.Comment;
import atpku.client.model.Message;
import atpku.client.model.PostResult;
import atpku.client.model.User;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.util.ThemeUtil;

/**
 * Created by wyh on 2016/5/19.
 */
public class UserManagingWindow extends AppCompatActivity
{
    public TextView nicknameText;
    public TextView emailText;
    public TextView isBannedText;
    public TextView reportedText;
    public Button banButton;
    public ListView msgList;
    private RequestQueue volleyQuque;
    private User managedUser = new User();

    private int userID;

    public ActionBar actionBar;
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermanaging);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        nicknameText = (TextView)findViewById(R.id.nickname);
        emailText = (TextView)findViewById(R.id.email);
        isBannedText = (TextView)findViewById(R.id.isBanned);
        reportedText = (TextView)findViewById(R.id.reportNum);
        banButton = (Button)findViewById(R.id.banButton);
        msgList = (ListView)findViewById(R.id.msgList);

        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Intent intent = this.getIntent();
        userID = (int) intent.getSerializableExtra("userID");
        volleyQuque = Volley.newRequestQueue(this);
        refreshUserInfo();
        banButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText daysText = new EditText(UserManagingWindow.this);
                daysText.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(UserManagingWindow.this).setTitle("请输入禁言天数").setView(
                        daysText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                                "http://139.129.22.145:5000/banUser/" + userID
                                        + "/" +daysText.getText().toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        PostResult result = JSON.parseObject(response, PostResult.class);
                                        if (result.success) {
                                            //likeNum.setText(msg.getLikeUsers().size()+1+"");
                                            refreshUserInfo();
                                            Toast.makeText(UserManagingWindow.this, "禁言成功", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            Toast.makeText(UserManagingWindow.this, result.message, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }, null);
                        volleyQuque.add(stringRequest);
                    }
                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).show();
            }
        });
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

    public void refreshUserInfo() {
        StringRequestWithCookie stringRequest = null;
        stringRequest = new StringRequestWithCookie(Request.Method.GET,
                    "http://139.129.22.145:5000/admin/profile?id="+ userID,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            PostResult result = JSON.parseObject(response, PostResult.class);
                            if(result.success) {
                                List<User> users = JSON.parseArray(result.data, User.class);
                                managedUser = users.get(0);
                                nicknameText.setText(managedUser.getNickname());
                                emailText.setText(managedUser.getEmail());
                                if (managedUser.isBanned()) {
                                    isBannedText.setText("被禁言：是");
                                    banButton.setVisibility(View.GONE);
                                }
                                else {
                                    isBannedText.setText("被禁言：否");
                                }
                                reportedText.setText("被举报次数："+managedUser.getReportReceived());
                            }
                            else
                                Toast.makeText(UserManagingWindow.this, result.message, Toast.LENGTH_LONG).show();
                        }
                    }, null);
        volleyQuque.add(stringRequest);

        final MessageAdapter adapter = new MessageAdapter(this, R.layout.messagemore_row);
        Map<String, String> params = new HashMap<String, String>();
        params.put("ownerid", "" + userID);
        StringRequestWithCookie stringRequest2 = new StringRequestWithCookie(Request.Method.POST,
                "http://139.129.22.145:5000/message/search",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if(result.success) {
                            List<Message> messages = JSON.parseArray(result.data, Message.class);
                            for(Message message:messages) {
                                adapter.add(message);
                            }
                        }
                        else
                            Toast.makeText(UserManagingWindow.this, result.message, Toast.LENGTH_LONG).show();
                        msgList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }, params);
        volleyQuque.add(stringRequest2);
    }

    class MessageAdapter extends ArrayAdapter<Message> {
        private int mResourceId;

        public MessageAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.mResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message msg = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(mResourceId, null);
            view.setClickable(true);
            final int msgid = msg.getId();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserManagingWindow.this, MsgWindow.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("messageID", msgid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            TextView titleText = (TextView) view.findViewById(R.id.title);
            TextView timeText = (TextView) view.findViewById(R.id.time);
            TextView placeText = (TextView) view.findViewById(R.id.place);
            TextView msgReportedText = (TextView) view.findViewById(R.id.msgReportNum);

            titleText.setText(msg.getTitle());
            timeText.setText(msg.getPostTime());
            placeText.setText(msg.getAtPlace().getName());
            msgReportedText.setText("被举报次数："+msg.getReportTimes());
            return view;
        }
    }
}

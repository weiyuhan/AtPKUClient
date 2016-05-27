package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atpku.client.R;
import atpku.client.model.Comment;
import atpku.client.model.Message;
import atpku.client.model.PostResult;
import atpku.client.util.StringRequestWithCookie;

/**
 * Created by wyh on 2016/5/19.
 */
public class MsgWindow extends Activity
{
    public TextView title;
    public TextView author;
    public TextView content;
    public TextView likeNum;
    public TextView dislikeNum;
    public TextView reportNum;
    public Button likeButton;
    public Button dislikeButton;
    public Button reportButton;
    public EditText commentText;
    public Button commentButton;
    public ListView commentList;
    private RequestQueue volleyQuque;
    private Message msg;

    private int messageID;

    public ActionBar actionBar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showmsg);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        title = (TextView)findViewById(R.id.msg_title);
        author = (TextView)findViewById(R.id.msg_author);
        content = (TextView)findViewById(R.id.msg_content);
        likeNum = (TextView)findViewById(R.id.msg_likeNum);
        likeButton = (Button)findViewById(R.id.msg_likeButton);
        dislikeNum = (TextView)findViewById(R.id.msg_dislikeNum);
        dislikeButton = (Button)findViewById(R.id.msg_dislikeButton);
        reportNum = (TextView)findViewById(R.id.msg_reportNum);
        reportButton = (Button)findViewById(R.id.msg_reportButton);
        commentText = (EditText)findViewById(R.id.msg_addComment);
        commentButton = (Button)findViewById(R.id.msg_commentButton);
        commentList = (ListView)findViewById(R.id.msg_commentList);

        CharSequence label = (CharSequence) "";
        setTitle(label);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Intent intent = this.getIntent();
        messageID = (int) intent.getSerializableExtra("messageID");
        volleyQuque = Volley.newRequestQueue(this);
        refreshMessageInfo();
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                        "http://139.129.22.145:5000/message/" + messageID + "/like",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                PostResult result = JSON.parseObject(response, PostResult.class);
                                if (result.success) {
                                    //likeNum.setText(msg.getLikeUsers().size()+1+"");
                                    refreshMessageInfo();
                                }
                                else {
                                    Toast.makeText(MsgWindow.this, result.message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }, null);
                volleyQuque.add(stringRequest);
            }
        });
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                        "http://139.129.22.145:5000/message/"+messageID+"/dislike",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                PostResult result = JSON.parseObject(response, PostResult.class);
                                if(result.success) {
                                    //dislikeNum.setText(msg.getDislikeUsers().size()+1+"");
                                    refreshMessageInfo();
                                }
                                else {
                                    Toast.makeText(MsgWindow.this, result.message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }, null);
                volleyQuque.add(stringRequest);
            }
        });
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                        "http://139.129.22.145:5000/message/"+messageID+"/report",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                PostResult result = JSON.parseObject(response, PostResult.class);
                                if(result.success) {
                                    //dislikeNum.setText(msg.getDislikeUsers().size()+1+"");
                                    refreshMessageInfo();
                                }
                                else {
                                    Toast.makeText(MsgWindow.this, result.message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }, null);
                volleyQuque.add(stringRequest);
            }
        });
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentText.getText().toString().equals(""))
                {
                    Toast.makeText(MsgWindow.this, "评论不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put("content", commentText.getText().toString());
                StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                        "http://139.129.22.145:5000/message/"+messageID+"/comment",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                PostResult result = JSON.parseObject(response, PostResult.class);
                                if(result.success) {
                                    Toast.makeText(MsgWindow.this, "评论成功！", Toast.LENGTH_LONG).show();
                                    refreshMessageInfo();
                                }
                            }
                        }, params);
                volleyQuque.add(stringRequest);
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

    public void refreshMessageInfo() {
        final CommentAdapter adapter = new CommentAdapter(this, R.layout.comment_row);
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(StringRequest.Method.GET,
                "http://139.129.22.145:5000/message/"+messageID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if(result.success) {
                            msg = JSON.parseObject(result.data, Message.class);
                            title.setText(msg.getTitle());
                            author.setText(msg.getOwner().getNickname());
                            content.setText(msg.getContent());
                            likeNum.setText(msg.getLikeUsers().size()+"");
                            dislikeNum.setText(msg.getDislikeUsers().size()+"");
                            reportNum.setText(msg.getReportUsers().size()+"");
                            commentText.setText("");
                            commentText.clearFocus();
                            for(Comment comment:msg.comments) {
                                adapter.add(comment);
                            }
                        }
                        commentList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }}, null);
        volleyQuque.add(stringRequest);
    }

    class CommentAdapter extends ArrayAdapter<Comment> {
        private int mResourceId;

        public CommentAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.mResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Comment comment = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(mResourceId, null);

            TextView contentText = (TextView) view.findViewById(R.id.content);
            TextView timeText = (TextView) view.findViewById(R.id.time);
            TextView nicknameText = (TextView) view.findViewById(R.id.nickname);

            contentText.setText(comment.getContent());
            timeText.setText(comment.getCommentTime());
            nicknameText.setText(comment.owner.getNickname());
            return view;
        }
    }
}

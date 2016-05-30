package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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
import atpku.client.model.Image;
import atpku.client.model.Message;
import atpku.client.model.PostResult;
import atpku.client.util.CommentAdapter;
import atpku.client.util.ImageAdapter;
import atpku.client.util.ImageDialog;
import atpku.client.util.StringRequestWithCookie;

/**
 * Created by wyh on 2016/5/19.
 */
public class MsgWindow extends Activity implements AdapterView.OnItemClickListener
{
    public TextView title;
    public TextView time;
    public TextView author;
    public TextView content;
    public TextView likeNum;
    public TextView dislikeNum;
    public TextView reportNum;
    public Button likeButton;
    public Button dislikeButton;
    public Button reportButton;
    public Button deleteButton;
    public EditText commentText;
    public Button commentButton;
    public ListView commentList;
    private RequestQueue volleyQuque;
    private Message msg;

    public GridView imageList;

    private int messageID;
    public static String CUT_FILL_BLACK = "@200w_200h_4e_0-0-0bgc";
    public static String CUT_TO_CYCLESQUARE = "@200w_200h_1e_1c_10-2ci.png";
    public static String CUT_TO_CYCLE = "@200-1ci";

    public ActionBar actionBar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showmsg);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        title = (TextView)findViewById(R.id.msg_title);
        time = (TextView)findViewById(R.id.msg_time);
        author = (TextView)findViewById(R.id.msg_author);
        content = (TextView)findViewById(R.id.msg_content);
        likeNum = (TextView)findViewById(R.id.msg_likeNum);
        likeButton = (Button)findViewById(R.id.msg_likeButton);
        dislikeNum = (TextView)findViewById(R.id.msg_dislikeNum);
        dislikeButton = (Button)findViewById(R.id.msg_dislikeButton);
        reportNum = (TextView)findViewById(R.id.msg_reportNum);
        reportButton = (Button)findViewById(R.id.msg_reportButton);
        deleteButton = (Button)findViewById(R.id.msg_deleteButton);
        commentText = (EditText)findViewById(R.id.msg_addComment);
        commentButton = (Button)findViewById(R.id.msg_commentButton);
        commentList = (ListView)findViewById(R.id.msg_commentList);
        imageList = (GridView)findViewById(R.id.msg_imgList);

        CharSequence label = (CharSequence) "";
        setTitle(label);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Intent intent = this.getIntent();
        messageID = (int) intent.getSerializableExtra("messageID");
        volleyQuque = Volley.newRequestQueue(this);
        refreshMessageInfo(true);
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
                                    refreshMessageInfo(false);
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
                                    refreshMessageInfo(false);
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
                                    refreshMessageInfo(false);
                                }
                                else {
                                    Toast.makeText(MsgWindow.this, result.message, Toast.LENGTH_LONG).show();
                                }
                            }
                        }, null);
                volleyQuque.add(stringRequest);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MapWindow.user.getIsAdmin() && MapWindow.user.getId()!=msg.getId()) {
                    new AlertDialog.Builder(MsgWindow.this).setTitle("是否同时禁言该用户？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final EditText daysText = new EditText(MsgWindow.this);
                                    daysText.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    new AlertDialog.Builder(MsgWindow.this).setTitle("请输入禁言天数").setView(
                                            daysText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                                                    "http://139.129.22.145:5000/banUser/" + msg.getOwner().getId()
                                                            + "/" + daysText.getText().toString(),
                                                    new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            PostResult result = JSON.parseObject(response, PostResult.class);
                                                            if (result.success) {
                                                                return;
                                                            } else {
                                                                Toast.makeText(MsgWindow.this, result.message, Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    }, null);
                                            volleyQuque.add(stringRequest);
                                            deleteMsg();
                                        }
                                    })
                                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteMsg();
                                                }
                                            }).show();
                                }
                            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteMsg();
                        }
                    }).show();
                }
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
                                    refreshMessageInfo(false);
                                }
                            }
                        }, params);
                volleyQuque.add(stringRequest);
            }
        });
    }

    private void deleteMsg()
    {
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                "http://139.129.22.145:5000/message/"+messageID+"/delete",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if(result.success) {
                            //dislikeNum.setText(msg.getDislikeUsers().size()+1+"");
                            Toast.makeText(MsgWindow.this, "删除成功！", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText(MsgWindow.this, result.message, Toast.LENGTH_LONG).show();
                        }
                    }
                }, null);
        volleyQuque.add(stringRequest);
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

    public void refreshMessageList()
    {
        if(msg != null && msg.images != null)
        {
            ImageAdapter adapter = new ImageAdapter(this, R.layout.image_row);
            for(Image image:msg.images)
            {
                System.out.println(image.getUrl());
                adapter.add(image.getUrl() + MsgWindow.CUT_FILL_BLACK);
            }
            imageList.setAdapter(adapter);
            imageList.setOnItemClickListener(this);
        }
    }

    public void refreshMessageInfo(final boolean refreshImage) {
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
                            time.setText(msg.getPostTime());
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
                            if (MapWindow.user.getIsAdmin() || MapWindow.user.getId()==msg.getOwner().getId())
                            {
                                reportButton.setVisibility(View.GONE);
                                reportNum.setText(msg.getReportUsers().size()+"举报");
                                deleteButton.setVisibility(View.VISIBLE);
                            }
                            if(refreshImage)
                                refreshMessageList();
                        }
                        commentList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }}, null);
        volleyQuque.add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        String smallImgUrl = (String)parent.getItemAtPosition(position);
        String imgUrl = smallImgUrl.substring(0, smallImgUrl.lastIndexOf("@"));
        ImageDialog imageDialog = new ImageDialog(this, imgUrl);
        imageDialog.show();
    }

}

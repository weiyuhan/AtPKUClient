package atpku.client.window;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
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
import atpku.client.util.ThemeUtil;

/**
 * Created by wyh on 2016/5/19.
 */
public class MsgWindow extends AppCompatActivity implements AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout refreshLayout;
    public View msgContent;
    public View comment;

    private RequestQueue volleyQuque;
    private Message msg;

    ViewPager pager = null;
    PagerTabStrip tabStrip = null;
    ArrayList<View> viewContainter = new ArrayList<View>();
    ArrayList<String> titleContainer = new ArrayList<String>();

    public TextView title;
    public TextView time;
    public TextView author;
    public TextView content;
    public TextView likeNum;
    public TextView dislikeNum;
    public TextView reportNum;
    public ImageButton likeButton;
    public ImageButton dislikeButton;
    public Button reportButton;
    public Button deleteButton;
    public EditText commentText;
    public Button commentButton;
    public ImageView avatarView;
    public GridView imageList;
    public ListView commentList;


    private int messageID;
    public static String CUT_FILL_BLACK = "@200w_200h_4e_0-0-0bgc";
    public static String CUT_TO_CYCLESQUARE = "@200w_200h_1e_1c_10-2ci.png";
    public static String CUT_TO_CYCLE = "@200-1ci";

    public ActionBar actionBar;

    public TextView likeView;
    public TextView dislikeView;
    public TextView reportView;
    public Animation animation;

    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        pager = (ViewPager) this.findViewById(R.id.viewpager);
        tabStrip = (PagerTabStrip) this.findViewById(R.id.tabstrip);

        msgContent = LayoutInflater.from(this).inflate(R.layout.msg_content, null);
        comment = LayoutInflater.from(this).inflate(R.layout.msg_comment, null);
        //viewpager开始添加view
        viewContainter.add(msgContent);
        viewContainter.add(comment);
        //页签项
        titleContainer.add("信息内容");
        titleContainer.add("评论");

        pager.setAdapter(new PagerAdapter() {

            //viewpager中的组件数量
            @Override
            public int getCount() {
                return viewContainter.size();
            }

            //滑动切换的时候销毁当前的组件
            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                ((ViewPager) container).removeView(viewContainter.get(position));
            }

            //每次滑动的时候生成的组件
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(viewContainter.get(position));
                return viewContainter.get(position);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return titleContainer.get(position);
            }
        });

        refreshLayout = (SwipeRefreshLayout)comment.findViewById(R.id.msg_comment_refresh_layout);
        refreshLayout.setColorScheme(R.color.lawngreen, R.color.yellow, R.color.blue, R.color.crimson);
        refreshLayout.setOnRefreshListener(this);

        title = (TextView)msgContent.findViewById(R.id.msg_title);
        time = (TextView)msgContent.findViewById(R.id.msg_time);
        author = (TextView)msgContent.findViewById(R.id.msg_author);
        content = (TextView)msgContent.findViewById(R.id.msg_content);
        likeNum = (TextView)msgContent.findViewById(R.id.msg_likeNum);
        likeButton = (ImageButton)msgContent.findViewById(R.id.msg_likeButton);
        dislikeNum = (TextView)msgContent.findViewById(R.id.msg_dislikeNum);
        dislikeButton = (ImageButton)msgContent.findViewById(R.id.msg_dislikeButton);
        reportNum = (TextView)msgContent.findViewById(R.id.msg_reportNum);
        reportButton = (Button)msgContent.findViewById(R.id.msg_reportButton);
        deleteButton = (Button)msgContent.findViewById(R.id.msg_deleteButton);
        commentText = (EditText)msgContent.findViewById(R.id.msg_addComment);
        commentButton = (Button)msgContent.findViewById(R.id.msg_commentButton);
        imageList = (GridView)msgContent.findViewById(R.id.msg_imgList);
        avatarView = (ImageView)msgContent.findViewById(R.id.showmsg_avatar);
        commentList = (ListView)comment.findViewById(R.id.msg_commentList);
        likeView = (TextView)msgContent.findViewById(R.id.animation_like);;
        dislikeView = (TextView)msgContent.findViewById(R.id.animation_dislike);;
        reportView = (TextView)msgContent.findViewById(R.id.animation_report);;
        animation  = AnimationUtils.loadAnimation(MsgWindow.this, R.anim.applaud_animation);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Intent intent = this.getIntent();
        messageID = (int) intent.getSerializableExtra("messageID");
        volleyQuque = Volley.newRequestQueue(this);
        refreshMessageInfo(true);
    }

    public void onLikeButtonClick(View v) {
        if (!MapWindow.isLogin) {
            Snackbar.make(findViewById(R.id.msg_layout), "请登录", Snackbar.LENGTH_LONG).show();
            return;
        }
        likeButton.setEnabled(false);
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                "http://139.129.22.145:5000/message/" + messageID + "/like",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        likeButton.setEnabled(true);
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            //likeNum.setText(msg.getLikeUsers().size()+1+"");
                            likeView.setVisibility(View.VISIBLE);
                            likeView.startAnimation(animation);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    likeView.setVisibility(View.INVISIBLE);
                                }
                            }, 1000);
                            refreshMessageInfo(false);
                        } else {
                            Snackbar.make(findViewById(R.id.msg_layout), result.message, Snackbar.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        likeButton.setEnabled(true);
                        Snackbar.make(findViewById(R.id.msg_layout), "赞失败，请检查网络连接", Snackbar.LENGTH_LONG).show();
                    }
                }, null);
        volleyQuque.add(stringRequest);
    }

    public void onDislikeButtonClick(View v) {
        if (!MapWindow.isLogin) {
            Snackbar.make(findViewById(R.id.msg_layout), "请登录", Snackbar.LENGTH_LONG).show();
            return;
        }
        dislikeButton.setEnabled(false);
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                "http://139.129.22.145:5000/message/" + messageID + "/dislike",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dislikeButton.setEnabled(true);
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            //dislikeNum.setText(msg.getDislikeUsers().size()+1+"");
                            dislikeView.setVisibility(View.VISIBLE);
                            dislikeView.startAnimation(animation);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    dislikeView.setVisibility(View.INVISIBLE);
                                }
                            }, 1000);
                            refreshMessageInfo(false);
                        } else {
                            Snackbar.make(findViewById(R.id.msg_layout), result.message, Snackbar.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        dislikeButton.setEnabled(true);
                        Snackbar.make(findViewById(R.id.msg_layout), "踩失败，请检查网络连接", Snackbar.LENGTH_LONG).show();
                    }
                }, null);
        volleyQuque.add(stringRequest);
    }
    public void onReportButtonClick(View v) {
        if (!MapWindow.isLogin) {
            Snackbar.make(findViewById(R.id.msg_layout), "请登录", Snackbar.LENGTH_LONG).show();
            return;
        }
        reportButton.setEnabled(false);
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                "http://139.129.22.145:5000/message/" + messageID + "/report",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        reportButton.setEnabled(true);
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            //dislikeNum.setText(msg.getDislikeUsers().size()+1+"");
                            reportView.setVisibility(View.VISIBLE);
                            reportView.startAnimation(animation);
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    reportView.setVisibility(View.INVISIBLE);
                                }
                            }, 1000);
                            refreshMessageInfo(false);
                        } else {
                            Snackbar.make(findViewById(R.id.msg_layout), result.message, Snackbar.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        reportButton.setEnabled(true);
                        Snackbar.make(findViewById(R.id.msg_layout), "举报失败，请检查网络连接", Snackbar.LENGTH_LONG).show();
                    }
                }, null);
        volleyQuque.add(stringRequest);
    }

    public void onDeleteButtonClick(View v) {
        if (MapWindow.user.getIsAdmin() && MapWindow.user.getId() != msg.getId()) {
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
                                                        Snackbar.make(findViewById(R.id.msg_layout), result.message, Snackbar.LENGTH_LONG).show();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError volleyError) {
                                                    Snackbar.make(findViewById(R.id.msg_layout), "删除失败，请检查网络连接", Snackbar.LENGTH_LONG).show();
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

    public void onCommentButtonClick(View v) {
        if (!MapWindow.isLogin) {
            Snackbar.make(findViewById(R.id.msg_layout), "请登录", Snackbar.LENGTH_LONG).show();
            return;
        }
        if (commentText.getText().toString().equals("")) {
            Snackbar.make(findViewById(R.id.msg_layout), "评论不能为空", Snackbar.LENGTH_LONG).show();
            return;
        }
        commentButton.setEnabled(false);
        commentButton.setText("发送中");
        Map<String, String> params = new HashMap<String, String>();
        params.put("content", commentText.getText().toString());
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                "http://139.129.22.145:5000/message/" + messageID + "/comment",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        commentButton.setEnabled(true);
                        commentButton.setText("评论");
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            commentText.setText("");
                            commentText.clearFocus();
                            Snackbar.make(findViewById(R.id.msg_layout), "评论成功", Snackbar.LENGTH_LONG).show();
                            pager.setCurrentItem(1, true);
                            refreshMessageInfo(false);
                        }
                        else
                            Snackbar.make(findViewById(R.id.msg_layout), result.message, Snackbar.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        commentButton.setEnabled(true);
                        commentButton.setText("评论");
                        Snackbar.make(findViewById(R.id.msg_layout), "评论失败，请检查网络连接", Snackbar.LENGTH_LONG).show();
                    }
                }, params);
        volleyQuque.add(stringRequest);
    }

    private void deleteMsg() {
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                "http://139.129.22.145:5000/message/" + messageID + "/delete",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        Snackbar.make(findViewById(R.id.msg_layout), result.message, Snackbar.LENGTH_LONG).show();
                        if (result.success) {
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Snackbar.make(findViewById(R.id.msg_layout), "删除信息失败，请检查网络连接", Snackbar.LENGTH_LONG).show();
                    }
                }, null);
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

    public void refreshMessageList() {
        if (msg != null && msg.owner.avatar != null) {
            Picasso.with(this).load(msg.owner.avatarIntoCycle()).resize(144, 144).into(avatarView);
        }
        if (msg != null && msg.images != null) {
            ImageAdapter adapter = new ImageAdapter(this, R.layout.image_row);
            for (Image image : msg.images) {
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
                "http://139.129.22.145:5000/message/" + messageID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            msg = JSON.parseObject(result.data, Message.class);
                            setTitle(msg.atPlace.getName());
                            title.setText(msg.getTitle());
                            time.setText(msg.getPostTime() + "\n至 " + msg.getEndTime());
                            author.setText(msg.getOwner().getNickname());
                            content.setText(msg.getContent());
                            likeNum.setText(msg.getLikeUsers().size() + "");
                            dislikeNum.setText("-" + msg.getDislikeUsers().size());
                            reportNum.setText(msg.getReportUsers().size() + "");
                            for (Comment comment : msg.comments) {
                                adapter.add(comment);
                            }
                            refreshLayout.setRefreshing(false);
                            if (MapWindow.user.getIsAdmin()) {
                                reportButton.setVisibility(View.GONE);
                                reportNum.setText(msg.getReportUsers().size() + "举报");
                                deleteButton.setVisibility(View.VISIBLE);
                            }
                            if (refreshImage)
                                refreshMessageList();
                        }
                        commentList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        refreshLayout.setRefreshing(false);
                        Snackbar.make(findViewById(R.id.msg_layout), "请检查网络连接", Snackbar.LENGTH_LONG).show();
                    }
                }, null);
        volleyQuque.add(stringRequest);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String smallImgUrl = (String) parent.getItemAtPosition(position);
        String imgUrl = smallImgUrl.substring(0, smallImgUrl.lastIndexOf("@"));
        ImageDialog imageDialog = new ImageDialog(this, imgUrl);
        imageDialog.show();
    }

    public void onRefresh() {
        refreshMessageInfo(false);
    }
}

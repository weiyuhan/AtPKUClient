package atpku.client.window;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import atpku.client.R;
import atpku.client.model.User;
import atpku.client.util.MessageAdapter;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.Message;
import atpku.client.model.PostResult;
import atpku.client.util.ThemeUtil;

/**
 * Created by wyh on 2016/5/19.
 */
public class PlaceWindow extends AppCompatActivity implements SearchView.OnQueryTextListener,
        SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout refreshLayout;
    public ListView msgList;
    private int placeID;
    public ActionBar actionBar;
    private RequestQueue volleyQuque;
    public SearchView searchView;

    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.logo);
        actionBar.setDisplayHomeAsUpEnabled(true);

        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.place_refresh_layout);
        refreshLayout.setColorScheme(R.color.lawngreen, R.color.yellow, R.color.blue, R.color.crimson);
        refreshLayout.setOnRefreshListener(this);
        msgList = (ListView) this.findViewById(R.id.place_msgList);

        Intent intent = this.getIntent();
        placeID = (int) intent.getSerializableExtra("id");
        CharSequence label = (CharSequence) intent.getSerializableExtra("name");
        setTitle(label);
        volleyQuque = Volley.newRequestQueue(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.menu_place, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void refreshMessageList() {
        final MessageAdapter adapter = new MessageAdapter(this, R.layout.message_row);
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.GET,
                "http://139.129.22.145:5000/msgsAtPlace/" + placeID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            final List<Message> messages = JSON.parseArray(result.data, Message.class);
                            Collections.sort(messages, new Comparator<Message>() {
                                @Override
                                public int compare(Message lhs, Message rhs) {
                                    return rhs.getPostTime().compareTo(lhs.getPostTime());
                                }
                            });
                            StringRequestWithCookie stringRequest2 = new StringRequestWithCookie(Request.Method.GET,
                                    "http://139.129.22.145:5000/hotmessages/" + placeID,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            PostResult result = JSON.parseObject(response, PostResult.class);
                                            if (result.success) {
                                                List<Message> hotMessages = JSON.parseArray(result.data, Message.class);
                                                Collections.sort(hotMessages, new Comparator<Message>() {
                                                    @Override
                                                    public int compare(Message lhs, Message rhs) {
                                                        return rhs.getHeat() - lhs.getHeat();
                                                    }
                                                });
                                                for (Message message : hotMessages) {
                                                    System.out.println("Get hot message: " + message.getTitle().toString());
                                                    message.title = "//!?hot!?//"+message.title;
                                                    adapter.add(message);
                                                }
                                                for (Message message : messages) {
                                                    System.out.println("Get message: " + message.getTitle().toString());
                                                    adapter.add(message);
                                                }
                                                msgList.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                                refreshLayout.setRefreshing(false);
                                            };
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError volleyError) {
                                            refreshLayout.setRefreshing(false);
                                            Snackbar.make(findViewById(R.id.place_layout), "请检查网络连接", Snackbar.LENGTH_INDEFINITE).show();
                                        }
                                    }, null);
                            volleyQuque.add(stringRequest2);
                            if(messages.size() == 0)
                            {
                                Snackbar.make(findViewById(R.id.place_layout), "这里什么也没有，发表新的信息或去其他地方看看吧", Snackbar.LENGTH_INDEFINITE).show();
                            }
                        } else
                            Snackbar.make(findViewById(R.id.place_layout), result.message, Snackbar.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        refreshLayout.setRefreshing(false);
                        Snackbar.make(findViewById(R.id.place_layout), "请检查网络连接", Snackbar.LENGTH_INDEFINITE).show();
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
            case R.id.action_sendmsg: {
                sendMsgHandler(null);
            }
            break;
            case R.id.action_place_search: {
                searchView = (SearchView) MenuItemCompat.getActionView(mi);
                if (searchView != null)
                    searchView.setOnQueryTextListener(this);
            }
            break;
            default:
                break;
        }
        return true;

    }

    public void sendMsgHandler(View view)
    {
        if (!MapWindow.isLogin) {
            Snackbar.make(findViewById(R.id.place_layout), "请登录", Snackbar.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, SendMsgWindow.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("placeId", placeID);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    public boolean onQueryTextChange(String newText) {
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        HashMap<String, String> params = new HashMap<String, String>();
        // 按标题搜索
        params.put("title", query);
        params.put("placeid", String.valueOf(placeID));
        Intent intent = new Intent(PlaceWindow.this, SearchResultWindow.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("params", params);
        bundle.putSerializable("caller", "MapWindow");
        intent.putExtras(bundle);
        startActivity(intent);
        return false;
    }

    protected void onResume() {
        super.onResume();
        refreshMessageList();
    }

    public void onRefresh() {
        refreshMessageList();
    }
}

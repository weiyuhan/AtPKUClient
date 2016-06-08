package atpku.client.window;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import atpku.client.R;
import atpku.client.model.User;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.PostResult;
import atpku.client.util.ThemeUtil;

/**
 * Created by wyh on 2016/5/19.
 */
public class UserListWindow extends AppCompatActivity implements SearchView.OnQueryTextListener {
    public ListView userList;
    public ActionBar actionBar;
    private RequestQueue volleyQuque;
    public SearchView searchView;
    private String nickname = "";
    private List<User> users = null;

    private boolean hideBanned = false;

    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.logo);
        actionBar.setDisplayHomeAsUpEnabled(true);

        userList = (ListView) findViewById(R.id.userList);

        volleyQuque = Volley.newRequestQueue(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.menu_userlist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void refreshUserList() {
        final UserAdapter adapter = new UserAdapter(this, R.layout.user_row);
        if (users != null) {
            for (User user : users) {
                if (user.getNickname().contains(nickname) && !(hideBanned && user.isBanned()))
                    adapter.add(user);
            }
            userList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return;
        }

        StringRequestWithCookie stringRequest = null;
        try {
            stringRequest = new StringRequestWithCookie(Request.Method.GET,
                    "http://139.129.22.145:5000/admin/profile?nickname=" + URLEncoder.encode(nickname, "UTF-8"),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            PostResult result = JSON.parseObject(response, PostResult.class);
                            if (result.success) {
                                users = JSON.parseArray(result.data, User.class);
                                Collections.sort(users, new Comparator<User>() {
                                    @Override
                                    public int compare(User lhs, User rhs) {
                                        return rhs.getReportReceived() - lhs.getReportReceived();
                                    }
                                });
                                for (User user : users) {
                                    adapter.add(user);
                                }
                            } else
                                Snackbar.make(findViewById(R.id.userList_layout), result.message, Snackbar.LENGTH_LONG).show();
                            userList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Snackbar.make(findViewById(R.id.userList_layout), "请检查网络连接", Snackbar.LENGTH_INDEFINITE).show();
                        }
                    }, null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
            case R.id.action_user_search: {
                //mi.expandActionView();
                searchView = (SearchView) MenuItemCompat.getActionView(mi);
                if (searchView != null)
                    searchView.setOnQueryTextListener(this);
            }
            break;
            case R.id.action_mode_change: {
                if (hideBanned) {
                    hideBanned = false;
                    mi.setTitle("只看未禁言");
                } else {
                    hideBanned = true;
                    mi.setTitle("查看全部用户");
                }
                refreshUserList();
            }
            break;
            default:
                break;
        }
        return true;

    }

    class UserAdapter extends ArrayAdapter<User> {
        private int mResourceId;

        public UserAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.mResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User user = getItem(position);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(mResourceId, null);
            view.setClickable(true);
            final int userid = user.getId();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserListWindow.this, UserManagingWindow.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userID", userid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            TextView nicknameText = (TextView) view.findViewById(R.id.nickname);
            TextView isBannedText = (TextView) view.findViewById(R.id.isBanned);
            TextView reportedText = (TextView) view.findViewById(R.id.reportNum);

            nicknameText.setText((user.getNickname()));
            if (user.isBanned()) {
                isBannedText.setText("被禁言：是");
            } else {
                isBannedText.setText("被禁言：否");
            }
            reportedText.setText("被举报次数：" + user.getReportReceived());
            return view;
        }
    }

    public boolean onQueryTextChange(String newText) {
        // 按昵称搜索
        nickname = newText;
        refreshUserList();
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        // 按昵称搜索
        nickname = query;
        refreshUserList();
        return false;
    }

    protected void onResume() {
        super.onResume();
        users = null;
        refreshUserList();
    }
}

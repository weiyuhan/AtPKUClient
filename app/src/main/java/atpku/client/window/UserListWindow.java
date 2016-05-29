package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atpku.client.R;
import atpku.client.model.Place;
import atpku.client.model.User;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.Message;
import atpku.client.model.PostResult;

/**
 * Created by wyh on 2016/5/19.
 */
public class UserListWindow extends Activity implements SearchView.OnQueryTextListener
{
    public ListView userList;
    public ActionBar actionBar;
    private RequestQueue volleyQuque;
    public SearchView search;
    private String nickname = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlist);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        userList = (ListView) findViewById(R.id.userList);

        volleyQuque = Volley.newRequestQueue(this);
        refreshUserList();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_userlist, menu);

        MenuItem searchItem = menu.findItem(R.id.action_user_search);
        search = (SearchView)searchItem.getActionView();
        search.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    public void refreshUserList() {
        final UserAdapter adapter = new UserAdapter(this, R.layout.user_row);
        StringRequestWithCookie stringRequest = null;
        try {
            stringRequest = new StringRequestWithCookie(Request.Method.GET,
                    "http://139.129.22.145:5000/admin/profile?nickname="+ URLEncoder.encode(nickname,"UTF-8"),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            PostResult result = JSON.parseObject(response, PostResult.class);
                            if(result.success) {
                                List<User> users = JSON.parseArray(result.data, User.class);
                                for(User user: users) {
                                    adapter.add(user);
                                }
                            }
                            else
                                Toast.makeText(UserListWindow.this, result.message, Toast.LENGTH_LONG).show();
                            userList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
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
            case R.id.action_user_search:{
                if (search != null)
                    search.setOnQueryTextListener(this);
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
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserListWindow.this, UserManagingWindow.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("userID", userid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });*/

            TextView nicknameText = (TextView) view.findViewById(R.id.nickname);
            TextView isBannedText = (TextView) view.findViewById(R.id.isBanned);
            TextView reportedText = (TextView) view.findViewById(R.id.reportNum);

            nicknameText.setText((user.getNickname()));
            if (user.isBanned())
            {
                isBannedText.setText("被禁言：是");
            }
            reportedText.setText("被举报次数：" + user.getReportReceived());
            return view;
        }
    }

    public boolean onQueryTextChange(String newText)
    {
        // 按昵称搜索
        nickname = newText;
        refreshUserList();
        return true;
    }

    public boolean onQueryTextSubmit(String query)
    {
        // 按昵称搜索
        nickname = query;
        refreshUserList();
        return false;
    }

    protected void onResume() {
        super.onResume();
        refreshUserList();
    }
}

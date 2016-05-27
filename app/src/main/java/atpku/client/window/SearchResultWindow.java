package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atpku.client.R;
import atpku.client.model.Message;
import atpku.client.model.PostResult;
import atpku.client.util.StringRequestWithCookie;

/**
 * Created by wyh on 2016/5/19.
 */
public class SearchResultWindow extends Activity
{
    public ListView resultList;
    private com.android.volley.RequestQueue volleyQuque;

    public ActionBar actionBar;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchresult);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        volleyQuque = Volley.newRequestQueue(this);

        resultList = (ListView)findViewById(R.id.searchResult_list);
        setTitle("搜索结果");
        final MessageAdapter adapter = new MessageAdapter(this, R.layout.message_row);

        Intent intent = this.getIntent();
        String caller = (String)intent.getSerializableExtra("caller");
        if(caller.equals("UserInfoWindow"))
            setTitle("我发送过的信息");

        HashMap<String, String> params = (HashMap<String, String>)intent.getSerializableExtra("params");
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                "http://139.129.22.145:5000/message/search",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if(result.success)
                        {
                            List<Message> messages = JSON.parseArray(result.data, Message.class);
                            for(Message message:messages) {
                                adapter.add(message);
                            }
                            resultList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        else
                        {
                            Toast.makeText(SearchResultWindow.this, result.message, Toast.LENGTH_LONG).show();
                        }
                        Log.d("TAG", response);
                    }
                }, params);
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
                    Intent intent = new Intent(SearchResultWindow.this, MsgWindow.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("messageID", msgid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            TextView titleText = (TextView) view.findViewById(R.id.title);
            TextView timeText = (TextView) view.findViewById(R.id.time);
            TextView nicknameText = (TextView) view.findViewById(R.id.nickname);

            titleText.setText(msg.getTitle());
            timeText.setText(msg.getPostTime());
            nicknameText.setText(msg.owner.getNickname());
            return view;
        }
    }
}

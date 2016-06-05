package atpku.client.window;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atpku.client.R;
import atpku.client.model.Place;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.Message;
import atpku.client.model.PostResult;
import atpku.client.util.ThemeUtil;

/**
 * Created by wyh on 2016/5/19.
 */
public class ReportHandlingWindow extends AppCompatActivity {
    public ListView msgList;
    public ActionBar actionBar;
    private RequestQueue volleyQuque;

    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportlist);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        msgList = (ListView) findViewById(R.id.reportList);

        volleyQuque = Volley.newRequestQueue(this);
        refreshMessageList();
    }

    public void refreshMessageList() {
        final MessageAdapter adapter = new MessageAdapter(this, R.layout.reporthandling);
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.GET,
                "http://139.129.22.145:5000/reportedmessage",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            List<Message> messages = JSON.parseArray(result.data, Message.class);
                            for (Message message : messages) {
                                adapter.add(message);
                            }
                        } else
                            Toast.makeText(ReportHandlingWindow.this, result.message, Toast.LENGTH_LONG).show();
                        msgList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
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
                    Intent intent = new Intent(ReportHandlingWindow.this, MsgWindow.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("messageID", msgid);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            TextView msgReportedText = (TextView) view.findViewById(R.id.msgReportNum);
            TextView titleText = (TextView) view.findViewById(R.id.title);
            TextView nicknameText = (TextView) view.findViewById(R.id.nickname);
            TextView timeText = (TextView) view.findViewById(R.id.time);
            TextView placeText = (TextView) view.findViewById(R.id.place);

            msgReportedText.setText("被举报次数：" + msg.getReportTimes());
            titleText.setText(msg.getTitle());
            nicknameText.setText(msg.owner.getNickname());
            timeText.setText(msg.getPostTime());
            placeText.setText(msg.getAtPlace().getName());
            return view;
        }
    }

    protected void onResume() {
        super.onResume();
        refreshMessageList();
    }
}

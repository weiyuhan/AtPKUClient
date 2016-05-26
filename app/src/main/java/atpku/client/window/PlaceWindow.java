package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import atpku.client.R;
import atpku.client.httputil.StringRequestWithCookie;
import atpku.client.model.PostResult;
import atpku.client.model.User;

/**
 * Created by wyh on 2016/5/19.
 */
public class PlaceWindow extends Activity {
    public ListView msgList;
    private int placeID;
    public ActionBar actionBar;
    private RequestQueue volleyQuque;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        msgList = (ListView) findViewById(R.id.place_msgList);

        Intent intent = this.getIntent();
        placeID = (int) intent.getSerializableExtra("id");
        volleyQuque = Volley.newRequestQueue(this);
        refreshMessageList();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_place, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void refreshMessageList() {
        final MessageAdapter adapter = new MessageAdapter(this, R.layout.message_row);
        Map<String, String> params = new HashMap<String, String>();
        params.put("keyword", "");
        params.put("placeid", "" + placeID);
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                "http://139.129.22.145:5000/message/search",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray messageArray = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < messageArray.length(); i++) {
                                String time = messageArray.getJSONObject(i).getString("postTime")
                                        .replaceAll("T", " ").substring(5);
                                adapter.add(new Message(messageArray.getJSONObject(i).getInt("id"),
                                        messageArray.getJSONObject(i).getString("title"),
                                        time,
                                        messageArray.getJSONObject(i).getJSONObject("owner").getString("nickname")));
                            }
                            msgList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Toast.makeText(PlaceWindow.this, "failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                }, params);
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
                Intent intent = new Intent(this, SendMsgWindow.class);
                startActivity(intent);
            }
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
            final int msgid = msg.getID();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PlaceWindow.this, MsgWindow.class);
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
            nicknameText.setText(msg.getOwnerNickname());
            return view;
        }
    }

    class Message {
        private int id;
        private String title;
        private String postTime;
        private String heat;
        private int ownerid;
        private String ownerNickname;
        private String ownerGender;

        public Message(int id, String title, String postTime, String ownerNickname) {
            this.id = id;
            this.title = title;
            this.postTime = postTime;
            this.ownerNickname = ownerNickname;
        }

        public int getID() {
            return this.id;
        }

        public String getTitle() {
            return this.title;
        }

        public String getPostTime() {
            return this.postTime + "";
        }

        public String getOwnerNickname() {
            return this.ownerNickname;
        }
    }
}

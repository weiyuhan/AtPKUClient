package atpku.client.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import atpku.client.R;
import atpku.client.model.Message;
import atpku.client.window.MsgWindow;

/**
 * Created by wyh on 2016/6/2.
 */
public class MessageAdapter extends ArrayAdapter<Message> {
    private int mResourceId;
    private Activity activity;

    public MessageAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.mResourceId = textViewResourceId;
        activity = (Activity)context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message msg = getItem(position);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(mResourceId, null);
        view.setClickable(true);
        final int msgid = msg.getId();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MsgWindow.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("messageID", msgid);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
        });

        TextView titleText = (TextView) view.findViewById(R.id.title);
        TextView timeText = (TextView) view.findViewById(R.id.time);
        TextView nicknameText = (TextView) view.findViewById(R.id.nickname);
        TextView hotText = (TextView) view.findViewById(R.id.hot);

        if (msg.getTitle().startsWith("//!?hot!?//"))
        {
            hotText.setText("热");
            titleText.setText(msg.getTitle().substring(11));
        }
        else
            titleText.setText(msg.getTitle());
        timeText.setText(msg.getPostTime());
        nicknameText.setText(msg.owner.getNickname());

        return view;
    }
}
package atpku.client.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import atpku.client.R;
import atpku.client.model.Comment;

/**
 * Created by wyh on 2016/5/30.
 */
public class CommentAdapter extends ArrayAdapter<Comment> {
    private int mResourceId;
    private Activity activity;

    public CommentAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.mResourceId = textViewResourceId;
        this.activity = (Activity)context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Comment comment = getItem(position);
        LayoutInflater inflater = activity.getLayoutInflater();
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
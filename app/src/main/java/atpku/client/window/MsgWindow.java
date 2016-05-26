package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import atpku.client.R;

/**
 * Created by wyh on 2016/5/19.
 */
public class MsgWindow extends Activity
{
    public TextView title;
    public TextView author;
    public TextView content;
    public TextView likeNum;
    public TextView dislikeNum;
    public Button likeButton;
    public Button dislikeButton;
    public EditText commentText;
    public Button commentButton;
    public ListView commentList;

    private int messageID;

    public ActionBar actionBar;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        title = (TextView)findViewById(R.id.msg_title);
        author = (TextView)findViewById(R.id.msg_author);
        content = (TextView)findViewById(R.id.msg_content);
        likeNum = (TextView)findViewById(R.id.msg_likeNum);
        likeButton = (Button)findViewById(R.id.msg_likeButton);
        dislikeNum = (TextView)findViewById(R.id.msg_dislikeNum);
        dislikeButton = (Button)findViewById(R.id.msg_dislikeButton);
        commentText = (EditText)findViewById(R.id.msg_addComment);
        commentButton = (Button)findViewById(R.id.msg_commentButton);
        commentList = (ListView)findViewById(R.id.msg_commentList);

        Intent intent = this.getIntent();
        messageID = (int) intent.getSerializableExtra("messageID");
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
}

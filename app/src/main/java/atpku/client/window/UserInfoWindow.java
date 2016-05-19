package atpku.client.window;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import atpku.client.R;

/**
 * Created by JIANG YUMENG on 2016/5/14.
 * Show user info.
 */
public class UserInfoWindow extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo);
    }
    public void feedbackHandler(View source) {
        Intent intent = new Intent(UserInfoWindow.this, FeedbackWindow.class);
        startActivity(intent);
    }

    public void userInfoBackHandler(View source)
    {
        super.onBackPressed();
    }
}

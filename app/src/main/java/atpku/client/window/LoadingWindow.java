package atpku.client.window;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import atpku.client.R;

/**
 * Created by wyh on 2016/5/19.
 */
public class LoadingWindow extends Activity
{
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActionBar().hide(); // 隐藏actionBar
        setContentView(R.layout.loading);

        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                /* Create an Intent that will start the Main WordPress Activity. */
                Intent mainIntent = new Intent(LoadingWindow.this, MapWindow.class);
                startActivity(mainIntent);
                finish();
            }
        }, 3000); //3000 for release
    }
}

package atpku.client.window;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import atpku.client.R;
import atpku.client.util.ThemeUtil;

/**
 * Created by wyh on 2016/5/20.
 */
public class HelpWindow extends AppCompatActivity {
    private ActionBar actionBar = null;
    private TextView helpcontent;

    public void onCreate(Bundle savedInstanceState)
    {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usehelp);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        helpcontent = (TextView) findViewById(R.id.helpcontent);
        helpcontent.setText("欢迎使用AtPKU。\n"
                        + "本app的主要功能是提供基于地理位置的信息。\n"
                        + "作为游客，您可以查看各主要地点及其信息摘要。\n"
                        + "登录之后就可以发送信息，对信息进行评论、点赞等操作。\n"
                        + "注册时请填写PKU邮箱，我们会发送确认邮件，确认之后即注册成功。\n"
                        + "\n一些额外的信息：\n"
                        + "注册时性别只要没有选中男，就视为女。\n"
                        + "你可以对自己发送的信息进行赞、踩或举报。\n"
                        + "主地图的信息每2分钟会自动刷新一次。暂时不支持用户自定义该间隔时间。\n"
        );
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
}

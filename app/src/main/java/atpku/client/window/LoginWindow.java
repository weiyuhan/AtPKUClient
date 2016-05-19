package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import atpku.client.R;


public class LoginWindow extends Activity
{
    public EditText username;
    public EditText password;
    public Button loginButton;
    public Button registButton;
    public ActionBar actionBar;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        username = (EditText)findViewById(R.id.login_username);
        password = (EditText)findViewById(R.id.login_password);
        loginButton = (Button)findViewById(R.id.loginButton);
        registButton = (Button)findViewById(R.id.registButton);
    }

    public void loginHandler(View source)
    {
        MapWindow.isLogin = true;
        Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
        finish();
    }
    public void regisHandler(View source)
    {
        Intent intent = new Intent(LoginWindow.this, RegisterWindow.class);
        startActivity(intent);
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

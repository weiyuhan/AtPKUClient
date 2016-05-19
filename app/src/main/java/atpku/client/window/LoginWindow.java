package atpku.client.window;

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        username = (EditText)findViewById(R.id.login_username);
        password = (EditText)findViewById(R.id.login_password);
        loginButton = (Button)findViewById(R.id.loginButton);
        registButton = (Button)findViewById(R.id.registButton);
    }

    public void loginHandler(View source)
    {
        Intent intent = new Intent(LoginWindow.this, MapWindow.class);
        startActivity(intent);
    }
    public void regisHandler(View source)
    {
        Intent intent = new Intent(LoginWindow.this, RegisterWindow.class);
        startActivity(intent);
    }

}

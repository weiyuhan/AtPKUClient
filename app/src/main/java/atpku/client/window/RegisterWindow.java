package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import atpku.client.R;

public class RegisterWindow extends Activity
{
    public EditText studentNum;
    public EditText username;
    public EditText password;
    public EditText confirmPasswd;
    public RadioButton male;
    public RadioButton female;
    public RadioButton secret_sex;
    public static int GENDER_MALE = 0;
    public static int GENDER_FEMALE = 1;
    public static int GENDER_ORTHER = 2;
    public Button submitButton;
    public Button backButton;

    public ActionBar actionBar;



    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        studentNum = (EditText)findViewById(R.id.regist_studentNum);
        username = (EditText)findViewById(R.id.register_username);
        password = (EditText)findViewById(R.id.register_password);
        confirmPasswd = (EditText)findViewById(R.id.regist_confirmpasswd);
        male = (RadioButton)findViewById(R.id.register_male);
        female = (RadioButton)findViewById(R.id.register_female);
        secret_sex = (RadioButton)findViewById(R.id.register_secret);
        submitButton = (Button)findViewById(R.id.regist_submitButton);
    }

    public void registSubmitHandler(View source)
    {

    }

    public void registerBackHandler(View source)
    {
        super.onBackPressed();
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

package atpku.client.window;

import android.app.Activity;
import android.os.Bundle;
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



    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        studentNum = (EditText)findViewById(R.id.regist_studentNum);
        username = (EditText)findViewById(R.id.register_username);
        password = (EditText)findViewById(R.id.register_password);
        confirmPasswd = (EditText)findViewById(R.id.regist_confirmpasswd);
        male = (RadioButton)findViewById(R.id.register_male);
        female = (RadioButton)findViewById(R.id.register_female);
        secret_sex = (RadioButton)findViewById(R.id.register_secret);
        submitButton = (Button)findViewById(R.id.regist_submitButton);
        backButton = (Button)findViewById(R.id.regist_backButton);
    }

    public void registSubmitHandler(View source)
    {

    }

    public void registerBackHandler(View source)
    {
        super.onBackPressed();
    }
}

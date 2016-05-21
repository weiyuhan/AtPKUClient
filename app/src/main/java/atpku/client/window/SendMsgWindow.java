package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import atpku.client.R;

/**
 * Created by wyh on 2016/5/19.
 */
public class SendMsgWindow extends Activity
{
    public EditText title;
    public EditText content;
    public EditText startTime;
    public EditText endTime;
    public Spinner place;
    public Button submitButton;
    public ActionBar actionBar;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmsg);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);


        title = (EditText)findViewById(R.id.sendMsg_title);
        content = (EditText)findViewById(R.id.sendMsg_content);
        startTime = (EditText)findViewById(R.id.sendMsg_startTime);
        endTime = (EditText)findViewById(R.id.sendMsg_endTime);
        place = (Spinner)findViewById(R.id.sendMsg_selectPlace);
        submitButton = (Button)findViewById(R.id.sendMsg_submitButton);
    }

    public void sendMsgSelectTimeHanlder(View source)
    {
        final EditText text = (EditText)source;
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // TODO Auto-generated method stub
                //更新EditText控件日期 小于10加0
                text.setText(year + "-" + (month+1) + "-" + day);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH) );
        datePickerDialog.setTitle("选择时间");
        datePickerDialog.show();

    }

    public void sendMsgSubmitButton(View source)
    {

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

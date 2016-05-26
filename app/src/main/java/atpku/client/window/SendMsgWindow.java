package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atpku.client.R;
import atpku.client.httputil.StringRequestWithCookie;
import atpku.client.model.Place;
import atpku.client.model.PostResult;

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
    private com.android.volley.RequestQueue volleyQuque;
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

        InputFilter[] filters = {new InputFilter.LengthFilter(18)};
        title.setFilters(filters);

        volleyQuque = Volley.newRequestQueue(this);

        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1);
        for(String placename:MapWindow.places.keySet()) {
            adapter.add(placename);
        }
        place.setAdapter(adapter);
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
                text.setText(year + "-" + (month+1) + "-" + day);   // month+1 because month started from 0 by default!
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH) );
        datePickerDialog.setTitle("选择时间");
        datePickerDialog.show();

    }

    public void sendMsgSubmitHandler(View source)
    {
        Map<String, String> params = new HashMap<String, String>();

        // 需要在本地检查title和content的合法性，比如不能为空，长度不能过长（可能需要与后端交流
        if (title.getText().toString().equals(""))
        {
            Toast.makeText(SendMsgWindow.this, "请填写标题", Toast.LENGTH_LONG).show();
            return;
        }
        if (startTime.getText().toString().equals(""))
        {
            Toast.makeText(SendMsgWindow.this, "请选择起始时间", Toast.LENGTH_LONG).show();
            return;
        }
        if (endTime.getText().toString().equals(""))
        {
            Toast.makeText(SendMsgWindow.this, "请选择截止时间", Toast.LENGTH_LONG).show();
            return;
        }
        params.put("title", title.getText().toString());
        params.put("content", content.getText().toString());

        String placename = (String)place.getSelectedItem();
        Place chosen = MapWindow.places.get(placename);
        params.put("atPlaceid", String.valueOf(chosen.getId()));

        // startTime和endTime的格式可能需要转化！需要与后端交流
        String startTimeStr = startTime.getText().toString()+" 00:00:00";
        String endTimeStr =  endTime.getText().toString()+" 23:59:59";
        params.put("startTime", startTimeStr);
        params.put("endTime", endTimeStr);

        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,"http://139.129.22.145:5000/message",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if(result.success)
                        {
                            Toast.makeText(SendMsgWindow.this, "发送成功", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(SendMsgWindow.this, result.message, Toast.LENGTH_LONG).show();
                        }
                        Log.d("TAG", response);
                    }
                }, params);
        volleyQuque.add(stringRequest);
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

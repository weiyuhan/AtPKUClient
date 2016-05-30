package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import atpku.client.R;
import atpku.client.model.Place;
import atpku.client.model.PostResult;
import atpku.client.util.StringRequestWithCookie;

/**
 * Created by wyh on 2016/5/19.
 */
public class SearchMsgWindow extends Activity
{
    public EditText title;
    public EditText author;
    public EditText content;
    public EditText startTime;
    public EditText endTime;
    public Spinner place;
    public Switch isGlobal;
    public Button submitButton;
    private ActionBar actionBar = null;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.advancesearch);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        title = (EditText)findViewById(R.id.advanceSearch_title);
        author = (EditText)findViewById(R.id.advanceSearch_author);
        content = (EditText)findViewById(R.id.advanceSearch_content);
        startTime = (EditText)findViewById(R.id.advanceSearch_startTime);
        endTime = (EditText)findViewById(R.id.advanceSearch_endTime);
        place = (Spinner)findViewById(R.id.advanceSearch_place);
        isGlobal = (Switch)findViewById(R.id.advanceSearch_global);
        submitButton = (Button)findViewById(R.id.advanceSearch_submitButton);


        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this, R.layout.place_spiner_row);
        for(String placename:MapWindow.places.keySet()) {
            adapter.add(placename);
        }
        place.setAdapter(adapter);
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

    public void searchMsgSelectTimeHanlder(View source)
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

    public void searchMsgSubmitHandler(View source)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("keyword", content.getText().toString());
        params.put("nickname", author.getText().toString());
        params.put("title", title.getText().toString());

        String placename = (String)place.getSelectedItem();
        Place chosen = MapWindow.places.get(placename);
        params.put("placeid", String.valueOf(chosen.getId()));

        String temp = startTime.getText().toString();
        if(!temp.equals(""))
            params.put("startTime", temp +" 00:00:00");
        temp = endTime.getText().toString();
        if(!temp.equals(""))
            params.put("endTime", temp + " 23:59:59");

        if(isGlobal.isChecked())
            params.put("isHot", "1");
        else
            params.put("isHot", "0");

        Intent intent = new Intent(SearchMsgWindow.this, SearchResultWindow.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("params", params);
        bundle.putSerializable("caller", "SearchMsgWindow");
        intent.putExtras(bundle);
        startActivity(intent);
    }
}

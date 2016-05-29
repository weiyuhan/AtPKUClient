package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import atpku.client.R;
import atpku.client.util.StringRequestWithCookie;
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
    public ImageView imageView;

    public  static int PHOTO_REQUEST_GALLERY = 0;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmsg);

        Intent intent = this.getIntent();
        int placeId = (Integer)intent.getSerializableExtra("placeId");

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
        imageView = (ImageView)findViewById(R.id.sendMsg_image);

        InputFilter[] filters = {new InputFilter.LengthFilter(18)};
        title.setFilters(filters);

        volleyQuque = Volley.newRequestQueue(this);

        ArrayAdapter<String> adapter =  new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1);
        int position = -1;
        int index = 0;
        for(String placename:MapWindow.places.keySet())
        {
            Place place = MapWindow.places.get(placename);
            if(place.getId() == placeId)
                position = index;
            adapter.add(placename);
            index++;
        }
        place.setAdapter(adapter);
        if(placeId != -1 && position != -1)
        {
            place.setSelection(position);
        }
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

    public void addImageSubmitHandler(View view)
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                Picasso.with(this).load(uri).resize(200,200).into(imageView);

            }

        }
    }
}

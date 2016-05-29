package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import atpku.client.AtPKUApplication;
import atpku.client.R;
import atpku.client.model.Message;
import atpku.client.util.ImageAdapter;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.Place;
import atpku.client.model.PostResult;
import atpku.client.util.Utillity;

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

    public ListView imageList;
    public ImageAdapter imageAdapter;

    public List<String> uploadedImgUris;

    public static int uploadedImgs = 0;

    public List<String> imgUris;

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
        imageList = (ListView)findViewById(R.id.sendMsg_imageList);

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

        imgUris = new ArrayList<String>();
        uploadedImgUris = new ArrayList<String>();
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
        final Map<String, String> params = new HashMap<String, String>();

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

        System.out.println(imgUris);
        for(String imgUri:imgUris)
        {
            Calendar calendar = Calendar.getInstance();
            String objectKey = MapWindow.getCookie().substring(10) + calendar.get(Calendar.YEAR) +
                    calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) +
                    calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
            String types = imgUri.substring(imgUri.lastIndexOf(".") - 1);
            PutObjectRequest put = new PutObjectRequest("public-image-source", objectKey + types, imgUri);
            OSSAsyncTask task = ((AtPKUApplication)getApplication()).oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    if(uploadedImgUris == null)
                    {
                        uploadedImgUris = new ArrayList<String>();
                    }
                    uploadedImgs++;
                    Log.d("PutObject", "UploadSuccess");
                    Log.d("ETag", result.getETag());
                    Log.d("RequestId", result.getRequestId());
                    System.out.println(uploadedImgs + "    " + imgUris.size());
                    uploadedImgUris.add("http://" + request.getBucketName() + ".oss-cn-shanghai.aliyuncs.com/" + request.getObjectKey());
                    if(uploadedImgs >= imgUris.size())
                    {
                        sendMsgRequest(params);
                    }
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    // 请求异常
                    uploadedImgs++;
                    if (clientExcepion != null) {
                        // 本地异常如网络异常等
                        clientExcepion.printStackTrace();
                    }
                    if (serviceException != null) {
                        // 服务异常
                        Log.e("ErrorCode", serviceException.getErrorCode());
                        Log.e("RequestId", serviceException.getRequestId());
                        Log.e("HostId", serviceException.getHostId());
                        Log.e("RawMessage", serviceException.getRawMessage());
                    }
                    if(uploadedImgs >= imgUris.size())
                    {
                        sendMsgRequest(params);
                    }
                }
            });
        }

    }

    public void sendMsgRequest(Map<String, String> params)
    {
        if(uploadedImgUris != null) {
            String urisJson = JSON.toJSONString(uploadedImgUris);
            params.put("images", urisJson);
        }
        System.out.println("fuck1");
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(imageAdapter == null) {
            imageAdapter = new ImageAdapter(this, R.layout.image_row);
            imageList.setAdapter(imageAdapter);
            imgUris = new ArrayList<String>();
        }
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                String uri_s = uri.toString();
                String imgPath = Utillity.getRealPathFromUri(this, uri);

                System.out.println(imgPath);

                if(!imgUris.contains(imgPath))
                {
                    imgUris.add(imgPath);
                    imageAdapter.add(uri_s);
                    imageList.setAdapter(imageAdapter);
                }
            }
        }

    }


}

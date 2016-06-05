package atpku.client.window;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atpku.client.AtPKUApplication;
import atpku.client.R;
import atpku.client.util.ImageAdapter;
import atpku.client.util.ImageDialog;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.Place;
import atpku.client.model.PostResult;
import atpku.client.util.Utillity;

/**
 * Created by wyh on 2016/5/19.
 */
public class SendMsgWindow extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public TextInputLayout title;
    public TextInputLayout content;
    public EditText startTime;
    public EditText endTime;
    public Spinner place;
    public Button submitButton;
    public ActionBar actionBar;
    public ProgressBar progressBar;
    private com.android.volley.RequestQueue volleyQuque;

    public GridView imageList;
    public ImageAdapter imageAdapter;

    public List<String> uploadedImgUris;

    public static int uploadedImgs = 0;

    public List<String> imgUris;

    public static int PHOTO_REQUEST_GALLERY = 0;
    public static int PHOTO_REQUEST_CAREMA = 1;


    private File cameraImageTempFile;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmsg);

        Intent intent = this.getIntent();
        int placeId = (Integer) intent.getSerializableExtra("placeId");

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);


        title = (TextInputLayout) findViewById(R.id.sendMsg_title);
        content = (TextInputLayout) findViewById(R.id.sendMsg_content);
        startTime = (EditText) findViewById(R.id.sendMsg_startTime);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        startTime.setText(String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day));
        endTime = (EditText) findViewById(R.id.sendMsg_endTime);
        endTime.setText(String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(day));
        place = (Spinner) findViewById(R.id.sendMsg_selectPlace);
        submitButton = (Button) findViewById(R.id.sendMsg_submitButton);
        imageList = (GridView) findViewById(R.id.sendMsg_imageList);
        imageList.setOnItemClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.sendMsg_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        imageAdapter = new ImageAdapter(this, R.layout.image_row);
        imageAdapter.add(String.valueOf(R.mipmap.camera));
        imageAdapter.add(String.valueOf(R.mipmap.plus));


        imageList.setAdapter(imageAdapter);

        imgUris = new ArrayList<String>();

        InputFilter[] filters = {new InputFilter.LengthFilter(50)};
        title.getEditText().setFilters(filters);

        volleyQuque = Volley.newRequestQueue(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.place_spiner_row);
        int position = -1;
        int index = 0;
        for (String placename : MapWindow.places.keySet()) {
            Place place = MapWindow.places.get(placename);
            if (place.getId() == placeId)
                position = index;
            adapter.add(placename);
            index++;
        }
        place.setAdapter(adapter);
        if (placeId != -1 && position != -1) {
            place.setSelection(position);
        }

        imgUris = new ArrayList<String>();
        uploadedImgUris = new ArrayList<String>();
    }

    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    public void sendMsgSelectTimeHanlder(View source) {
        final EditText text = (EditText) source;
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // TODO Auto-generated method stub
                //更新EditText控件日期 小于10加0
                text.setText(year + "-" + (month + 1) + "-" + day);   // month+1 because month started from 0 by default!
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("选择时间");
        datePickerDialog.show();

    }

    public void sendMsgSubmitHandler(View source) {
        if (!MapWindow.isLogin) {
            Toast.makeText(this, "请登录", Toast.LENGTH_LONG).show();
            return;
        }
        final Map<String, String> params = new HashMap<String, String>();

        // 需要在本地检查title和content的合法性，比如不能为空，长度不能过长（可能需要与后端交流
        if (title.getEditText().getText().toString().length() == 0) {
            title.setErrorEnabled(true);
            title.setError("请填写标题");
            return;
        } else
            title.setErrorEnabled(false);
        if (content.getEditText().toString().length() == 0) {
            content.setErrorEnabled(true);
            content.setError("请填写内容");
            return;
        } else
            content.setErrorEnabled(false);
        submitButton.setEnabled(false);
        submitButton.setText("发送中");
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        params.put("title", title.getEditText().getText().toString());
        params.put("content", content.getEditText().getText().toString());

        String placename = (String) place.getSelectedItem();
        Place chosen = MapWindow.places.get(placename);
        params.put("atPlaceid", String.valueOf(chosen.getId()));

        // startTime和endTime的格式可能需要转化！需要与后端交流
        String startTimeStr = startTime.getText().toString() + " 00:00:00";
        String endTimeStr = endTime.getText().toString() + " 23:59:59";
        params.put("startTime", startTimeStr);
        params.put("endTime", endTimeStr);

        if (imgUris.size() == 0) {
            sendMsgRequest(params);
        }
        System.out.println(imgUris);
        for (String imgUri : imgUris) {
            Calendar calendar = Calendar.getInstance();
            String objectKey = MapWindow.getCookie().substring(10) + calendar.get(Calendar.YEAR) +
                    calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) +
                    calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
            String types = imgUri.substring(imgUri.lastIndexOf(".") - 1);
            PutObjectRequest put = new PutObjectRequest("public-image-source", objectKey + types, imgUri);

            if (imgUris.size() == 1) {
                put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                    @Override
                    public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                        Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                        double percent = (double) currentSize / (double) totalSize;
                        int progress = (int) percent * 100;
                        if (progress > progressBar.getProgress())
                            progressBar.setProgress(progress);
                    }
                });
            }

            OSSAsyncTask task = ((AtPKUApplication) getApplication()).oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    if (uploadedImgUris == null) {
                        uploadedImgUris = new ArrayList<String>();
                    }
                    uploadedImgs++;
                    if (imgUris.size() > 1) {
                        double percent = uploadedImgs / imgUris.size();
                        int progress = (int) percent * 100;
                        progressBar.setProgress(progress);
                    }
                    Log.d("PutObject", "UploadSuccess");
                    Log.d("ETag", result.getETag());
                    Log.d("RequestId", result.getRequestId());
                    System.out.println(uploadedImgs + "    " + imgUris.size());
                    uploadedImgUris.add("http://" + request.getBucketName() + ".img-cn-shanghai.aliyuncs.com/" + request.getObjectKey());
                    if (uploadedImgs >= imgUris.size()) {
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
                    if (uploadedImgs >= imgUris.size()) {
                        sendMsgRequest(params);
                    }
                }
            });
        }

    }

    public void sendMsgRequest(Map<String, String> params) {
        progressBar.setProgress(100);
        if (uploadedImgUris != null && uploadedImgUris.size() != 0) {
            String urisJson = JSON.toJSONString(uploadedImgUris);
            params.put("images", urisJson);
        }
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST, "http://139.129.22.145:5000/message",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        submitButton.setText("发送");
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            Toast.makeText(SendMsgWindow.this, "发送成功", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            submitButton.setEnabled(true);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SendMsgWindow.this, result.message, Toast.LENGTH_LONG).show();
                        }
                        Log.d("TAG", response);
                    }
                }, params);
        volleyQuque.add(stringRequest);
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

    public void addImageSubmitHandler(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    public void useCameraHandler(View view) {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Calendar calendar = Calendar.getInstance();
        File cameraDir = new File(Environment.getExternalStorageDirectory(), "AtPKUCameraTemp");
        if (!cameraDir.exists())
            cameraDir.mkdirs();
        cameraImageTempFile = new File(cameraDir, "atpku" + calendar.get(Calendar.YEAR) +
                calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) +
                calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) +
                calendar.get(Calendar.MILLISECOND) + ".jpg");
        // 从文件中创建uri
        Uri cameraImageUri = Uri.fromFile(cameraImageTempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                String uri_s = uri.toString();
                String imgPath = Utillity.getRealPathFromUri(this, uri);

                System.out.println(imgPath);

                if (!imgUris.contains(imgPath)) {
                    imgUris.add(imgPath);
                    imageAdapter.add(uri_s);
                    imageList.setAdapter(imageAdapter);
                    System.out.println(imageAdapter.getCount());
                }
            }
        }
        if (requestCode == PHOTO_REQUEST_CAREMA) {
            String imgPath = cameraImageTempFile.getAbsolutePath();

            if (cameraImageTempFile.exists() && cameraImageTempFile.canRead()) {

                System.out.println(imgPath);


                if (!imgUris.contains(imgPath)) {
                    imgUris.add(imgPath);
                    imageAdapter.add(Uri.fromFile(cameraImageTempFile).toString());
                    imageList.setAdapter(imageAdapter);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
        if (position == 0) {
            useCameraHandler(null);
        }

        if (position == 1) {
            addImageSubmitHandler(null);
        }


        if (position >= 2) {
            final String imgUrl = (String) parent.getItemAtPosition(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请选择您要做的事");
            builder.setCancelable(true);
            String[] items = new String[]{"显示图片", "删除图片"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if (which == 0) {
                        System.out.println(imgUrl);
                        ImageDialog imageDialog = new ImageDialog(SendMsgWindow.this, imgUrl);
                        imageDialog.show();
                    }
                    if (which == 1) {
                        String imgPath = imgUris.get(position - 2);
                        imgUris.remove(imgPath);
                        imageAdapter.remove(imgUrl);
                        imageAdapter.notifyDataSetChanged();
                    }
                }
            });
            builder.show();
        }
    }
}

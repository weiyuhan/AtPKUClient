package atpku.client.window;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import atpku.client.AtPKUApplication;
import atpku.client.R;
import atpku.client.model.Message;
import atpku.client.model.PostResult;
import atpku.client.model.User;
import atpku.client.util.StringRequestWithCookie;

/**
 * Created by JIANG YUMENG on 2016/5/28.
 */
public class EditMyInfoWindow extends Activity implements View.OnClickListener
{
    public EditText username;
    public RadioButton male;
    public RadioButton female;
    public RadioButton secret_sex;
    public ActionBar actionBar;
    private RequestQueue volleyQuque;
    public ImageView avatarView;

    public static int PHOTO_REQUEST_CUT = 0;
    public static int PHOTO_REQUEST_GALLERY = 1;

    private boolean needSubmit;

    public boolean modifyAvatar;
    public boolean uploaded;
    public String avatarUrl;
    public File avatarFile;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editmyinfo);

        actionBar = getActionBar();
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        username = (EditText)findViewById(R.id.editmyinfo_username);
        male = (RadioButton)findViewById(R.id.editmyinfo_male);
        female = (RadioButton)findViewById(R.id.editmyinfo_female);
        secret_sex = (RadioButton)findViewById(R.id.editmyinfo_secret);

        avatarView = (ImageView)findViewById(R.id.editmyinfo_avatar);

        setTitle("修改个人资料");

        volleyQuque = Volley.newRequestQueue(this);

        String userAvatarUrl = MapWindow.user.avatar;
        Picasso.with(this).load(userAvatarUrl).placeholder(R.mipmap.image_loading).error(R.mipmap.image_error).resize(200,200).into(avatarView);
        avatarView.setOnClickListener(this);
    }

    public void editMyInfoSubmitHandler(View source) {
        needSubmit = false;

        HashMap<String, String> params = new HashMap<String, String>();
        if (!username.getText().toString().equals("")) {
            needSubmit = true;
            params.put("nickname", username.getText().toString());
        }
        String genderStr = null;
        if(male.isChecked()) {
            System.out.println("male");
            needSubmit = true;
            genderStr = "m";
            params.put("gender", genderStr);
        }
        else if(female.isChecked()) {
            System.out.println("female");
            needSubmit = true;
            genderStr = "f";
            params.put("gender", genderStr);
        }
        else if(secret_sex.isChecked()){
            Toast.makeText(EditMyInfoWindow.this, "服务器不支持私密性别！", Toast.LENGTH_LONG).show();
            // 服务器不支持私密性别！
        }

        if(modifyAvatar && uploaded && avatarUrl != null)
        {
            System.out.println(avatarUrl);
            params.put("avatar", avatarUrl);
            StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                    "http://139.129.22.145:5000/profile",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            PostResult result = JSON.parseObject(response, PostResult.class);
                            if(result.success) {
                                Toast.makeText(EditMyInfoWindow.this, result.message, Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else {
                                Toast.makeText(EditMyInfoWindow.this, result.message, Toast.LENGTH_LONG).show();
                            }
                            Log.d("TAG", response);
                        }
                    }, params);
            volleyQuque.add(stringRequest);
        }

        if(needSubmit) {
            StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                    "http://139.129.22.145:5000/profile",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            PostResult result = JSON.parseObject(response, PostResult.class);
                            if(result.success) {
                                Toast.makeText(EditMyInfoWindow.this, result.message, Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else {
                                Toast.makeText(EditMyInfoWindow.this, result.message, Toast.LENGTH_LONG).show();
                            }
                            Log.d("TAG", response);
                        }
                    }, params);
            volleyQuque.add(stringRequest);
        }
        else {
            Toast.makeText(EditMyInfoWindow.this, "没有可以提交的东西！", Toast.LENGTH_LONG).show();
        }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }

        }
        if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                saveBitmap(bitmap);
                Uri uri = Uri.fromFile(avatarFile);
                Picasso.with(this).load(uri).placeholder(R.mipmap.image_loading).error(R.mipmap.image_error).resize(200,200).into(avatarView);

                String avatarPath = uri.getEncodedPath();
                modifyAvatar = true;
                System.out.println(avatarPath);
                try {
                    modifyAvater(avatarPath);
                }catch (Exception e){e.printStackTrace();}

            }
        }
    }

    public void modifyAvater(String imgUri)
    {
        Calendar calendar = Calendar.getInstance();
        String objectKey = "avatar" + MapWindow.user.getId() + calendar.get(Calendar.YEAR) +
                calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) +
                calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
        String types = imgUri.substring(imgUri.lastIndexOf(".") - 1);
        PutObjectRequest put = new PutObjectRequest("public-image-source", objectKey + types, imgUri);
        OSSAsyncTask task = ((AtPKUApplication)getApplication()).oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                uploaded = true;
                avatarUrl = "http://" + request.getBucketName() + ".img-cn-shanghai.aliyuncs.com/" + request.getObjectKey();

            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
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
            }
        });
    }

    public void saveBitmap(Bitmap bm)
    {
        Calendar calendar = Calendar.getInstance();
        File cameraDir =  new File(Environment.getExternalStorageDirectory(),"AtPKUCameraTemp");
        if(!cameraDir.exists())
            cameraDir.mkdirs();
        avatarFile = new File(cameraDir,"avatar" + calendar.get(Calendar.YEAR) +
                calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) +
                calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND) +
                calendar.get(Calendar.MILLISECOND) + ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(avatarFile);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        if(v instanceof ImageView)
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        }
    }

    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(StringRequest.Method.GET,"http://139.129.22.145:5000/profile",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if(result.success)
                        {
                            MapWindow.user = JSON.parseObject(result.data, User.class);
                            System.out.println(MapWindow.user);
                        }
                        else
                        {
                            Toast.makeText(EditMyInfoWindow.this, result.message, Toast.LENGTH_LONG).show();
                        }
                        Log.d("TAG", response);
                    }
                }, null);
        volleyQuque.add(stringRequest);
    }
}

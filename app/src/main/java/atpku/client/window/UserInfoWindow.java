package atpku.client.window;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atpku.client.AtPKUApplication;
import atpku.client.R;
import atpku.client.util.StringRequestWithCookie;
import atpku.client.model.PostResult;
import atpku.client.model.User;
import atpku.client.util.ThemeUtil;
import atpku.client.util.UserInfoAdapter;
import atpku.client.util.UserInfoLine;
import atpku.client.util.Utillity;

/**
 * Created by JIANG YUMENG on 2016/5/14.
 * Show user info.
 */
public class UserInfoWindow extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private User user;

    public ActionBar actionBar;
    private RequestQueue volleyQuque;

    public ListView imgList;
    public ListView infoList;

    public static int PHOTO_REQUEST_CUT = 0;
    public static int PHOTO_REQUEST_GALLERY = 1;

    public boolean modifyAvatar = false;
    public boolean uploaded;
    public String avatarUrl;
    public File avatarFile;
    public UserInfoAdapter imgAdapter;

    public boolean modifyGender = false;
    public String newGender;

    public boolean modifyNickname = false;
    public String newNickname;

    public boolean submitting = false;

    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.logo);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imgList = (ListView) findViewById(R.id.userInfo_imgList);
        infoList = (ListView) findViewById(R.id.userInfo_infoList);

        infoList.setOnItemClickListener(this);

        imgList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });

        volleyQuque = Volley.newRequestQueue(this);

        refreshUser(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: { // 修改昵称
                setNickname();
            }
            break;
            case 2: { // 修改性别
                selectGender();
            }
            break;
            case 3: { // 显示加入天数
                showJoinDays();
            }
            break;
            default:
                break;
        }
    }

    public void showJoinDays() {
        String diff = Utillity.dateDiff(user.date_joined.replaceAll("T", " "));
        if (diff != null) {
            Snackbar.make(findViewById(R.id.userInfo_layout), "您已加入AtPKU " + diff, Snackbar.LENGTH_LONG).show();
        }
    }

    public void setNickname() {
        final EditText text = new EditText(this);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("请输入新的昵称");
        builder.setView(text);
        builder.setCancelable(true);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nickname = text.getText().toString();
                if (!nickname.equals(user.nickname)) {
                    modifyNickname = true;
                    newNickname = nickname;
                    user.nickname = newNickname;
                    System.out.println(newNickname);
                }
                if (modifyNickname) {
                    modifyNickname = false;
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("nickname", newNickname);
                    submitChange(params);
                }
                dialog.dismiss();
            }
        }).setNegativeButton("取消", null);
        builder.show();
    }

    public void selectGender() {
        String items[] = {"男", "女"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("请选择您的性别");
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.dismiss();
                if (user.gender.equals("m") && which == 1) {
                    modifyGender = true;
                    newGender = "f";
                    user.gender = newGender;
                }
                if (user.gender.equals("f") && which == 0) {
                    modifyGender = true;
                    newGender = "m";
                    user.gender = newGender;
                }
                if (modifyGender) {
                    modifyGender = false;
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("gender", newGender);
                    submitChange(params);
                }
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void refreshUserInfo(boolean refreshAvatar) {
        System.out.println("refreshing UI");
        user = MapWindow.user;
        if (user.avatar == null)
            user.avatar = "http://public-image-source.img-cn-shanghai.aliyuncs.com/avatar33201652203559.jpg";
        System.out.println(user);
        if (user != null) {
            if(refreshAvatar) {
                imgAdapter = new UserInfoAdapter(this, R.layout.userinfo_row);
                imgAdapter.add(new UserInfoLine("用户头像", null, user.avatar, null));
                imgList.setAdapter(imgAdapter);
            }

            UserInfoAdapter infoAdapter = new UserInfoAdapter(this, R.layout.userinfo_row);
            infoAdapter.add(new UserInfoLine("昵称", user.nickname, null, String.valueOf(R.drawable.right_entry)));
            infoAdapter.add(new UserInfoLine("邮箱", user.email, null, String.valueOf(R.drawable.right_entry)));
            if (user.gender.equals("m"))
                infoAdapter.add(new UserInfoLine("性别", "男", null, String.valueOf(R.drawable.right_entry)));
            else
                infoAdapter.add(new UserInfoLine("性别", "女", null, String.valueOf(R.drawable.right_entry)));
            infoAdapter.add(new UserInfoLine("加入时间", Utillity.parseTimeString(user.date_joined), null, String.valueOf(R.drawable.right_entry)));
            if (user.isAdmin)
                infoAdapter.add(new UserInfoLine("状态", "管理员", null, String.valueOf(R.drawable.right_entry)));
            else if (user.isBanned)
                infoAdapter.add(new UserInfoLine("状态", "禁言", null, String.valueOf(R.drawable.right_entry)));
            else
                infoAdapter.add(new UserInfoLine("状态", "正常", null, String.valueOf(R.drawable.right_entry)));
            infoAdapter.add(new UserInfoLine("被赞总数", String.valueOf(user.likeReceived), null, String.valueOf(R.drawable.right_entry)));
            infoAdapter.add(new UserInfoLine("被评论总数", String.valueOf(user.commentReceived), null, String.valueOf(R.drawable.right_entry)));
            infoList.setAdapter(infoAdapter);
        }
    }

    public void refreshUser(final boolean refreshUI) {
        final boolean refreshui = refreshUI;
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(StringRequest.Method.GET, "http://139.129.22.145:5000/profile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        if (result.success) {
                            MapWindow.user = JSON.parseObject(result.data, User.class);
                            System.out.println(MapWindow.user);
                            SharedPreferences prefs = getSharedPreferences("userInfo", 1);
                            SharedPreferences.Editor mEditor = prefs.edit();
                            mEditor.putString("userInfoJson", result.data);
                            mEditor.apply();
                            mEditor.commit();
                            refreshUserInfo(refreshUI);
                        } else {
                            Snackbar.make(findViewById(R.id.userInfo_layout), result.message, Snackbar.LENGTH_LONG).show();
                        }
                        Log.d("TAG", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Snackbar.make(findViewById(R.id.userInfo_layout), "请检查网络连接", Snackbar.LENGTH_INDEFINITE).show();
                    }
                }, null);
        volleyQuque.add(stringRequest);
        if(submitting)
            submitting = false;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(getApplication()).inflate(R.menu.menu_userinfo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem mi) {
        if (mi.isCheckable()) {
            mi.setChecked(true);
        }

        switch (mi.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_feedback: {
                if(user == null)
                    return true;
                Intent intent = new Intent(UserInfoWindow.this, MyFeedbackWindow.class);
                startActivity(intent);
            }
            break;
            case R.id.action_mymsg: {
                if(user == null)
                    return true;
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("nickname", user.getNickname());
                Intent intent = new Intent(UserInfoWindow.this, SearchResultWindow.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("params", params);
                bundle.putSerializable("caller", "UserInfoWindow");
                intent.putExtras(bundle);
                startActivity(intent);
            }
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
                if (uri != null)
                    crop(uri);
            }

        }
        if (requestCode == PHOTO_REQUEST_CUT) {
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                if (bitmap != null) {
                    saveBitmap(bitmap);
                    Uri uri = Uri.fromFile(avatarFile);
                    String avatarPath = uri.getEncodedPath();
                    modifyAvatar = true;
                    System.out.println(avatarPath);
                    try {
                        modifyAvater(avatarPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void modifyAvater(String imgUri) {
        Calendar calendar = Calendar.getInstance();
        String objectKey = "avatar" + MapWindow.user.getId() + calendar.get(Calendar.YEAR) +
                calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH) +
                calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
        String types = imgUri.substring(imgUri.lastIndexOf(".") - 1);
        PutObjectRequest put = new PutObjectRequest("public-image-source", objectKey + types, imgUri);
        OSSAsyncTask task = ((AtPKUApplication) getApplication()).oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                uploaded = true;
                avatarUrl = "http://" + request.getBucketName() + ".img-cn-shanghai.aliyuncs.com/" + request.getObjectKey();
                Map<String, String> params = new HashMap<String, String>();
                params.put("avatar", avatarUrl);
                submitChange(params);
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


    public void submitChange(Map<String, String> params) {
        submitting = true;
        System.out.println(params);
        StringRequestWithCookie stringRequest = new StringRequestWithCookie(Request.Method.POST,
                "http://139.129.22.145:5000/profile",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        PostResult result = JSON.parseObject(response, PostResult.class);
                        Snackbar.make(findViewById(R.id.userInfo_layout), result.message, Snackbar.LENGTH_LONG).show();
                        if (result.success) {
                            if (modifyAvatar && uploaded) {
                                modifyAvatar = uploaded = false;
                                ImageView imageView = (ImageView) imgList.getChildAt(0).findViewById(R.id.userInfo_row_img);
                                Picasso.with(UserInfoWindow.this).load(Uri.fromFile(avatarFile)).resize(200, 200).into(imageView);
                            }
                            refreshUser(false);
                        }
                        Log.d("TAG", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Snackbar.make(findViewById(R.id.userInfo_layout), "提交失败，请检查网络连接", Snackbar.LENGTH_INDEFINITE).show();
                    }
                }, params);
        volleyQuque.add(stringRequest);
    }

    public void saveBitmap(Bitmap bm) {
        Calendar calendar = Calendar.getInstance();
        File cameraDir = new File(Environment.getExternalStorageDirectory(), "AtPKUCameraTemp");
        if (!cameraDir.exists())
            cameraDir.mkdirs();
        avatarFile = new File(cameraDir, "avatar" + calendar.get(Calendar.YEAR) +
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
}

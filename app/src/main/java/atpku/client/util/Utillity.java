package atpku.client.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wyh on 2016/5/29.
 */
public class Utillity
{
    public static String dateDiff(String date1)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date d2 = calendar.getTime();
        try
        {
            Date d1 = df.parse(date1);
            long diff = d2.getTime() - d1.getTime();//这样得到的差值是微秒级别

            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);

            return days+"天"+hours+"小时";
        }
        catch (Exception e)
        {
        }
        return null;
    }

    public static String parseTimeString(String str)
    {
        String date = str.substring(0, str.indexOf("T"));
        String time = str.substring(str.indexOf("T")+1);
        String[] dates = date.split("-");
        StringBuffer ret = new StringBuffer();
        ret.append(dates[0] + "年");
        ret.append(dates[1] + "月");
        ret.append(dates[2] + "日  ");
        ret.append(time);
        return ret.toString();
    }

    public static String getRealPathFromUri(Context context, Uri contentUri)
    {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}

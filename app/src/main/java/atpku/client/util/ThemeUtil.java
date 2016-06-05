package atpku.client.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import atpku.client.window.MapWindow;

/**
 * Created by wyh on 2016/6/5.
 */
public class ThemeUtil
{
    public static boolean themeChanged = false;
    public static int themeid;

    public static void changeTheme(int themeid)
    {
        if(ThemeUtil.themeid != themeid) {
            ThemeUtil.themeid = themeid;
            themeChanged = true;
        }
    }

    public static void setTheme(Activity activity)
    {
        activity.setTheme(themeid);
    }
}

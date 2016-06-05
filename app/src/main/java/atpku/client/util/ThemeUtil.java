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
    public static boolean themeChanged;
    public static int themeid;
    public static void init()
    {

    }
    public static void setTheme(Activity activity)
    {
        activity.setTheme(themeid);
    }
}

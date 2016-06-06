package atpku.client.window;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.support.design.widget.Snackbar;

import atpku.client.R;
import atpku.client.util.ThemeUtil;

/**
 * Created by wyh on 2016/6/5.
 */
public class SettingWindow extends AppCompatActivity
{

    public RadioButton grey;
    public RadioButton green;
    public RadioButton red;
    public RadioButton pink;
    public RadioButton purple;

    public ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtil.setTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        grey = (RadioButton)findViewById(R.id.setting_theme_grey);
        green = (RadioButton)findViewById(R.id.setting_theme_green);
        red = (RadioButton)findViewById(R.id.setting_theme_red);
        pink = (RadioButton)findViewById(R.id.setting_theme_pink);
        purple = (RadioButton)findViewById(R.id.setting_theme_purple);

        switch (ThemeUtil.themeid)
        {
            case R.style.AppTheme_Grey:
                grey.setChecked(true);
                break;
            case R.style.AppTheme_Green:
                green.setChecked(true);
                break;
            case R.style.AppTheme_Red:
                red.setChecked(true);
                break;
            case R.style.AppTheme_Pink:
                pink.setChecked(true);
                break;
            case R.style.AppTheme_Purple:
                purple.setChecked(true);
                break;
        }
    }

    public void settingSubmitHandler(View view)
    {
        int themeid = R.style.AppTheme_Grey;
        if(grey.isChecked())
        {
            themeid = R.style.AppTheme_Grey;
            ThemeUtil.changeTheme(themeid);
        }
        if(green.isChecked())
        {
            themeid = R.style.AppTheme_Green;
            ThemeUtil.changeTheme(themeid);
        }
        if(red.isChecked())
        {
            themeid = R.style.AppTheme_Red;
            ThemeUtil.changeTheme(themeid);
        }
        if(pink.isChecked())
        {
            themeid = R.style.AppTheme_Pink;
            ThemeUtil.changeTheme(themeid);
        }
        if(purple.isChecked())
        {
            themeid = R.style.AppTheme_Purple;
            ThemeUtil.changeTheme(themeid);
        }
        SharedPreferences prefs = getSharedPreferences("theme", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = prefs.edit();
        mEditor.putInt("Theme", themeid);
        mEditor.apply();
        mEditor.commit();
        Snackbar.make(findViewById(R.id.setting_linear), "修改成功", Snackbar.LENGTH_INDEFINITE)
                .setAction("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingWindow.this.finish();
            }
        }).setActionTextColor(getResources().getColor(R.color.white)).show();
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
}

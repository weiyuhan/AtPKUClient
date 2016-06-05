package atpku.client.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import atpku.client.R;

/**
 * Created by wyh on 2016/6/6.
 */
public class UserInfoAdapter extends ArrayAdapter<UserInfoLine>
{
    private int mResourceId;
    private Activity activity;
    public UserInfoAdapter(Context context, int res) {
        super(context, res);
        this.mResourceId = res;
        this.activity = (Activity)context;

    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        UserInfoLine line = getItem(position);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(mResourceId, null);


        if(line.key != null)
        {
            TextView textView = (TextView)view.findViewById(R.id.userInfo_row_key);
            textView.setText(line.key);
        }

        if(line.value != null)
        {
            TextView textView = (TextView)view.findViewById(R.id.userInfo_row_value);
            textView.setText(line.value);
        }

        if(line.imgUrl != null) {
            System.out.println(line.imgUrl);
            ImageView imageView = (ImageView)view.findViewById(R.id.userInfo_row_img);
            try {
                int resID = Integer.parseInt(line.imgUrl);
                Picasso.with(activity).load(resID).resize(200, 200).into(imageView);
            } catch (NumberFormatException e) {
                Picasso.with(activity).load(line.imgUrl).resize(200, 200).into(imageView);
            }
        }

        if(line.enterUrl != null) {
            ImageView enterImageView = (ImageView)view.findViewById(R.id.userInfo_row_enter);
            try {
                int resID = Integer.parseInt(line.enterUrl);
                Picasso.with(activity).load(resID).placeholder(R.mipmap.image_loading).error(R.mipmap.image_error).resize(72, 72).into(enterImageView);

            } catch (NumberFormatException e) {
                Picasso.with(activity).load(line.enterUrl).placeholder(R.mipmap.image_loading).error(R.mipmap.image_error).resize(72, 72).into(enterImageView);
            }
        }
        return view;
    }
}
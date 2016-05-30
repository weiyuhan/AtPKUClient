package atpku.client.util;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import atpku.client.R;

/**
 * Created by wyh on 2016/5/29.
 */
public class ImageAdapter extends ArrayAdapter<String>
{
    private int mResourceId;
    private Activity activity;
    public ImageAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.mResourceId = textViewResourceId;
        this.activity = (Activity)context;

    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        String imgUri = getItem(position);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(mResourceId, null);

        ImageView imageView = (ImageView)view.findViewById(R.id.sendMsg_image);

        try
        {
            int resID = Integer.parseInt(imgUri);
            Picasso.with(activity).load(resID).resize(200,200).into(imageView);

        }catch (NumberFormatException e)
        {
            Picasso.with(activity).load(imgUri).resize(200,200).into(imageView);
        }


        return view;
    }
}

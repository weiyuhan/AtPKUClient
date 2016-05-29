package atpku.client.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import atpku.client.R;

/**
 * Created by wyh on 2016/5/30.
 */
public class ImageDialog extends Dialog
{
    int layoutRes;//布局文件
    Context context;
    public String imgUrl;
    public ImageView imageView;
    public ImageDialog(Context context, String imgUrl)
    {
        super(context, R.style.customDialog);
        this.context = context;
        layoutRes = R.layout.imagedialog;
        this.imgUrl = imgUrl;
    }
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(layoutRes);
        imageView = (ImageView)findViewById(R.id.imgDialog_image);

        Picasso.with(context).load(imgUrl).into(imageView);
    }

}

package atpku.client.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by wyh on 2016/5/30.
 */
public class BitMapTarget implements Target
{
    public Bitmap bitmap;
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        this.bitmap = bitmap;
    }

    public void onBitmapFailed() {
    }

    public void onBitmapFailed(Drawable errorDrawable) {

    }

    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}

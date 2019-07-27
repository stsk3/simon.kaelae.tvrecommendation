package simon.kaelae.tvrecommendation;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import java.net.URI;

public class GridViewAdapter extends BaseAdapter {
    private Context mContext;

    // Keep all Images in array
//    public Integer[] mThumbIds = {
//            R.drawable.pic_1, R.drawable.pic_2,
//            R.drawable.pic_3, R.drawable.pic_4,
//            R.drawable.pic_5, R.drawable.pic_6,
//            R.drawable.pic_7, R.drawable.pic_8,
//            R.drawable.pic_9, R.drawable.pic_10,
//            R.drawable.pic_11, R.drawable.pic_12,
//            R.drawable.pic_13, R.drawable.pic_14,
//            R.drawable.pic_15
//    };

    Uri[] cardImageUrl = {Uri.parse("http://i.imgur.com/xTtwwzS.jpg"),
            Uri.parse("http://i.imgur.com/ucm7n4h.jpg"),
            Uri.parse("http://i.imgur.com/dydKNhm.jpg"),
            Uri.parse("http://i.imgur.com/a2yFl08.jpg"),
            Uri.parse("http://i.imgur.com/a7rFbnX.png"),
            Uri.parse("http://i.imgur.com/ManpvmP.png"),
            Uri.parse("http://i.imgur.com/wnl8bSg.jpg"),
            Uri.parse("http://i.imgur.com/1pvaI8X.jpg")};




    // Constructor
    public GridViewAdapter(Context c){
        mContext = c;
    }

    @Override
    public int getCount() {
        return cardImageUrl.length;
    }

    @Override
    public Object getItem(int position) {
        return cardImageUrl[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);

        Picasso.with(mContext).load(cardImageUrl[position]).into(imageView);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(200, 100));
        return imageView;
    }

}

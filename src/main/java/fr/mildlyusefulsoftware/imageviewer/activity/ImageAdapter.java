package fr.mildlyusefulsoftware.imageviewer.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

public class ImageAdapter extends BaseAdapter implements SpinnerAdapter {

	int mGalleryItemBackground;
	private Context mContext;

	public ImageAdapter(Context c) {
		mContext = c;

	}

	public int getCount() {
		return PicturePager.getInstance(mContext).getNumberOfPictures();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(mContext);

		byte[] thumbnailBytes = PicturePager.getInstance(mContext)
				.getPictureAt(position).getThumbnail();
		Bitmap b = BitmapFactory.decodeByteArray(thumbnailBytes, 0,
				thumbnailBytes.length);
		imageView.setImageBitmap(b);

		imageView.setLayoutParams(new Gallery.LayoutParams(150, 100));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setBackgroundResource(mGalleryItemBackground);

		return imageView;
	}

}

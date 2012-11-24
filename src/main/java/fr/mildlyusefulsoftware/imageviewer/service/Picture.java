package fr.mildlyusefulsoftware.imageviewer.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Picture {

	private byte[] thumbnail;

	private String imageURL;
	
	private int id;
	

	private static String TAG = "imageviewer";

	public Picture(int id, String imageURL,byte[] thumbnail) {
		super();
		this.thumbnail = thumbnail;
		this.imageURL = imageURL;
		this.id = id;
	}

	public byte[] getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static Bitmap getBitmapFromPicture(Picture picture) throws IOException {
		Log.d(TAG, "display "+picture.getImageURL());
		 //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new URL(picture.getImageURL()).openStream(),null,o);

        //The new size we want to scale to
        final int REQUIRED_SIZE=1600;

        //Find the correct scale value. It should be the power of 2.
        int width_tmp=o.outWidth, height_tmp=o.outHeight;
        int scale=1;
        while(true){
            if(width_tmp<=REQUIRED_SIZE && height_tmp<=REQUIRED_SIZE)
                break;
            width_tmp/=2;
            height_tmp/=2;
            scale*=2;
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        o2.inDither=false;                     //Disable Dithering mode
        o2.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared
        o2.inInputShareable=true;              //Which kind of reference will be used to recover the Bitmap data after being clear, when it will be used in the future
        o2.inTempStorage=new byte[32 * 1024];
        try {
        	return BitmapFactory.decodeStream(new URL(picture.getImageURL()).openStream(), null, o2);
        }catch (Exception e){
        	return null;
        }
		
	}

	
	public static byte[] getPictureThumbnail(String url) throws IOException {
		InputStream in = null;
		try {
			Log.d(TAG, "getPictureThumbnail " + url);
			in = new BufferedInputStream(new URL(url).openStream());
			final int THUMBNAIL_SIZE = 128;
			Bitmap imageBitmap = BitmapFactory.decodeStream(in);
			imageBitmap = Bitmap.createScaledBitmap(imageBitmap,
					THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
			return baos.toByteArray();
		} finally {
			in.close();
		}
	}
}

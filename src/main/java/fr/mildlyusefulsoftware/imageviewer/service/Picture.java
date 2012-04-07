package fr.mildlyusefulsoftware.imageviewer.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Picture {

	private byte[] thumbnail;

	private String imageURL;
	
	private int id;

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
		BufferedInputStream in = new BufferedInputStream(new URL(picture.getImageURL()).openStream());
		return BitmapFactory.decodeStream(in);
	}

	
	public static byte[] getPictureThumbnail(String url) throws IOException {
		InputStream in = null;
		try {
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

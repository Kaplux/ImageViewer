package fr.mildlyusefulsoftware.imageviewer.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.util.Log;
import fr.mildlyusefulsoftware.imageviewer.service.DatabaseHelper;
import fr.mildlyusefulsoftware.imageviewer.service.Picture;
import fr.mildlyusefulsoftware.imageviewer.service.PictureProvider;

public class PicturePager {

	private static String TAG = "cutekitty";
	private static PicturePager instance;

	private List<Picture> pictures;

	private PicturePager(final Context context) {
		DatabaseHelper db = DatabaseHelper.connect(context);
		try {
			pictures = db.getPictures();
			if (pictures.isEmpty()){
				try {
					db.copyDatabase();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		} finally {
			db.release();
		}

		db = DatabaseHelper.connect(context);
		try {
			pictures = db.getPictures();
		} finally {
			db.release();
		}

	}

	public static PicturePager getInstance(Context context) {
		if (instance == null) {
			instance = new PicturePager(context);
		}
		return instance;
	}

	public Picture getPictureAt(int i) {
		return pictures.get(i);
	}

	public int getNumberOfPictures() {
		return pictures.size();
	}

}

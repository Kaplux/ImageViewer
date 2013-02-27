package fr.mildlyusefulsoftware.imageviewer.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper {

	private static final String DB_NAME = "PICTURES.DB";
	private SQLiteDatabase db;
	private InternalDBHelper internalDBHelper ;
	private static ReentrantLock LOCK = new ReentrantLock();
	private static String TAG = "cutekitty";

	public static DatabaseHelper connect(Context context) {
		LOCK.lock();
		return new DatabaseHelper(context);
	}

	private DatabaseHelper(Context context) {
		super();
		internalDBHelper = new InternalDBHelper(context,
				DB_NAME, null, 3);
		db = internalDBHelper.getWritableDatabase();
	}

	public void release() {
		try {
			db.close();
		} finally {
			LOCK.unlock();
		}
	}

	public void putPictures(Collection<Picture> pictures) {
		for (Picture p : pictures) {
			ContentValues values = new ContentValues();
			values.put(InternalDBHelper.COL_ID, p.getId());
			values.put(InternalDBHelper.COL_IMAGE_URL, p.getImageURL());
			values.put(InternalDBHelper.COL_THUMBNAIL, p.getThumbnail());
			values.put(InternalDBHelper.COL_TITLE, p.getTitle());
			db.insert(InternalDBHelper.TABLE_PICTURES, null, values);
		}
	}
	
	public List<Picture> getPictures() {
		List<Picture> pictures=new ArrayList<Picture>();
		Cursor c = db.query(InternalDBHelper.TABLE_PICTURES, new String[] {
				InternalDBHelper.COL_ID,
				InternalDBHelper.COL_IMAGE_URL,
				InternalDBHelper.COL_THUMBNAIL,InternalDBHelper.COL_TITLE },null,
				null, null, null, InternalDBHelper.COL_ID + " DESC");
		if (c.getCount() != 0) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				Picture picture = new Picture(c.getInt(0), c.getString(1),
						c.getBlob(2),c.getString(0));
				pictures.add(picture);
				c.moveToNext();
			}
		}
		c.close();
		return pictures;
	}
	
	public void copyDatabase() throws IOException {
		internalDBHelper.copyDatabase();
	}

	class InternalDBHelper extends SQLiteOpenHelper {

		public static final String TABLE_PICTURES = "PICTURES";
		public static final String COL_ID = "ID";
		public static final String COL_IMAGE_URL = "URL";
		public static final String COL_THUMBNAIL = "THUMBNAIL";
		public static final String COL_TITLE = "TITLE";

		private static final String CREATE_BDD = "CREATE TABLE "
				+ TABLE_PICTURES + " (" + COL_ID
				+ " INTEGER PRIMARY KEY, " + COL_IMAGE_URL
				+ " TEXT NOT NULL, "+ COL_TITLE
				+ " TEXT NOT NULL, " + COL_THUMBNAIL + " BLOB NOT NULL);";
		
		private Context context;

		public InternalDBHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			this.context=context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_BDD);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//db.execSQL("DROP TABLE " + TABLE_PICTURES + ";");
			//onCreate(db);
			try {
				Log.i(TAG, "Upgrading database");
				copyDatabase();
			} catch (IOException e) {
				Log.e(TAG,e.getMessage());
			}
		}
		
		public void copyDatabase() throws IOException {
			Log.i(TAG, "copying database");
			File dbFile = context.getDatabasePath(DB_NAME);
			int BUFFER=2048;
			ZipInputStream zis = new ZipInputStream(context.getAssets().open(
					DB_NAME + ".zip"));
			BufferedOutputStream dest = null;
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				System.out.println("Extracting: " + entry);
				int count;
				byte data[] = new byte[BUFFER];
				FileOutputStream fos = new FileOutputStream(dbFile);
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();

		}
	}
}

package fr.mildlyusefulsoftware.imageviewer.activity;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.apperhand.device.android.AndroidSDKProvider;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import fr.mildlyusefulsoftware.imageviewer.R;
import fr.mildlyusefulsoftware.imageviewer.service.Picture;

public abstract class ViewPictureActivity extends Activity {

	private static String TAG = "cutekitty";
	private AdView adView;
	private int currentPosition = 0;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		AndroidSDKProvider.initSDK(this);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.view_picture_layout);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);

		Gallery pictureList = (Gallery) findViewById(R.id.pictureList);
		if (savedInstanceState != null) {
			currentPosition = savedInstanceState.getInt("POSITION");
			Log.d(TAG, "restore position " + currentPosition);
			pictureList.setSelection(currentPosition, true);
			loadImageFromPosition(currentPosition);
		} else {
			loadImageFromPosition(currentPosition);
		}
		pictureList.setAdapter(new ImageAdapter(this));
		pictureList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View v, int position,
					long id) {
				currentPosition = position;
				loadImageFromPosition(position);
			}
		});
		initAdBannerView();
	}

	private void loadImageFromPosition(final int pos) {
		final Activity currentActivity = this;

		new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... params) {
				Bitmap b=null;
				try {
					Picture p =PicturePager.getInstance(currentActivity).getPictureAt(
							pos);
					b = Picture.getBitmapFromPicture(p);
				} catch (IOException e1) {
					Log.e(TAG, e1.getMessage(), e1);
				}
				return b;
			}

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected void onPostExecute(Bitmap b) {
				if (b!=null){
					ImageView pictureView = (ImageView) findViewById(R.id.pictureView);
					pictureView.setImageBitmap(b);
				}
			}

		}.execute();

	}

	protected void initAdBannerView() {
		try {
			final ViewGroup quoteLayout = (ViewGroup) findViewById(R.id.view_picutre_root_layout);
			// Create the adView
			adView = new AdView(this, AdSize.BANNER, getAdMobId());
			LinearLayout adLayout = new LinearLayout(this);
			adLayout.addView(adLayout);
			// Add the adView to it
			quoteLayout.addView(adView);
			AdRequest ar = new AdRequest();
			// ar.addTestDevice(AdRequest.TEST_EMULATOR);

			// Initiate a generic request to load it with an ad
			adView.loadAd(ar);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}

	protected abstract String getAdMobId();

	@Override
	protected void onDestroy() {
		super.onPause();
		if (adView != null) {
			adView.destroy();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt("POSITION", currentPosition);
		Log.d(TAG, "position " + currentPosition);
		super.onSaveInstanceState(savedInstanceState);
	}

}

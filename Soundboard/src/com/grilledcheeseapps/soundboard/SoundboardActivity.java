package com.grilledcheeseapps.soundboard;

import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;


public class SoundboardActivity extends Activity implements AdListener {

	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_soundboard);

		setTheme();

		// Request an Ad
		adView = (AdView) findViewById(R.id.adView);
		adView.loadAd(new AdRequest());
		adView.setAdListener(this);
	}

	public void setTheme() {
		ImageView background = (ImageView) findViewById(R.id.background);
		try {
//			InputStream stream = getAssets().open("background_1.png");
//			BitmapDrawable img = new BitmapDrawable(getResources(), stream);
//			img.setGravity(android.view.Gravity.CENTER);
			background.setImageDrawable(new BitmapDrawable(getResources(), getAssets().open("background_1.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	@Override
	public void onReceiveAd(Ad arg0) {
		adView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onDismissScreen(Ad arg0) {
	}

	@Override
	public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
	}

	@Override
	public void onLeaveApplication(Ad arg0) {
	}
	
	@Override
	public void onPresentScreen(Ad arg0) {
	}

}

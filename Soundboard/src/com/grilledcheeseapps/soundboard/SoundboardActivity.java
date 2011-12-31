package com.grilledcheeseapps.soundboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;


public class SoundboardActivity extends Activity implements AdListener {

	private ArrayList<String> backgrounds;
	private ArrayList<SoundClip> soundClips;
	private AdView adView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_soundboard);
		loadSettings();
		buildClipButtons();
	}

	@Override
	public void onResume() {
		super.onResume();
		setTheme();
		
		// Request an Ad
		adView = (AdView) findViewById(R.id.adView);
		adView.loadAd(new AdRequest());
		adView.setAdListener(this);
	}
	
	public void setTheme() { 
		// Select a random background
		Random random = new Random((new Date()).getTime());
		int index = random.nextInt(backgrounds.size());

		ImageView background = (ImageView) findViewById(R.id.background);
		try {
			 // InputStream stream = getAssets().open("background_1.png");
			 // BitmapDrawable img = new BitmapDrawable(getResources(),
			// stream);
			// // img.setGravity(android.view.Gravity.CENTER);
			background.setImageDrawable(new BitmapDrawable(getResources(), getAssets().open(
					backgrounds.get(index))));
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	private void buildClipButtons() {
		
		LinearLayout buttonContainer = (LinearLayout)findViewById(R.id.buttonContainer);
		
		for (SoundClip clip : soundClips) {
			
		}
		
	}

	private void parseJsonSettingsFile(JSONObject jo) {
		// Parse backgrounds
		backgrounds = new ArrayList<String>();
		JSONArray objs = jo.optJSONArray("backgrounds");
		for (int i = 0; i < objs.length(); i++)
			backgrounds.add(objs.optJSONObject(i).optString("filename"));

		// Parse sound clips
		soundClips = new ArrayList<SoundClip>();
		objs = jo.optJSONArray("clips");
		if (objs != null)
			for (int i = 0; i < objs.length(); i++)
				soundClips.add(new SoundClip(objs.optJSONObject(i)));
	}

	private void loadSettings() {
		try {
			String json = "";
			InputStream is = getAssets().open("settings.json");
			if (is != null) {
				StringBuilder sb = new StringBuilder();
				String line;

				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					while ((line = reader.readLine()) != null) {
						sb.append(line).append("\n");
					}
				} finally {
					is.close();
				}
				json = sb.toString();
			}
			parseJsonSettingsFile(new JSONObject(json));
		} catch (JSONException e) {
			e.printStackTrace();
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

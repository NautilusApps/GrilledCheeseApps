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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;


public class SoundboardActivity extends Activity implements AdListener, OnClickListener {

	private String headerColor;
	private ArrayList<String> backgrounds;
	private ArrayList<SoundClip> soundClips;
	private AdView adView;
	private MediaPlayer mediaPlayer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_soundboard);
		loadSettings();
		buildClipButtons();

		// Boot up the media player
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
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
		// Set the header color for bleed through
		ImageView header = (ImageView) findViewById(R.id.header);
		header.setBackgroundColor(Color.parseColor(headerColor));

		// Select a random background
		Random random = new Random((new Date()).getTime());
		int index = random.nextInt(backgrounds.size());
		ImageView background = (ImageView) findViewById(R.id.background);
		try {
			background.setImageDrawable(new BitmapDrawable(getResources(), getAssets().open(
					backgrounds.get(index))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buildClipButtons() {
		LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.buttonContainer);
		for (SoundClip clip : soundClips) {
			// Create the button
			Button button = new Button(this);
			button.setBackgroundResource(R.drawable.clip_button);
			button.setText(clip.getTitle());
			button.setSingleLine(true);
			button.setEllipsize(TextUtils.TruncateAt.END);
			button.setTag(clip);
			button.setOnClickListener(this);

			// Spacing
			// button.lay
			// button.setPadding(32, 32, 32, 320);

			// Font
			Typeface tf = Typeface.createFromAsset(getAssets(), "arial_rounded_bold.ttf");
			button.setTypeface(tf);
			button.setTextColor(Color.WHITE);
			button.setTextSize(20);
			button.setShadowLayer(1.0f, 1.0f, 1.0f, Color.parseColor("#99000000"));

			buttonContainer.addView(button);
		}
	}

	@Override
	public void onClick(View view) {
		SoundClip clip = (SoundClip) view.getTag();
		mediaPlayer.stop();
		try {
			clip.play(mediaPlayer);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parseJsonSettingsFile(JSONObject jo) {
		// Header color
		headerColor = jo.optString("header_color", "#000000");

		// Parse backgrounds
		backgrounds = new ArrayList<String>();
		JSONArray objs = jo.optJSONArray("backgrounds");
		for (int i = 0; i < objs.length(); i++)
			backgrounds.add(objs.optJSONObject(i).optString("filename"));

		// Parse sound clips
		soundClips = new ArrayList<SoundClip>();
		objs = jo.optJSONArray("clips");
		if (objs != null) {
			for (int i = 0; i < objs.length(); i++) {
				try {
					SoundClip clip = SoundClip.BuildSoundClip(objs.optJSONObject(i), this);
					if (clip != null)
						soundClips.add(clip);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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

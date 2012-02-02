package com.grilledcheeseapps.soundboard.appname;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdView;


public class SoundboardActivity extends Activity implements AdListener, OnClickListener,
		OnCompletionListener {
	public static final String TAG = "SoundboardActivity";

	public static final String PREFS = "prefs";
	public static final String HELP_SHOWN = "help_shown";

	private String headerColor;
	private ArrayList<String> backgrounds;
	private ArrayList<SoundClip> soundClips;
	private AdView adView;
	private MediaPlayer mediaPlayer;
	private Button currentButton;
	private Typeface tf;
	private Button lastButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_soundboard);
		loadSettings();
		buildClipButtons();

		// Volume controls clip playback always
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

		// Boot up the media player
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnCompletionListener(this);

		tf = Typeface.createFromAsset(getAssets(), "arial_rounded_bold.ttf");

		SharedPreferences settings = getSharedPreferences(PREFS, 0);
		if (!settings.getBoolean(HELP_SHOWN, false)) {
			showHelp();
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(HELP_SHOWN, true);
			editor.commit();
		}
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
					"backgrounds/" + backgrounds.get(index))));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void buildClipButtons() {
		LinearLayout buttonContainer = (LinearLayout) findViewById(R.id.buttonContainer);
		for (SoundClip clip : soundClips) {
			// Create the button
			Button button = new Button(this);
			button = (Button) getLayoutInflater().inflate(R.layout.button, buttonContainer, false);
			button.setText(clip.getTitle());
			button.setTag(clip);
			button.setOnClickListener(this);
			button.setTypeface(tf);
			button.setOnCreateContextMenuListener(this);
			buttonContainer.addView(button);
		}
	}

	@Override
	public void onClick(View view) {
		// Stop the clip if they tapped the same button again
		if (currentButton == (Button) view) {
			currentButton.setTextColor(Color.WHITE);
			currentButton = null;
			mediaPlayer.stop();
			return;
		}

		// Just in case we didn't let the other clip finish
		if (currentButton != null)
			currentButton.setTextColor(Color.WHITE);
		currentButton = (Button) view;
		// Highlight currently playing clip
		currentButton.setTextColor(Color.YELLOW);

		mediaPlayer.stop();
		mediaPlayer.reset();
		SoundClip clip = (SoundClip) view.getTag();
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

	private static final int CONTEXT_SET_RINGTONE = 100;
	private static final int CONTEXT_ADD_RINGTONE = 200;
	private static final int CONTEXT_ADD_NOTIFICATIONS = 300;
	private static final int CONTEXT_ADD_ALARMS = 500;
	private static final int CONTEXT_SHARE = 600;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		lastButton = (Button) view;

		menu.add(0, CONTEXT_SET_RINGTONE, 0, "Set as Ringtone");
		menu.add(0, CONTEXT_ADD_RINGTONE, 1, "Add to Ringtones");
		menu.add(0, CONTEXT_ADD_NOTIFICATIONS, 2, "Add to Notification Ringtones");
		menu.add(0, CONTEXT_ADD_ALARMS, 4, "Add to Alarms");
		menu.add(0, CONTEXT_SHARE, 5, "Share");

	}

	private boolean prepForIntent(SoundClip clip) {
		boolean result = clip.copyForShare(this.getApplicationContext());
		Log.d(TAG, "Sharing: " + clip.copy.getAbsolutePath());
		if (!result) {
			Toast.makeText(this, "Could not share clip, make sure your sdcard is ready",
					Toast.LENGTH_LONG).show();
		}
		return result;
	}

	private Uri addToRingtones(SoundClip clip, boolean isRingtone, boolean isNotification,
			boolean isAlarm) {
		boolean result = false;
		if (isRingtone)
			result = clip.copyForRingtone(this.getApplicationContext());
		else if (isNotification)
			result = clip.copyForNotification(this.getApplicationContext());
		else if (isAlarm)
			result = clip.copyForAlarm(this.getApplicationContext());
		if (!result) {
			Toast.makeText(this, "Could not set clip, make sure your sdcard is ready",
					Toast.LENGTH_LONG).show();
			return null;
		}

		ContentValues values = new ContentValues();
		Log.d(TAG, "content value: " + clip.copy.getAbsolutePath());
		values.put(MediaStore.MediaColumns.DATA, clip.copy.getAbsolutePath());
		values.put(MediaStore.MediaColumns.TITLE, clip.getTitle());
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
		values.put(MediaStore.MediaColumns.SIZE, clip.copy.length());
		values.put(MediaStore.Audio.Media.IS_RINGTONE, isRingtone);
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, isNotification);
		values.put(MediaStore.Audio.Media.IS_ALARM, isAlarm);
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);

		// Insert into database
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(clip.copy.getAbsolutePath());
		return getContentResolver().insert(uri, values);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Uri ringtone = null;
		SoundClip clip = (SoundClip) lastButton.getTag();
		switch (item.getItemId()) {
		case CONTEXT_SET_RINGTONE:
			// Add to ringtones storage / database
			ringtone = addToRingtones(clip, true, false, false);

			// Set as default ringtone
			if (ringtone != null) {
				RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE,
						ringtone);
				Toast.makeText(this, "\"" + clip.getTitle() + "\" set as the default ringtone",
						Toast.LENGTH_LONG).show();
			}
			break;
		case CONTEXT_ADD_RINGTONE:
			ringtone = addToRingtones(clip, true, false, false);
			if (ringtone != null)
				Toast.makeText(this, "\"" + clip.getTitle() + "\" added to ringtones",
						Toast.LENGTH_LONG).show();
			break;
		case CONTEXT_ADD_NOTIFICATIONS:
			ringtone = addToRingtones(clip, false, true, false);
			if (ringtone != null)
				Toast.makeText(this, "\"" + clip.getTitle() + "\" added to notification ringtones",
						Toast.LENGTH_LONG).show();
			break;
		case CONTEXT_ADD_ALARMS:
			ringtone = addToRingtones(clip, false, false, true);
			if (ringtone != null)
				Toast.makeText(this, "\"" + clip.getTitle() + "\" added to alarms",
						Toast.LENGTH_LONG).show();
			break;
		case CONTEXT_SHARE:
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("audio/*");
			if (prepForIntent(clip)) {
				share.putExtra(Intent.EXTRA_STREAM, Uri.parse(clip.copy.getAbsolutePath()));
				startActivity(Intent.createChooser(share, "Share Clip"));
			}
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// Clear highlighted button
		if (currentButton != null) {
			currentButton.setTextColor(Color.WHITE);
			currentButton = null;
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

	private void showHelp() {
		String message = "Tap a button to play a sound clip.\n\nDrag to scroll for more sound clips.\n\nLong press a button for more options.\n\nEnjoy!";

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Soundboard Help");
		builder.setMessage(message);
		builder.setPositiveButton("Ok", null);
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.help:
			showHelp();
			break;
		case R.id.other:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://search?q=pub:Grilled Cheese"));
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onReceiveAd(Ad arg0) {
		adView.setVisibility(View.VISIBLE);

		// Schedule next ad call
		int delay = 20000;
		new Timer().schedule(new TimerTask() {
			public void run() {
				SoundboardActivity.this.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adView.loadAd(new AdRequest());
					}
				});
			}
		}, delay);
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

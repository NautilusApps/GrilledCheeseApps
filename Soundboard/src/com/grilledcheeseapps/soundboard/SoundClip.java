package com.grilledcheeseapps.soundboard;

import java.io.IOException;
import org.json.JSONObject;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;


public class SoundClip {

	private String title;
	private String resource;

	public SoundClip(JSONObject jo) {
		if (jo != null) {
			title = jo.optString("title");
			resource = jo.optString("resource");
		}
	}

	public String getTitle() {
		return title;
	}

	public void play(Context context) {
		try {
			AssetFileDescriptor fd = context.getAssets().openFd(resource);
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

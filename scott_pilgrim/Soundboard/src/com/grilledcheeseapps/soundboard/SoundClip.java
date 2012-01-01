package com.grilledcheeseapps.soundboard;

import java.io.IOException;
import org.json.JSONObject;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;


public class SoundClip {

	private String title;
	private String resource;
	AssetFileDescriptor resourceFile;

	public static SoundClip BuildSoundClip(JSONObject jo, Context context) throws IOException {
		SoundClip soundClip = null;
		if (jo != null) {
			soundClip = new SoundClip();
			soundClip.title = jo.optString("title");
			soundClip.resource = jo.optString("resource");
			soundClip.resourceFile = context.getAssets().openFd("clips/" + soundClip.resource);
		}
		return soundClip;
	}

	public String getTitle() {
		return title;
	}

	public void play(MediaPlayer player) throws IllegalArgumentException, IllegalStateException, IOException {
		player.setDataSource(resourceFile.getFileDescriptor(), resourceFile.getStartOffset(),
				resourceFile.getLength());
		player.prepare();
		player.start();
	}

}

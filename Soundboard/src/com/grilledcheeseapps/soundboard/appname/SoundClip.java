package com.grilledcheeseapps.soundboard.appname;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.json.JSONObject;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;


public class SoundClip {
	public static final String TAG = "SoundClip";

	private String title;
	private String resource;
	AssetFileDescriptor resourceFile;
	public String copyPath;

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

	public boolean copyForShare(Context context) {
		// Clear out all files in this directory
		File cache = context.getExternalCacheDir();
		Log.d(TAG, "Burning cache: " + cache.getAbsolutePath());
		if (cache != null) {
			for (File file : cache.listFiles()) {
				boolean res = file.delete();
				if (res)
					Log.d(TAG, "File burned: " + file.getAbsolutePath());
				else
					Log.e(TAG, "Could not burn file: " + file.getAbsolutePath());
			}
		}

		// New file location
		File file = new File(cache.getAbsolutePath() + "/" + resource);
		Log.d(TAG, "Creating file: " + file.getAbsolutePath());
		try {
			boolean res = file.createNewFile();
			if (res) {
				Log.d(TAG, "Copying clip...");
				copy(resourceFile.getFileDescriptor(), file);
				Log.d(TAG, "Clip copied: " + file.length());
				copyPath = file.getAbsolutePath();
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	void copy(FileDescriptor src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public void play(MediaPlayer player) throws IllegalArgumentException, IllegalStateException,
			IOException {
		player.setDataSource(resourceFile.getFileDescriptor(), resourceFile.getStartOffset(),
				resourceFile.getLength());
		player.prepare();
		player.start();
	}

}

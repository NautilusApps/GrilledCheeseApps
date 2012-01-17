package com.grilledcheeseapps.soundboard.appname;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.json.JSONObject;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;


public class SoundClip {
	public static final String TAG = "SoundClip";

	private String title;
	private String resource;
	AssetFileDescriptor resourceFile;
	public File copy;

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

	private boolean copyForEverything(File desDir) {
		if (desDir != null && !desDir.exists()) {
			desDir.mkdirs();
		}

		if (desDir != null && desDir.exists()) {
			// New file location
			File file = new File(desDir.getAbsolutePath() + "/" + title + ".ogg");
			Log.d(TAG, "Creating file: " + file.getAbsolutePath());
			try {
				boolean res = false;
				if (!res)
					res = file.createNewFile();
				if (res) {
					Log.d(TAG, "Copying clip...");
					copy(resourceFile, file);
					Log.d(TAG, "Clip copied: " + file.length());
					if (file.length() <= 0)
						return false;
					copy = file;
					return true;
				} else if (file.exists()) {
					Log.d(TAG, "File already exists");
					copy = file;
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	public boolean copyForNotification(Context context) {
		try {
			resourceFile = context.getAssets().openFd("clips/" + resource);
		} catch (IOException e) {
			e.printStackTrace();
		} // refresh
		File storage = Environment.getExternalStorageDirectory();
		File desDir = new File(storage.getAbsolutePath() + "/media/notifications");
		return copyForEverything(desDir);
	}

	public boolean copyForAlarm(Context context) {
		try {
			resourceFile = context.getAssets().openFd("clips/" + resource);
		} catch (IOException e) {
			e.printStackTrace();
		} // refresh
		File storage = Environment.getExternalStorageDirectory();
		File desDir = new File(storage.getAbsolutePath() + "/media/alarms");
		return copyForEverything(desDir);
	}

	public boolean copyForRingtone(Context context) {
		try {
			resourceFile = context.getAssets().openFd("clips/" + resource);
		} catch (IOException e) {
			e.printStackTrace();
		} // refresh
		File storage = Environment.getExternalStorageDirectory();
		File desDir = new File(storage.getAbsolutePath() + "/media/ringtones");
		return copyForEverything(desDir);
	}

	public boolean copyForShare(Context context) {
		try {
			resourceFile = context.getAssets().openFd("clips/" + resource);
		} catch (IOException e) {
			e.printStackTrace();
		} // refresh
		
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
		File file = new File(cache.getAbsolutePath() + "/" + title + ".ogg");
		Log.d(TAG, "Creating file: " + file.getAbsolutePath());
		try {
			boolean res = file.createNewFile();
			if (res) {
				Log.d(TAG, "Copying clip...");
				copy(resourceFile, file);
				Log.d(TAG, "Clip copied: " + file.length());
				if (file.length() <= 0)
					return false;
				copy = file;
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	void copy(AssetFileDescriptor src, File dst) throws IOException {
		byte[] readData = new byte[1024];
		FileInputStream fis = src.createInputStream();
		FileOutputStream fos = new FileOutputStream(dst);
		int i = fis.read(readData);
		while (i != -1) {
			fos.write(readData, 0, i);
			i = fis.read(readData);
		}
		fos.close();
	}

	public void play(MediaPlayer player) throws IllegalArgumentException, IllegalStateException,
			IOException {
		player.setDataSource(resourceFile.getFileDescriptor(), resourceFile.getStartOffset(),
				resourceFile.getLength());
		player.prepare();
		player.start();
	}

}

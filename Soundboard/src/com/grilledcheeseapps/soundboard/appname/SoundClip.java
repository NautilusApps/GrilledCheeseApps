package com.grilledcheeseapps.soundboard.appname;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.json.JSONObject;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;


public class SoundClip {

	private String title;
	private String resource;
	AssetFileDescriptor resourceFile;
	public String uri;

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
	
	public String copyForShare(Context context) {
	    AssetManager am = context.getAssets();
	    String filePath = context.getExternalCacheDir() + "/" + resource;
	    File file = new File(filePath);
	    try {
			OutputStream os = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//	    DBFile.createNewFile();
//	    byte []b = new byte[1024];
//	    int i, r;
//	    String []Files = am.list("");
//	    Arrays.sort(Files);
//	    for(i=1;i<10;i++)
//	    {
//	           String fn = String.format("%d.db", i);
//	        if(Arrays.binarySearch(Files, fn) < 0)
//	               break;
//	        InputStream is = am.open(fn);
//	        while((r = is.read(b)) != -1)
//	            os.write(b, 0, r);
//	        is.close();
//	    }
//	    os.close();
	    
	    return filePath;
	}

	public void play(MediaPlayer player) throws IllegalArgumentException, IllegalStateException, IOException {
		player.setDataSource(resourceFile.getFileDescriptor(), resourceFile.getStartOffset(),
				resourceFile.getLength());
		player.prepare();
		player.start();
	}

}

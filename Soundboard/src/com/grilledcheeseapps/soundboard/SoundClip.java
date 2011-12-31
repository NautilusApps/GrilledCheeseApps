package com.grilledcheeseapps.soundboard;

import org.json.JSONObject;
import android.util.Log;

public class SoundClip {
	
	public SoundClip(JSONObject jo) {
		
		if (jo != null) {
			Log.d("SOundClip", jo.toString());
		}
		
	}

}

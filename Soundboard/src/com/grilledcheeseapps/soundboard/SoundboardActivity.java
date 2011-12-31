package com.grilledcheeseapps.soundboard;

import android.app.Activity;
import android.os.Bundle;
import com.grilledcheeseapps.soundboard.R;

public class SoundboardActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_soundboard);
    }
    
}
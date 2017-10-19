package com.tdefence;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

//todo Shooting laser
//todo Animation shooting turret
//todo animation blowing enemy
//todo main menu
//todo building turret
//todo money
//todo graphics
//todo enemies killed/passed away
//todo health coloring

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new PlayScreen(), config);
	}
}

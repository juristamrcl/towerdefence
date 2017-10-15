package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;


public class TDefence extends ApplicationAdapter {

	private Map map;
	@Override
	public void create () {
		map = new Map("1");
		map.create();
	}

	@Override
	public void render () {
		map.render();
	}
	
	@Override
	public void dispose () {
		map.dispose();
	}
}

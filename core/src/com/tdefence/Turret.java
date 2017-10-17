package com.tdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Marcel Jurišta on 15.10.2017.
 */

public class Turret {
    private Vector2 position;
    private int id;

    private float tileSize;
    private Texture image;


    public Turret (Vector2 position, int id, float tileSize){
        this.position = position;
        this.id = id;
        this.tileSize = tileSize;
        this.image = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("sand_tile_battle.png")), tileSize));
    }

    public Vector2 getPosition() {
        return position;
    }

    public Texture getImage() {
        return image;
    }
}

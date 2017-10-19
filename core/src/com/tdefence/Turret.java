package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Marcel Jurišta on 15.10.2017.
 */

public class Turret extends ApplicationAdapter {

    private SpriteBatch batch;
    private Vector2 position;
    private Texture image;

    public Sprite getShootingRangeBounds() {
        return shootingRangeBounds;
    }

    private Sprite shootingRangeBounds;

    private int id;
    private int width;
    private int height;

    private float power;
    private float tileSize;
    float shootingRange;


    public Turret (Vector2 position, int id, float tileSize, int width, int height, float shootingRange){
        this.position = position;
        this.id = id;
        this.tileSize = tileSize;
        this.width = width;
        this.height = height;
        this.shootingRange = shootingRange * 2;
        this.image = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("tower_medium.png")), tileSize));
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.shootingRangeBounds = new Sprite();
        this.shootingRangeBounds.setBounds(this.position.x + (this.tileSize / 2) - (this.shootingRange / 2),height - (this.position.y + (- this.tileSize / 2) + (this.shootingRange / 2)), this.shootingRange, this.shootingRange);
        Gdx.app.log("INFO", "Turret Bounds:" + (this.position.x + (this.tileSize / 2) - (this.shootingRange / 2))+ " " + (height - (this.position.y + (- this.tileSize / 2) + (this.shootingRange / 2))));
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(image, this.position.x, height - this.position.y);
        batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        this.image.dispose();
        this.batch.dispose();
    }
}

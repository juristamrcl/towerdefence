package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

/**
 * Created by Marcel Jurišta on 15.10.2017.
 */

public class Store extends ApplicationAdapter {

    public Vector2[] getTurretPositions() {
        return turretPositions;
    }

    public Texture[] getStoreItems() {
        return storeItems;
    }

    private Vector2 [] turretPositions;
    private Texture [] storeItems;
    private SpriteBatch batch;
    private Sprite testSprite;

    private int tileSize;
    private int width;
    private int height;


    private float storeHeight;
    private float shopItemSpaceBetween;

    public Store(float storeHeight, int tileSize, int width, int height){
        this.storeHeight = storeHeight;
        this.tileSize = tileSize;
        this.width = width;
        this.height = height;
    }

    @Override
    public void create() {

        batch = new SpriteBatch();

        float offsetYStore;
        turretPositions = new Vector2[5];
        storeItems = new Texture[4];
        if (storeHeight > 1.7 * tileSize){
            offsetYStore = (storeHeight / 2) - (tileSize / 2);
        }
        else {
            offsetYStore = (storeHeight - tileSize) / 2;
        }

        shopItemSpaceBetween = (2 * tileSize) / 5;

        for (int i = 0; i < 4; i++){
            storeItems[i] = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("sand_tile_battle.png")), tileSize));
            turretPositions[i] = new Vector2((7 * tileSize) + (i * tileSize) + ((i+1) * shopItemSpaceBetween), height - tileSize - offsetYStore);
        }
        testSprite = new Sprite(storeItems[0]);
        testSprite.setBounds(turretPositions[0].x, turretPositions[0].y, tileSize, tileSize);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

        batch.begin();
        drawStoreItems();
        batch.draw(testSprite, turretPositions[0].x, turretPositions[0].y);
        batch.end();
    }

    @Override
    public void dispose() {
        for (Texture text : storeItems){
            text.dispose();
        }
        batch.dispose();
    }

    public void drawStoreItems (){
        for (int i = 1; i < 4; i++){
            batch.draw(storeItems[i], turretPositions[i].x, turretPositions[i].y);
        }
    }

}

package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;

import java.awt.Font;
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
    private Texture moneyItem;
    private Texture menuButton;
    private Texture pauseButton;
    private Texture playButton;
    private SpriteBatch batch;
    private BitmapFont cashFont;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    private int tileSize;
    private int width;
    private int height;
    private int cash;

    private float storeHeight;
    private float shopItemSpaceBetween;

    private boolean isPaused = true;

    public Store(float storeHeight, int tileSize, int width, int height, int cash){
        this.storeHeight = storeHeight;
        this.tileSize = tileSize;
        this.width = width;
        this.height = height;
        this.cash = cash;
    }

    @Override
    public void create() {

        this.batch = new SpriteBatch();

        float offsetYStore;

        this.turretPositions = new Vector2[4];
        this.storeItems = new Texture[4];
        if (this.storeHeight > 1.7 * this.tileSize){
            offsetYStore = (this.storeHeight / 2) - (this.tileSize / 2);
        }
        else {
            offsetYStore = (this.storeHeight - this.tileSize) / 2;
        }

        this.shopItemSpaceBetween = (2 * this.tileSize) / 5;

        for (int i = 0; i < 4; i++){
            this.storeItems[i] = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("tower_medium.png")), tileSize));
            this.turretPositions[i] = new Vector2((7 * this.tileSize) + (i * this.tileSize) + ((i+1) * this.shopItemSpaceBetween), this.height - this.tileSize - offsetYStore);
        }

        this.moneyItem = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("money.png")), this.storeHeight / 3));
        this.menuButton = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("menu.png")), this.storeHeight / 3));
        this.playButton = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("play.png")), this.storeHeight / 3));
        this.pauseButton = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("pause.png")), this.storeHeight / 3));


        this.generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Quicksand-Bold.ttf"));
        this.parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        this.parameter.size = (int)(this.storeHeight / 3);
        this.parameter.color = Color.DARK_GRAY;
        this.cashFont = generator.generateFont(parameter);

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

        this.batch.begin();
        this.drawStoreItems();
        batch.draw(this.moneyItem, 40 + (this.storeHeight / 2), this.height - (this.storeHeight) + (this.storeHeight / 3));
        this.cashFont.draw(this.batch, this.cash + "", 100 + (int)this.storeHeight / 2, this.height - (this.storeHeight / 5 * 2));

        batch.draw(this.menuButton, this.width - (40 + (this.storeHeight / 2)) - this.storeHeight / 3, this.height - (this.storeHeight) + (this.storeHeight / 3));
        if (isPaused){
            batch.draw(this.playButton, this.width - (140 + (this.storeHeight / 2)), this.height - (this.storeHeight) + (this.storeHeight / 3));
        }
        else{
            batch.draw(this.pauseButton, this.width - (140 + (this.storeHeight / 2)), this.height - (this.storeHeight) + (this.storeHeight / 3));
        }
        this.batch.end();
    }

    @Override
    public void dispose() {
        for (Texture texture : this.storeItems){
            texture.dispose();
        }
        this.batch.dispose();
        this.cashFont.dispose();
        this.generator.dispose();
        this.menuButton.dispose();
    }

    public void drawStoreItems (){
        for (int i = 0; i < 4; i++){
            this.batch.draw(this.storeItems[i], this.turretPositions[i].x, this.turretPositions[i].y);
        }
    }

}

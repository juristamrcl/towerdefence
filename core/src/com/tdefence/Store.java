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

    public Texture getMenuButton() {
        return menuButton;
    }

    public Texture getPauseButton() {
        return pauseButton;
    }

    public Texture getPlayButton() {
        return playButton;
    }

    private Texture playButton;
    private SpriteBatch batch;
    private BitmapFont cashFont;
    private BitmapFont turretPriceFontGreen;
    private BitmapFont turretPriceFontRed;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;

    private int tileSize;
    private int width;
    private int height;
    private int cash;
    private int turretPrices[];

    private float storeHeight;
    private float shopItemSpaceBetween;
    private float offsetYStore;

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    private boolean isPaused = false;

    public Store(float storeHeight, int tileSize, int width, int height, int[] turretPrices, int cash){
        this.storeHeight = storeHeight;
        this.tileSize = tileSize;
        this.width = width;
        this.height = height;
        this.cash = cash;
        this.turretPrices = turretPrices;
    }

    public int getCash() {
        return cash;
    }

    public void addCash(int cash) {
        this.cash += cash;
    }
    public void removeCash(int cash) {
        this.cash -= cash;
    }

    @Override
    public void create() {

        batch = new SpriteBatch();

        turretPositions = new Vector2[4];
        storeItems = new Texture[4];

        if (storeHeight > 1.7 * tileSize){
            offsetYStore = (storeHeight / 2) - (tileSize / 2);
        }
        else {
            offsetYStore = (storeHeight - tileSize) / 2;
        }

        shopItemSpaceBetween = (2 * tileSize) / 5;

        for (int i = 0; i < 4; i++){
            storeItems[i] = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("tower" + i + ".png")), tileSize));
            turretPositions[i] = new Vector2((7 * tileSize) + (i * tileSize) + ((i+1) * shopItemSpaceBetween), height - tileSize - offsetYStore + 5);
        }

        moneyItem = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("money.png")), storeHeight / 3));
        menuButton = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("menu.png")), storeHeight / 3));
        playButton = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("play.png")), storeHeight / 3));
        pauseButton = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("pause.png")), storeHeight / 3));


        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Quicksand-Bold.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(storeHeight / 3);
        parameter.color = Color.DARK_GRAY;
        cashFont = generator.generateFont(parameter);
        parameter.size = 20;
        parameter.color = Color.FOREST;
        turretPriceFontGreen = generator.generateFont(parameter);
        parameter.color = Color.RED;
        turretPriceFontRed = generator.generateFont(parameter);

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

        batch.begin();
        drawStoreItems();
        batch.draw(moneyItem, 40 + (storeHeight / 2), height - (storeHeight) + (storeHeight / 3));
        cashFont.draw(batch, cash + "", 90 + (int)storeHeight / 2, height - (storeHeight / 5 * 2));
        for (int i = 0; i < storeItems.length; i++){
            if (this.cash >= turretPrices[i]){
                turretPriceFontGreen.draw(batch, "$" + turretPrices[i],(tileSize / 2) + (7 * tileSize) + (i * tileSize) + ((i+1) * shopItemSpaceBetween), height - tileSize - offsetYStore / 5 * 4);
            }
            else{
                turretPriceFontRed.draw(batch, "$" + turretPrices[i],(tileSize / 2) + (7 * tileSize) + (i * tileSize) + ((i+1) * shopItemSpaceBetween), height - tileSize - offsetYStore / 5 * 4);
            }

        }

        batch.draw(menuButton, width - (40 + (storeHeight / 2)) - storeHeight / 3, height - (storeHeight) + (storeHeight / 3));
        if (isPaused){
            batch.draw(playButton, width - (140 + (storeHeight / 2)), height - (storeHeight) + (storeHeight / 3));
        }
        else{
            batch.draw(pauseButton, width - (140 + (storeHeight / 2)), height - (storeHeight) + (storeHeight / 3));
        }
        batch.end();
    }

    @Override
    public void dispose() {
        for (Texture texture : storeItems){
            texture.dispose();
        }
        batch.dispose();
        cashFont.dispose();
        turretPriceFontGreen.dispose();
        turretPriceFontRed.dispose();
        generator.dispose();
        menuButton.dispose();
    }

    public void drawStoreItems (){
        for (int i = 0; i < storeItems.length; i++){
            batch.draw(storeItems[i], turretPositions[i].x, turretPositions[i].y);
        }
    }

}

package com.tdefence;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tdefence.Logic.MapReader;

/**
 * Created by Marcel Jurišta on 14.10.2017.
 */

public class Map extends ApplicationAdapter {

    public static final int LEVELS_COUNT = 5;

    private int width;
    private int height;
    private int extraTiles;
    private int [][][] tilesPositions;

    private SpriteBatch batch;
    private Texture imgGroundBlurred;
    private Texture imgBattle;

    private float tileSize;
    private float storeHeight;
    private float originalStoreHeight;

    public int getLevel() {
        return Integer.parseInt(level);
    }

    private String level;

    public Map (String level, int width, int height, float tileSize, float storeHeight, float originalStoreHeight){
        this.level = level;
        this.width = width;
        this.height = height;
        this.tileSize = tileSize;
        this.storeHeight = storeHeight;
        this.originalStoreHeight = originalStoreHeight;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        imgGroundBlurred = imgBattle = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("winter_attack2.png")), (int)tileSize));

        tilesPositions = new MapReader().read("levels/lvl" + level + ".txt");

        extraTiles = (int)Math.ceil(1 + (storeHeight / tileSize));

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        batch.begin();
        setTiles();
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        imgGroundBlurred.dispose();
        imgBattle.dispose();
    }

    public int[][] getTilesPositions(int i) {
        return tilesPositions[i];
    }


    public void setTiles (){

        for (int i = 0; i < 10; i++){
            for (int j = 0; j < 20; j++){
                if (tilesPositions[0][i][j] == 0){
                    //batch.draw(imgGround, width - ((width - tileSize) - (j * tileSize))- tileSize, (height - tileSize) - (i * tileSize) - originalStoreHeight);
                }
                else{
                    batch.draw(imgBattle, width - ((width - tileSize) - (j * tileSize))- tileSize, (height - tileSize) - (i * tileSize) - originalStoreHeight);
                }
            }
        }
        for (int i = 0; i < extraTiles; i++){
            for (int j = 0; j < 20; j++){
                batch.draw(imgGroundBlurred, width - ((width - tileSize) - (j * tileSize)) - tileSize,(height - tileSize) - (i * tileSize) + (extraTiles * tileSize) - storeHeight);
            }
        }
    }
}

package com.tdefence;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.tdefence.Logic.MapReader;

/**
 * Created by Marcel Jurišta on 14.10.2017.
 */

public class Map extends Game {

    public final static int TILES_WIDTH_NUMBER = 20;
    public final static int TILES_HEIGHT_NUMBER = 10;

    private int width;
    private int height;
    private int [][][] tilesPosition;

    private SpriteBatch batch;
    private Texture imgGround;
    private Texture imgGroundBlurred;
    private Texture imgBattle;
    private Texture storeOverlay;
    private Pixmap pixmap;

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private float tileSize;
    private float ratio;
    private float storeHeight;

    private String level;

    public Map (String level){
        this.level = level;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        width = Gdx.app.getGraphics().getWidth();
        height = Gdx.app.getGraphics().getHeight();
        ratio = width / height;
        tileSize = width / TILES_WIDTH_NUMBER;
        storeHeight = height - (TILES_HEIGHT_NUMBER * tileSize);


        imgGround = new Texture(convertPixmaps(new Pixmap(Gdx.files.internal("sand_tile_ground.png"))));
        imgBattle = new Texture(convertPixmaps(new Pixmap(Gdx.files.internal("sand_tile_battle.png"))));
        imgGroundBlurred = new Texture(convertPixmaps(new Pixmap(Gdx.files.internal("sand_tile_ground_blurred.png"))));

        tilesPosition = new MapReader().read("levels/lvl" + level + ".txt");
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        setTiles();
        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
        imgGround.dispose();
        imgBattle.dispose();
        pixmap.dispose();
    }

    public void setTiles (){

        int extraTiles = Math.round(storeHeight / tileSize);

        for (int i = 0; i < 10; i++){
            for (int j = 0; j < 20; j++){
                if (tilesPosition[0][i][j] == 0){
                    batch.draw(imgGround, (width - tileSize) - (j * tileSize),(height - tileSize) - (i * tileSize) - storeHeight);
                }
                else{
                    batch.draw(imgBattle, (width - tileSize) - (j * tileSize),(height - tileSize) - (i * tileSize) - storeHeight);
                }
            }
        }
        for (int i = 0; i < extraTiles; i++){
            for (int j = 0; j < 20; j++){
                batch.draw(imgGroundBlurred, (width - tileSize) - (j * tileSize),(height - tileSize) - (i * tileSize) + (extraTiles * tileSize) - storeHeight);
            }
        }
    }

    public Pixmap convertPixmaps(Pixmap pixmapOld){
        Pixmap pixmap = new Pixmap((int)tileSize, (int)tileSize, pixmapOld.getFormat());
        pixmap.drawPixmap(pixmapOld,
                0, 0, pixmapOld.getWidth(), pixmapOld.getHeight(),
                0, 0, pixmap.getWidth(), pixmap.getHeight()
        );

        return pixmap;
    }
}

package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcel Jurišta on 14.10.2017.
 */

public class PlayScreen extends ApplicationAdapter implements ApplicationListener, InputProcessor {

    public final static int TILES_WIDTH_NUMBER = 20;
    public final static int TILES_HEIGHT_NUMBER = 10;
    public final static int MAXIMUM_DAMAGE = 100;

    private int width;
    private int height;
    private int draggableHeight;

    private Map map;
    private Store store;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private Texture[] storeItems;

    private int damage = 25;
    private int onePercentWidth;
    private int grabbedItemId = -1;

    private float tileSize;
    private float storeHeight;
    private float originalStoreHeight;
    private Vector2 tapPosition;

    private boolean itemIsGrabbed = false;
    private boolean isDropped = false;

    Vector2 positions[];

    private List<Turret> turrets;

    @Override
    public void create () {
        width = Gdx.app.getGraphics().getWidth();
        height = Gdx.app.getGraphics().getHeight();
        onePercentWidth = width / 100;
        draggableHeight = (int)((TILES_WIDTH_NUMBER - 1) * tileSize);

        tileSize = width / TILES_WIDTH_NUMBER;
        originalStoreHeight = storeHeight = height - (TILES_HEIGHT_NUMBER * tileSize);
        if (storeHeight < 1.5 * tileSize){
            storeHeight += (tileSize / 2);
        }

        turrets = new ArrayList<Turret>();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        map = new Map("1", this.width, this.height, tileSize, storeHeight, originalStoreHeight);
        store = new Store((int)storeHeight, (int)tileSize, width, height);

        map.create();
        store.create();

        Gdx.input.setInputProcessor(this);

        positions = store.getTurretPositions();
        storeItems = store.getStoreItems();

        tapPosition = new Vector2(0,0);
    }

    @Override
    public void render () {
        map.render();
        store.render();
        drawDebugLine(new Vector2(width - ((damage * onePercentWidth) / 2), height - storeHeight - 4), new Vector2(width - (width - ((damage * onePercentWidth) / 2)), height - storeHeight - 4), 10, Color.BROWN);

        if (itemIsGrabbed){
            batch.begin();
            batch.draw(storeItems[grabbedItemId], tapPosition.x - (tileSize / 2), height - tapPosition.y - (tileSize / 2));
            batch.end();
        }
        for (Turret turret: turrets){
            batch.begin();
            batch.draw(turret.getImage(), turret.getPosition().x, height - turret.getPosition().y);
            batch.end();
        }

    }

    @Override
    public void dispose () {
        map.dispose();
        store.dispose();
        batch.dispose();
    }

    public void drawDebugLine(Vector2 start, Vector2 end, int lineWidth, Color color)
    {
        Gdx.gl.glLineWidth(lineWidth);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.line(start, end);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
    public static Pixmap convertPixmaps(Pixmap pixmapOld, float fTileSize){
        Pixmap pixmap = new Pixmap((int)fTileSize, (int)fTileSize, pixmapOld.getFormat());
        pixmap.drawPixmap(pixmapOld,
                0, 0, pixmapOld.getWidth(), pixmapOld.getHeight(),
                0, 0, pixmap.getWidth(), pixmap.getHeight()
        );

        return pixmap;
    }

    public static Vector2 setTurretPosition(Vector2 actualPosition, float tileSize, int width, int height, float storeHeight, float originalStoreHeight){
        Gdx.app.log("INFO", "Actual position:" + actualPosition.x + " " + actualPosition.y);
        Gdx.app.log("INFO", "DefStore: " + originalStoreHeight + "Store:" + storeHeight + " HEIGHT: " + height + " TILESIZE: " + tileSize);
        for (int i = 0; i < PlayScreen.TILES_HEIGHT_NUMBER; i++){
                Gdx.app.log("INFO", (height - (i*tileSize)) + " <= " +  actualPosition.y + " && " +  (height - ((i+1) * tileSize))+ " >= " + actualPosition.y);
            if (height - ((i+1) * tileSize) <= actualPosition.y && height - (i*tileSize) >= actualPosition.y){
                Gdx.app.log("INFO", "preslo1");
                for (int j = 0; j < PlayScreen.TILES_WIDTH_NUMBER; j++){
                    if (width - (width - (j*tileSize)) <= actualPosition.x && width - (width - (((j+1) * tileSize))) >= actualPosition.x) {
                        Gdx.app.log("INFO", "preslo2");
                        return new Vector2((j * tileSize), height - (i * tileSize));
                    }
                }
            }
        }
        return new Vector2(0,0);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        isDropped = false;
        tapPosition.set(screenX, screenY);
        Gdx.app.log("INFO", "Down" + screenX + " " + screenY);
        for (int i = 0; i < 4; i++){
            if ((screenX > positions[i].x && screenX < positions[i].x + tileSize) && (screenY < height - positions[i].y && screenY < height - positions[i].y + tileSize)){
                itemIsGrabbed = true;
                grabbedItemId = i;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isDropped = true;
        Gdx.app.log("INFO", "Coords" + screenX + " " + screenY);
        if (screenY > draggableHeight && itemIsGrabbed){
            Vector2 vector = new Vector2(screenX, screenY);
            Turret turret = new Turret(PlayScreen.setTurretPosition(vector, tileSize, width, height, storeHeight, originalStoreHeight), grabbedItemId, tileSize);
            turrets.add(turret);

        }
        itemIsGrabbed = false;
        grabbedItemId = -1;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(!isDropped){
            tapPosition.set(screenX, screenY);
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

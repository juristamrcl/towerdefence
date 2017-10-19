package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcel Jurišta on 14.10.2017.
 */

public class PlayScreen extends ApplicationAdapter implements ApplicationListener, InputProcessor {

    public final static int TILES_WIDTH_NUMBER = 20;
    public final static int TILES_HEIGHT_NUMBER = 10;
    public final static int MAXIMUM_DAMAGE = 100;

    private Map map;
    private Store store;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private Vector2 tapPosition;

    private Vector2 [][] centeredPositions;
    private Texture[] storeItems;
    Vector2 positions[];

    private int width;
    private int height;
    private int onePercentWidth;
    private int grabbedItemId = -1;

    private float tileSize;
    private float storeHeight;
    private float originalStoreHeight;
    private float draggableHeight;
    private float damage = 0;
    private float turretRadius[];

    private boolean itemIsGrabbed = false;
    private boolean isDropped = false;

    private List<Turret> turrets;
    private List<Enemy> enemies;

    @Override
    public void create () {
        width = Gdx.app.getGraphics().getWidth();
        height = Gdx.app.getGraphics().getHeight();

        this.centeredPositions = new Vector2[10][20];
        onePercentWidth = width / 100;
        tileSize = width / TILES_WIDTH_NUMBER;
        draggableHeight = height - ((TILES_HEIGHT_NUMBER - 1) * tileSize);

        originalStoreHeight = storeHeight = height - (TILES_HEIGHT_NUMBER * tileSize);
        if (storeHeight < 1.5 * tileSize){
            storeHeight += (tileSize / 2);
        }

        turrets = new ArrayList<Turret>();
        enemies = new ArrayList<Enemy>();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        map = new Map("1", this.width, this.height, tileSize, storeHeight, originalStoreHeight);
        store = new Store((int)storeHeight, (int)tileSize, width, height, 100);

        map.create();
        store.create();

        Gdx.input.setInputProcessor(this);

        positions = store.getTurretPositions();
        storeItems = store.getStoreItems();

        tapPosition = new Vector2(0,0);
        this.setCenteredPositions();

        //to remove
        Enemy enemy = new Enemy(1, this.tileSize, this.width, this.height, 5f, this.centeredPositions, this.getStartEndPosition(true), this.getStartEndPosition(false), this.map.getTilesPositions(0), this.getStartGroundPosition());
        enemy.create();
        this.enemies.add(enemy);

        this.turretRadius = new float[] {this.tileSize * 1.5f, this.tileSize * 2f, this.tileSize * 2f, this.tileSize * 2.5f};
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        map.render();
        store.render();

        drawDebugLine(new Vector2(width - ((damage * onePercentWidth) / 2), height - storeHeight + 1), new Vector2(width - (width - ((damage * onePercentWidth) / 2)), height - storeHeight + 1), 10, Color.FOREST);

        for (Turret turret: turrets){
            turret.render();
        }
        for (Enemy enemy: enemies){
            for (Turret turret: turrets){
                if (turret.getShootingRangeBounds().getBoundingRectangle().overlaps(enemy.getImage().getBoundingRectangle())){
                    enemy.setDamage(0.3f);
                    if (enemy.getHealth() < 0){
                        enemy.setToDestroy(true);
                    }
                }
            }
            if (enemy.destroy()){
                this.damage += enemy.getDamage();
                enemies.remove(enemy);
            }
            enemy.render();
        }

        // showing turret while it is dragging
        if (itemIsGrabbed){
            batch.begin();
            batch.draw(storeItems[grabbedItemId], tapPosition.x - (tileSize / 2), height - tapPosition.y - (tileSize / 2));
            batch.end();
            drawCircle(tapPosition, this.turretRadius[grabbedItemId], 2, Color.FOREST);
        }
    }

    @Override
    public void dispose () {
        map.dispose();

        for (Turret turret: turrets){
            turret.dispose();
        }
        for (Enemy enemy: enemies){
            enemy.dispose();
        }

        store.dispose();
        batch.dispose();
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
        if (screenY > draggableHeight && itemIsGrabbed){
            Vector2 vector = new Vector2(screenX, screenY);
            Turret turret = new Turret(PlayScreen.setTurretPosition(vector, this.tileSize, this.width, this.height, this.storeHeight, this.originalStoreHeight), grabbedItemId, tileSize, this.width, this.height, this.turretRadius[grabbedItemId]);
            turret.create();
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

    public void drawDebugLine(Vector2 start, Vector2 end, int lineWidth, Color color)
    {
        Gdx.gl.glLineWidth(lineWidth);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.line(start, end);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public void drawCircle(Vector2 position, float radius, int lineWidth, Color color)
    {
        Gdx.gl.glLineWidth(lineWidth);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(position.x, height - position.y, radius);
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
        for (int i = 0; i < PlayScreen.TILES_HEIGHT_NUMBER; i++){
            if (height - ((i+1) * tileSize) <= actualPosition.y && height - (i*tileSize) >= actualPosition.y){
                for (int j = 0; j < PlayScreen.TILES_WIDTH_NUMBER; j++){
                    if (width - (width - (j*tileSize)) <= actualPosition.x && width - (width - (((j+1) * tileSize))) >= actualPosition.x) {
                        return new Vector2((j * tileSize), height - (i * tileSize));
                    }
                }
            }
        }
        return new Vector2(0,0);
    }

    public void setCenteredPositions(){
        for (int i = 0; i < PlayScreen.TILES_HEIGHT_NUMBER; i++){
            for (int j = 0; j < PlayScreen.TILES_WIDTH_NUMBER; j++){
                this.centeredPositions[i][j] = new Vector2((j * tileSize), height - (i * tileSize));
            }
        }
    }

    public Vector2 getStartEndPosition(boolean start){
        if (start){
            for (int i = 0; i < map.getTilesPositions(1).length; i++){
                if (map.getTilesPositions(1)[i][0] == 1){
                    return new Vector2(0, PlayScreen.TILES_HEIGHT_NUMBER - (i + 1));
                }
            }
        }
        else {
            for (int i = 0; i < map.getTilesPositions(1).length; i++){
                if (map.getTilesPositions(1)[i][map.getTilesPositions(1)[0].length - 1] == 0){
                    return new Vector2(0, PlayScreen.TILES_HEIGHT_NUMBER - (i + 1));
                }
            }
        }
        return new Vector2(0,0);
    }

    public Vector2 getStartGroundPosition(){
        for (int i = 0; i < map.getTilesPositions(1).length; i++){
            if (map.getTilesPositions(1)[i][0] == 1){
                return new Vector2(0, i);
            }
        }
        return new Vector2(0,0);
    }
}

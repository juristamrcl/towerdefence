package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

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
    private Random rand;

    private Vector2 [][] centeredPositions;
    private Texture[] storeItems;
    private Vector2 positionToDestroy;
    private Vector2 positions[];

    private int width;
    private int height;
    private int onePercentWidth;
    private int grabbedItemId = -1;
    private int turretCosts[];
    private int enemyPrices[];

    private float tileSize;
    private float storeHeight;
    private float originalStoreHeight;
    private float draggableHeight;
    private float damage = 0;
    private float turretRadius[];

    private boolean itemIsGrabbed = false;
    private boolean isDropped = false;
    private boolean isRunning = true;
    private boolean startAnimation = false;

    private List<Turret> turrets;
    private List<Enemy> enemies;

    // animation

    private TextureAtlas textureAtlas;
    private Animation<TextureRegion> animation;
    private float elapsedTime = 0;


    @Override
    public void create () {
        width = Gdx.app.getGraphics().getWidth();
        height = Gdx.app.getGraphics().getHeight();

        positionToDestroy = new Vector2(0,0);
        rand = new Random();
        turrets = new ArrayList<Turret>();
        enemies = new ArrayList<Enemy>();
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        textureAtlas = new TextureAtlas(Gdx.files.internal("charset.atlas"));
        animation = new Animation(1/50f, textureAtlas.getRegions(), Animation.PlayMode.NORMAL);
        centeredPositions = new Vector2[10][20];

        onePercentWidth = width / 100;
        tileSize = width / TILES_WIDTH_NUMBER;
        draggableHeight = height - ((TILES_HEIGHT_NUMBER - 1) * tileSize);

        originalStoreHeight = storeHeight = height - (TILES_HEIGHT_NUMBER * tileSize);
        if (storeHeight < 1.5 * tileSize){
            storeHeight += (tileSize / 2);
        }

        turretRadius = new float[] {tileSize * 1.5f, tileSize * 2f, tileSize * 2f, tileSize * 2.5f};
        turretCosts = new int[] {5, 20, 35, 60};
        enemyPrices = new int[]{4, 7, 9, 15};

        map = new Map("1", width, height, tileSize, storeHeight, originalStoreHeight);
        store = new Store((int)storeHeight, (int)tileSize, width, height, turretCosts, 190);

        map.create();
        store.create();

        Gdx.input.setInputProcessor(this);

        positions = store.getTurretPositions();
        storeItems = store.getStoreItems();

        tapPosition = new Vector2(0,0);
        setCenteredPositions();

        //to remove
        for (int j = 0; j < 4; j++){
            for (int i = 1; i < 3; i++){
                Enemy enemy = new Enemy(1, tileSize, width, height, 5f, i, enemyPrices[0], centeredPositions, getStartEndPosition(true), getStartEndPosition(false), map.getTilesPositions(0), getStartGroundPosition());
                enemy.create();
                enemies.add(enemy);
            }
        }
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        map.render();
        store.render();

        if(!isRunning){
            for (Enemy e: enemies){
                e.setRunning(false);
            }
        }
        else{
            for (Enemy e: enemies){
                e.setRunning(true);
            }
        }

        drawDebugLine(new Vector2(width - ((damage * onePercentWidth) / 2), height - storeHeight + 1), new Vector2(width - (width - ((damage * onePercentWidth) / 2)), height - storeHeight + 1), 10, PlayScreen.healthColoring(100 - damage));


        ListIterator<Enemy> itr = enemies.listIterator();
        while(itr.hasNext()){
            Enemy enemy = itr.next();
            ListIterator<Turret> turretsIt = turrets.listIterator();
            while (turretsIt.hasNext()){
                Turret turret = turretsIt.next();
                if (turret.getShootingRangeBounds().getBoundingRectangle().overlaps(enemy.getImage().getBoundingRectangle())){
                    if (!turret.isShooting()){
                        turret.setShooting(true);
                        turret.setShootingEnemy(enemy);
                        turret.shoot();

                        checkEnemyShoot(enemy);
                    }
                    else if (turret.isShooting() && turret.getShootingEnemy().equals(enemy)){
                        turret.shoot();
                        checkEnemyShoot(enemy);
                    }
                }
                else {
                    if (turret.getShootingEnemy().equals(enemy)){
                        turret.setShooting(false);
                    }
                }
            }

            enemy.render();
        }

        for (Turret turret: turrets){
            turret.render();
        }
        if (isRunning) {
            ListIterator<Enemy> enIter = enemies.listIterator();
            while (enIter.hasNext()){
                Enemy enemy = enIter.next();
                if (enemy.passedAway()){
                    for (Turret tur: turrets){
                        if (tur.getShootingEnemy().equals(enemy)){
                            tur.setShooting(false);
                        }
                    }
                    damage += enemy.getDamage();
                    enIter.remove();
                }
                else if (enemy.destroy()){
                    for (Turret tur: turrets){
                        if (tur.getShootingEnemy().equals(enemy)){
                            tur.setShooting(false);
                        }
                    }
                    positionToDestroy = enemy.getPosition();
                    startAnimation = true;
                    store.addCash(enemy.getPrice());
                    enIter.remove();
                }
            }
        }

        if (animation.isAnimationFinished(elapsedTime)){
            startAnimation = false;
            elapsedTime = 0;
        }
        else if (startAnimation){
            animateExplosion(positionToDestroy);
        }

        // showing turret while it is dragging
        if (itemIsGrabbed){
            batch.begin();
            batch.draw(storeItems[grabbedItemId], tapPosition.x - (tileSize / 2), height - tapPosition.y - (tileSize / 2));
            batch.end();
            drawCircle(tapPosition, turretRadius[grabbedItemId], 2, Color.FOREST);
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
        textureAtlas.dispose();
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
        if (screenX > width - (140 + (storeHeight / 2)) && screenX  <= width - (140 + (storeHeight / 2)) + (storeHeight / 3) && screenY > 0
                && screenY <= storeHeight){

            if (isRunning){
                isRunning = false;
            }
            else{
                isRunning = true;
            }
        }
        for (int i = 0; i < 4; i++){
            if ((screenX > positions[i].x && screenX < positions[i].x + tileSize) && (screenY < height - positions[i].y && screenY < height - positions[i].y + tileSize) && store.getCash() >= turretCosts[i] && isRunning){
                itemIsGrabbed = true;
                grabbedItemId = i;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        isDropped = true;
        if (itemIsGrabbed && screenY > draggableHeight && store.getCash() - turretCosts[grabbedItemId] >= 0 && checkForGroundPosition(new Vector2 (screenX, screenY))){
            Vector2 vector = new Vector2(screenX, screenY);
            Turret turret = new Turret(PlayScreen.setTurretPosition(vector, tileSize, width, height, storeHeight, originalStoreHeight), grabbedItemId, tileSize, width, height, turretRadius[grabbedItemId], turretCosts[grabbedItemId]);
            turret.create();
            turrets.add(turret);

            // minus cost
            store.removeCash(turret.getCost());

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

    public static Color healthColoring (float health){
        if (health <= 100 && health > 66){
            return Color.FOREST;
        }
        else if (health <= 66 && health > 33){
            return Color.ORANGE;
        }
        else {
            return Color.RED;
        }
    }

    public void setCenteredPositions(){
        for (int i = 0; i < PlayScreen.TILES_HEIGHT_NUMBER; i++){
            for (int j = 0; j < PlayScreen.TILES_WIDTH_NUMBER; j++){
                centeredPositions[i][j] = new Vector2((j * tileSize), height - (i * tileSize));
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

    public void checkEnemyShoot(Enemy enemy){
        if (this.isRunning){
            enemy.setDamage(0.3f);
            if (enemy.getHealth() <= 0){
                enemy.setToDestroy(true);
            }
        }
    }

    public boolean checkForGroundPosition(Vector2 pos){
        float tempWidth = 0;
        float tempHeight = 0;
        for (int i = 0; i < PlayScreen.TILES_HEIGHT_NUMBER; i++){
            tempWidth = 0;
            tempHeight += tileSize;
            for (int j = 0; j < PlayScreen.TILES_WIDTH_NUMBER; j++){
                tempWidth += tileSize;
                if ((pos.x < tempWidth && pos.x > tempWidth - tileSize) && (pos.y - originalStoreHeight < tempHeight && pos.y - originalStoreHeight > tempHeight - tileSize)){
                    if (map.getTilesPositions(0)[i][j] == 0){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void animateExplosion(Vector2 enemy){
        batch.begin();
        batch.draw(animation.getKeyFrame(elapsedTime, true), enemy.x - (50 - (tileSize / 2)), height - enemy.y - (50 - (tileSize / 2)));
        elapsedTime += Gdx.graphics.getDeltaTime();
        batch.end();
    }
}

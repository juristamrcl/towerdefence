package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tdefence.Logic.ShapeBuilder;
import com.tdefence.Logic.Spread;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by Marcel Jurišta on 14.10.2017.
 */

public class PlayScreen extends ApplicationAdapter implements InputProcessor, Serializable {

    public final static int TILES_WIDTH_NUMBER = 20;
    public final static int TILES_HEIGHT_NUMBER = 10;
    public final static int MAXIMUM_DAMAGE = 100;
    public static GameState gameState;

    public enum GameState {
        SHOW_MENU,
        SHOW_WIN_FAIL,
        SHOW_GAME,
        PLAY_NEW_LEVEL
    };

    private Map map;
    private Store store;
    private SpriteBatch batch;
    private Vector2 tapPosition;
    private Random rand;

    private Vector2 [][] centeredPositions;
    private Texture [] storeItems;
    private Vector2 positionToDestroy;
    private Vector2 [] positions;
    private Color[] turretShootingLineColors;
    private TextureAtlas textureAtlas;
    private Animation<TextureRegion> animation;
    private Sound sound;
    private Spread[] spreads;
    private Preferences prefs;

    public WinFailScreen getWinFail() {
        return winFail;
    }

    private WinFailScreen winFail;

    private int width;
    private int height;
    private int onePercentWidth;
    private int grabbedItemId = -1;
    private int spawnedEnemies = 0;
    private int enemiesEscaped = 0;
    private int [] turretCosts;
    private int [] enemyPrices;
    private int [] turretShootingLineHeights;
    private int [] enemyHealths;
    private int [] enemiesForLevel;
    private int [] cashForLevel;

    private float tileSize;
    private float storeHeight;
    private float originalStoreHeight;
    private float draggableHeight;
    private float damage = 0;
    private float elapsedTime = 0;
    private float totalTime = 0.0004f;
    private float timeSeconds = 0f;
    private float winFailSeconds = 0f;
    private float winFailTotalTime = 1f;
    private float [] turretRadius;
    private float [] turretPowers;
    private float [] enemyDamages;
    private float [] enemySpeeds;
    private float [] spawnTime;

    private boolean itemIsGrabbed = false;
    private boolean itemIsDropped = false;
    private boolean isRunning = true;
    private boolean startAnimation = false;
    private boolean playSound = true;

    private List<Turret> turrets;
    private List<Enemy> enemies;

    public PlayScreen(float tileSize, int width, int height, float storeHeight, float originalStoreHeight, Map map, boolean playSound){
        this.tileSize = tileSize;
        this.width = width;
        this.height = height;
        this.storeHeight = storeHeight;
        this.originalStoreHeight = originalStoreHeight;
        this.playSound = playSound;
        this.map = map;
    }

    @Override
    public void create () {
        onePercentWidth = width / 100;
        draggableHeight = height - ((TILES_HEIGHT_NUMBER - 1) * tileSize);

        map.create();

        initialize();
        store = new Store((int)storeHeight, (int)tileSize, width, height, turretCosts, cashForLevel[map.getLevel()]);

        store.create();

        Gdx.input.setInputProcessor(this);

        positions = store.getTurretPositions();
        storeItems = store.getStoreItems();

        tapPosition = new Vector2(0,0);
        setCenteredPositions();
        prefs = Gdx.app.getPreferences("tDefence");
    }

    @Override
    public void render () {

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
            timeSeconds += Gdx.graphics.getRawDeltaTime();
            if(timeSeconds > totalTime){
                if (spawnedEnemies < enemiesForLevel[map.getLevel()]) {
                    int enemyID = randomizeEnemyId();
                    Enemy enemy = new Enemy(enemyID, tileSize, width, height, enemyDamages[enemyID], enemyHealths[enemyID], enemySpeeds[enemyID], enemyPrices[enemyID], centeredPositions, getStartEndPosition(true), getStartEndPosition(false), map.getTilesPositions(0), getStartGroundPosition());
                    enemy.create();
                    enemies.add(enemy);
                    totalTime = MathUtils.random(spawnTime[map.getLevel()] - 0.6f, spawnTime[map.getLevel()] + 0.6f);
                    timeSeconds -= totalTime;
                    spawnedEnemies++;
                }
            }
            if(damage >= MAXIMUM_DAMAGE){
                if (winFailSeconds > winFailTotalTime){
                    winFail = new WinFailScreen(width, height, false, enemiesForLevel[map.getLevel()] - enemiesEscaped, enemiesForLevel[map.getLevel()], map.getLevel());
                    winFail.create();
                    gameState = GameState.SHOW_WIN_FAIL;
                    winFailSeconds = 0;
                }
                else {
                    winFailSeconds += Gdx.graphics.getRawDeltaTime();
                }
            }
            if(enemiesForLevel[map.getLevel()] <= spawnedEnemies && enemies.size() == 0 && MAXIMUM_DAMAGE > damage){
                if (winFailSeconds > winFailTotalTime){
                    winFail = new WinFailScreen(width, height, true, enemiesForLevel[map.getLevel()] - enemiesEscaped, enemiesForLevel[map.getLevel()], map.getLevel());
                    winFail.create();
                    gameState = GameState.SHOW_WIN_FAIL;
                    winFailSeconds = 0;
                }
                else {
                    winFailSeconds += Gdx.graphics.getRawDeltaTime();
                }
            }
        }

        ShapeBuilder.drawLine(new Vector2(width - ((damage * onePercentWidth) / 2), height - storeHeight + 1), new Vector2(width - (width - ((damage * onePercentWidth) / 2)), height - storeHeight + 1), 10, PlayScreen.healthColoring(PlayScreen.MAXIMUM_DAMAGE - damage));

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

                        shootEnemy(enemy, turret);
                    }
                    else if (turret.isShooting() && turret.getShootingEnemy().equals(enemy)){
                        turret.shoot();
                        shootEnemy(enemy, turret);
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
                    enemiesEscaped++;
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
                    if (playSound){
                        sound.play(1f);
                    }
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
            ShapeBuilder.drawCircle(tapPosition, turretRadius[grabbedItemId], 2, height, turretShootingLineColors[grabbedItemId]);
        }
    }

    @Override
    public void dispose () {

        for (Turret turret: turrets){
            turret.dispose();
        }
        for (Enemy enemy: enemies){
            enemy.dispose();
        }

        store.dispose();
        batch.dispose();
        sound.dispose();
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
        itemIsDropped = false;
        tapPosition.set(screenX, screenY);
        if (screenX > width - (140 + (storeHeight / 2)) && screenX  <= width - (140 + (storeHeight / 2)) + (storeHeight / 3) && screenY > 0
                && screenY <= storeHeight){

            if (isRunning){
                isRunning = false;
                store.setPaused(true);
            }
            else{
                isRunning = true;
                store.setPaused(false);
            }
        }

        if (screenX > width - (40 + (storeHeight / 2)) - storeHeight / 3 && screenX  <= width - (40 + (storeHeight / 2)) - storeHeight / 3  + (storeHeight / 3) && screenY > 0
                && screenY <= storeHeight){

            gameState = GameState.SHOW_MENU;
            this.dispose();
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
        itemIsDropped = true;
        if (itemIsGrabbed && screenY > draggableHeight && store.getCash() - grabbedItemId != -1 && checkForGroundPosition(new Vector2 (screenX, screenY))){
            Vector2 vector = new Vector2(screenX, screenY);
            Turret turret = new Turret(PlayScreen.setTurretPosition(vector, tileSize, width, height, storeHeight, originalStoreHeight), grabbedItemId, tileSize, width, height, turretRadius[grabbedItemId], turretPowers[grabbedItemId], turretCosts[grabbedItemId], turretShootingLineColors[grabbedItemId], turretShootingLineHeights[grabbedItemId]);
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
        if(!itemIsDropped){
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

    public void initialize(){
        positionToDestroy = new Vector2(0,0);
        rand = new Random();
        turrets = new ArrayList<Turret>();
        enemies = new ArrayList<Enemy>();
        batch = new SpriteBatch();
        textureAtlas = new TextureAtlas(Gdx.files.internal("charset.atlas"));
        animation = new Animation(1/50f, textureAtlas.getRegions(), Animation.PlayMode.NORMAL);
        centeredPositions = new Vector2[10][20];
        sound = Gdx.audio.newSound(Gdx.files.internal("data/explosion.mp3"));

        turretRadius = new float[] {tileSize * 1.5f, tileSize * 2f, tileSize * 2f, tileSize * 2.5f};
        turretCosts = new int[] {5, 20, 35, 60};
        enemyPrices = new int[]{1, 2, 3, 5};
        cashForLevel = new int[]{19, 25, 30, 70, 100};
        turretShootingLineColors = new Color[]{Color.ORANGE, Color.TEAL, Color.RED, Color.PURPLE};
        turretShootingLineHeights = new int[]{2, 2, 2, 2};
        turretPowers = new float[]{0.7f, 0.8f, 0.9f, 1f};
        enemyHealths = new int[]{100, 300, 700, 4000};
        enemyDamages = new float[]{5f, 7f, 9f, 15f};
        enemySpeeds = new float[]{0.000000001f, 0.003f, 0.004f, 0.01f};
        enemiesForLevel = new int[]{40, 50, 55, 70, 100};
        spawnTime = new float[]{3f, 1.7f, 1.65f, 1.6f, 1.6f};
        spreads = new Spread[]{new Spread(0,1), new Spread(0,2), new Spread(1,2), new Spread(1,3), new Spread(2,3)};
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

    public void shootEnemy(Enemy enemy, Turret turret){
        if (this.isRunning){
            enemy.loseHealth(turret.getPower());
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

    public int randomizeEnemyId(){
        if (map.getLevel() < 4){
            return MathUtils.random(spreads[map.getLevel()].getMinimum(), spreads[map.getLevel()].getMaximum());
        }
        else {
            return MathUtils.random(1, 3);
        }
    }
}

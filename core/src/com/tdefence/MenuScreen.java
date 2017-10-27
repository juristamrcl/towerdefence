package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.tdefence.Logic.ShapeBuilder;


/**
 * Created by Marcel Jurišta on 24.10.2017.
 */

public class MenuScreen extends ApplicationAdapter implements InputProcessor {

    public static final int LEVEL_OFFSET = 20;
    public static final int BUTTON_HEIGHT = 50;

    private int width;
    private int height;
    private int continueLevel;

    private float lvlSize;
    private float tileSize;
    private float storeHeight;
    private float originalStoreHeight;

    private SpriteBatch batch;
    private Vector2 [] lvlPositions;
    private Texture[] levels;
    private Texture soundOn;
    private Texture soundOff;
    private BitmapFont nameText;
    private BitmapFont lvlTextButton;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private PlayScreen playScreen;
    private Music music;
    private Map map;
    private Preferences prefs;

    private boolean [] lvlComplete;
    private boolean sound = false;

    private String [] lvlTextButtons;

    @Override
    public void create() {
        prefs = Gdx.app.getPreferences("tDefence");

        PlayScreen.gameState = PlayScreen.GameState.SHOW_MENU;

        getSharedPrefs();

        width = Gdx.app.getGraphics().getWidth();
        height = Gdx.app.getGraphics().getHeight();
        tileSize = width / PlayScreen.TILES_WIDTH_NUMBER;

        originalStoreHeight = storeHeight = height - (PlayScreen.TILES_HEIGHT_NUMBER * tileSize);
        if (storeHeight < 1.5 * tileSize){
            storeHeight += (tileSize / 2);
        }

        lvlSize = 2 * tileSize;

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Quicksand-Bold.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 80;
        parameter.color = Color.DARK_GRAY;
        nameText = generator.generateFont(parameter);

        Gdx.input.setInputProcessor(this);

        parameter.size = 20;
        parameter.color = Color.WHITE;
        lvlTextButton = generator.generateFont(parameter);

        levels = new Texture[5];
        batch = new SpriteBatch();
        lvlPositions = new Vector2[5];
        lvlTextButtons = new String [5];
        music = Gdx.audio.newMusic(Gdx.files.internal("data/ingame.mp3"));
        music.setVolume(0.1f);
        music.setLooping(true);
        if (sound){
            music.play();
        }
        lvlComplete = new boolean[5];

        soundOn = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("musicon.png")), tileSize));
        soundOff = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("musicoff.png")), tileSize));

        for (int i = 0; i < Map.LEVELS_COUNT; i++){
            levels[i] = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("level" + i + ".png")), lvlSize));
            lvlPositions[i] = new Vector2( - (Map.LEVELS_COUNT * LEVEL_OFFSET / 2) + ((width - (levels.length * lvlSize)) / 2) + (i * LEVEL_OFFSET) + (i * lvlSize), height / 2 - (lvlSize / 2));
            lvlComplete[i] = prefs.getBoolean("level" + i, false);
            continueLevel = prefs.getInteger("nextLevel", -1);
            lvlTextButtons[i] = (i == continueLevel) ? "Continue": "Play";
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.97f, 0.94f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (PlayScreen.gameState == PlayScreen.GameState.SHOW_MENU){
            refresh();

            for (int i = 0; i < Map.LEVELS_COUNT; i++){
                ShapeBuilder.drawRect(new Vector2(lvlPositions[i].x, lvlPositions[i].y - (height / 7)), new Vector2(lvlSize, BUTTON_HEIGHT), setButtonColor(lvlComplete[i], i));
                batch.begin();
                batch.draw(levels[i], lvlPositions[i].x, lvlPositions[i].y - LEVEL_OFFSET - 10);
                lvlTextButton.draw(batch,lvlTextButtons[i], (lvlTextButtons[i].equals("Continue")) ? lvlPositions[i].x + (21) : lvlPositions[i].x + (42), lvlPositions[i].y - (height / 7) + 32);
                batch.end();
            }
            batch.begin();
            nameText.draw(batch, "tDefence", width / 2 - 190, height - (height / 5));
            if (sound){
                if (!music.isPlaying()){
                    music.play();
                }
                batch.draw(soundOn, (width / 2) - (tileSize / 2), tileSize);
            }else{
                batch.draw(soundOff, (width / 2) - (tileSize / 2), tileSize);
                music.pause();
            }

            batch.end();

            if(PlayScreen.gameState == PlayScreen.GameState.SHOW_MENU){
                Gdx.input.setInputProcessor(this);
            }
        }
        else if (PlayScreen.gameState == PlayScreen.GameState.SHOW_GAME){
            playScreen.render();
        }

        else if (PlayScreen.gameState == PlayScreen.GameState.SHOW_WIN_FAIL){
            playScreen.getWinFail().render();
        }
        else if (PlayScreen.gameState == PlayScreen.GameState.PLAY_NEW_LEVEL){
            //playScreen.dispose();
            int nextLevel = prefs.getInteger("nextLevel", 0);
            map = new Map(nextLevel + "", width, height, tileSize, storeHeight, originalStoreHeight);
            map.create();
            playScreen = new PlayScreen(tileSize, width, height, storeHeight, originalStoreHeight, map, sound);
            playScreen.create();
            PlayScreen.gameState = PlayScreen.GameState.SHOW_GAME;
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        music.stop();
        music.dispose();
        for (Texture t: levels){
            t.dispose();
        }
        nameText.dispose();
        generator.dispose();
        batch.dispose();
        lvlTextButton.dispose();
        soundOn.dispose();
        soundOff.dispose();
    }

    public Color setButtonColor(boolean state, int level){
        if (state || level == 0){
            return Color.FOREST;
        }
        else {
            return Color.LIGHT_GRAY;
        }
    }
    public boolean play(boolean state, int level){
        if (state || level == 0){
            return true;
        }
        else {
            return false;
        }
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
        for (int i = 0; i < Map.LEVELS_COUNT; i++){
            if (screenX >= lvlPositions[i].x + LEVEL_OFFSET && screenX <= lvlPositions[i].x + lvlSize + LEVEL_OFFSET && screenY <= lvlPositions[i].y + (height / 7) + lvlSize
                    && screenY >= lvlPositions[i].y + (height / 7) + lvlSize - BUTTON_HEIGHT && play(lvlComplete[i], i)){

                map = new Map((i) + "", width, height, tileSize, storeHeight, originalStoreHeight);
                map.create();
                playScreen = new PlayScreen(tileSize, width, height, storeHeight, originalStoreHeight, map, sound);
                playScreen.create();

                PlayScreen.gameState = PlayScreen.GameState.SHOW_GAME;
            }
        }
        if (screenX >= (width / 2) - (tileSize / 2) && screenX <= (width / 2) - (tileSize / 2) + tileSize && screenY <= height - tileSize && screenY >=  height - (tileSize * 2)){
            if (sound){
                prefs.putBoolean("sounds", false);
                sound = false;
            }else {
                sound = true;
                prefs.putBoolean("sounds", true);
            }
            prefs.flush();
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
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

    public void getSharedPrefs(){
        sound = prefs.getBoolean("sounds", false);
    }

    public void refresh(){
        for (int i = 0; i < Map.LEVELS_COUNT; i++){
            lvlComplete[i] = prefs.getBoolean("level" + i, false);
            continueLevel = prefs.getInteger("nextLevel", -1);
            lvlTextButtons[i] = (i == continueLevel) ? "Continue": "Play";
        }
    }
}

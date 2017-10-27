package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.tdefence.Logic.ShapeBuilder;

/**
 * Created by Marcel Jurišta on 24.10.2017.
 */

public class WinFailScreen extends ApplicationAdapter implements InputProcessor{

    public static final int BUTTON_OFFSET = 20;

    private int width;
    private int height;
    private int enemiesKilled;
    private int totalEnemies;
    private int level;

    private SpriteBatch batch;
    private BitmapFont congratulationsText;
    private BitmapFont enemiesKilledText;
    private BitmapFont menuText;
    private BitmapFont continueRetryText;
    private FreeTypeFontGenerator generator;
    private FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    private Preferences prefs;

    boolean win;

    public WinFailScreen(int width, int height, boolean win, int enemiesKilled, int totalEnemies, int level){
        this.width = width;
        this.height = height;
        this.win = win;
        this.level = level;
        this.enemiesKilled = enemiesKilled;
        this.totalEnemies = totalEnemies;
        prefs = Gdx.app.getPreferences("tDefence");
    }


    @Override
    public void create() {
        batch = new SpriteBatch();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Quicksand-Bold.ttf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 50;
        parameter.color = Color.DARK_GRAY;
        congratulationsText = generator.generateFont(parameter);
        parameter.size = 30;
        enemiesKilledText = generator.generateFont(parameter);
        parameter.size = 20;
        parameter.color = Color.WHITE;
        menuText = generator.generateFont(parameter);
        continueRetryText = generator.generateFont(parameter);

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

        ShapeBuilder.drawRect(new Vector2(width / 2 - 250, height / 5 * 2), new Vector2(200, 50), (win) ? Color.FOREST : Color.RED);
        ShapeBuilder.drawRect(new Vector2(width / 2 + 50, height / 5 * 2), new Vector2(200, 50), Color.TEAL);

        batch.begin();
        if (win){
            congratulationsText.draw(batch, "Congratulations, you won the level!", width / 2 - 400, height / 3 * 2);
        }
        else{
            congratulationsText.draw(batch, "You failed the level, try again!", width / 2 - 380, height / 3 * 2);
        }
        enemiesKilledText.draw(batch, "You killed " + enemiesKilled + " / " + (totalEnemies) + " enemies", width / 2 - 190, height / 3 * 2 - 70);
        continueRetryText.draw(batch, (win) ? "Next level" : "Retry", (win) ? width / 2 - 200 : width / 2 - 180, height / 5 * 2 + 32);
        menuText.draw(batch, "Main menu", width / 2 + 100, height / 5 * 2 + 32);
        batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        congratulationsText.dispose();
        enemiesKilledText.dispose();
        menuText.dispose();
        continueRetryText.dispose();
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
        if (win){
            prefs.putBoolean("level" + (level + 1), true);
            if (level < 4 ){
                prefs.putInteger("nextLevel", level + 1);
            }
            prefs.putBoolean("continueInLevel", false);
            prefs.putBoolean("continueNextLevel", true);

            PlayScreen.gameState = PlayScreen.GameState.PLAY_NEW_LEVEL;

            prefs.flush();
        }
        else{
            prefs.putBoolean("level" + (level + 1), false);
            if (level < 4 ){
                prefs.putInteger("nextLevel", level);
            }
            prefs.putBoolean("continueInLevel", false);
            prefs.putBoolean("continueNextLevel",false);

            PlayScreen.gameState = PlayScreen.GameState.PLAY_NEW_LEVEL;

            prefs.flush();
        }

        if (screenX >= width / 2 - 250 && screenX <= width / 2 - 50 && screenY <= height - (height / 5 * 2) && screenY >= height - (height / 5 * 2) - 70) {
            this.dispose();
        }
        else if (screenX >= width / 2 + 50 && screenX <= width / 2 + 250 && screenY <= height - (height / 5 * 2) && screenY >= height - (height / 5 * 2) - 70) {
            PlayScreen.gameState = PlayScreen.GameState.SHOW_MENU;
            this.dispose();
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
}

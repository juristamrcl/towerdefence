package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Marcel Jurišta on 15.10.2017.
 */

public class Turret extends ApplicationAdapter {

    private SpriteBatch batch;
    private Vector2 position;
    private Texture image;

    public Sprite getShootingRangeBounds() {
        return shootingRangeBounds;
    }

    private Sprite shootingRangeBounds;

    public Enemy getShootingEnemy() {
        return shootingEnemy;
    }

    public void setShootingEnemy(Enemy shootingEnemy) {
        this.shootingEnemy = shootingEnemy;
    }

    private Enemy shootingEnemy;
    private ShapeRenderer shapeRenderer;
    private Color shootingColor;

    private int id;
    private int width;
    private int height;
    private int cost;
    private int shootingLineHeight;

    public float getPower() {
        return power;
    }

    private float power;
    private float tileSize;
    private float shootingRange;

    public boolean isShooting() {
        return shooting;
    }

    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

    private boolean shooting;


    public Turret (Vector2 position, int id, float tileSize, int width, int height, float shootingRange, float power, int cost, Color shootingColor, int shootingLineHeight){
        this.position = position;
        this.id = id;
        this.tileSize = tileSize;
        this.width = width;
        this.height = height;
        this.shootingRange = shootingRange * 2;
        this.image = new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("tower" + id + ".png")), tileSize));
        this.cost = cost;
        this.power = power;
        this.shootingColor = shootingColor;
        this.shootingLineHeight = shootingLineHeight;

        shootingEnemy = new Enemy();
    }

    public int getCost() {
        return cost;
    }

    public void setPosition(Vector2 position) {
        position = position;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        shootingRangeBounds = new Sprite();
        shootingRangeBounds.setBounds(position.x + (tileSize / 2) - (shootingRange / 2),height - (position.y + (- tileSize / 2) + (shootingRange / 2)), shootingRange, shootingRange);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(image, position.x, height - position.y);
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
        image.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }

    public void shoot(){
        drawDebugLine(new Vector2(width - (width - (tileSize / 2) - position.x), height + (tileSize / 2) - position.y), new Vector2(width - (width - (tileSize / 2) - shootingEnemy.getPosition().x), height + (tileSize / 2) - shootingEnemy.getPosition().y), shootingLineHeight, shootingColor);
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
}

package com.tdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Marcel Jurišta on 17.10.2017.
 */

public class Enemy extends ApplicationAdapter {
    private static final float healthOffset = 15f;

    private SpriteBatch batch;
    private Sprite image;
    private Vector2 position;
    private Vector2 startPosition;
    private Vector2 endPosition;
    private Vector2 [][] centeredTilesPositions;
    private Vector2 actualGroundPosition;
    private ShapeRenderer shapeRenderer;

    private int [][] groundPositions;
    private int id;
    private int width;
    private int height;
    private int rotation = 270;
    private int price;

    private float damage = 1;
    private float health = 100;
    private float tileSize;
    private float speed = 0.0004f;
    private float timeSeconds = 0f;
    private float originalHealth;

    private boolean toDestroy = false;
    private boolean isPassedAway = false;

    public void setRunning(boolean running) {
        isRunning = running;
    }

    private boolean isRunning = true;

    public void setToDestroy(boolean toDestroy) {
        this.toDestroy = toDestroy;
    }

    private String actualDirection = "right";

    public Vector2 getPosition() {
        return position;
    }

    public Sprite getImage() {
        return image;
    }

    public float getDamage() {
        return damage;
    }
    public void loseHealth(float damage) {
        this.health -= damage;
    }
    public float getHealth() {
        return health;
    }

    public int getPrice() {
        return price;
    }

    public Enemy (int id, float tileSize, int width, int height, float damage, float health, float speed, int price, Vector2 [][] centeredTilesPositions, Vector2 startPosition, Vector2 endPosition, int [][] groundPositions, Vector2 actualGroundPosition){
        this.id = id;
        this.tileSize = tileSize;
        this.width = width;
        this.height = height;
        this.damage = damage;
        this.originalHealth = this.health = health;
        this.centeredTilesPositions = centeredTilesPositions;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.groundPositions = groundPositions;
        this.actualGroundPosition = actualGroundPosition;
        this.speed = speed;
        this.price = price;
    }
    public Enemy (){
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        position = new Vector2(centeredTilesPositions[(int)startPosition.x][(int)endPosition.x].x - tileSize, centeredTilesPositions[(int)startPosition.y][(int)endPosition.y].y);
        image = new Sprite(new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("enemy" + id + ".png")), tileSize)));
        image.setRotation(rotation);
        image.setPosition(position.x, height - position.y);
        image.setBounds(position.x,height - (height - position.y) , tileSize, tileSize);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        if(isRunning){
            timeSeconds += Gdx.graphics.getRawDeltaTime();
            if(timeSeconds > speed){
                timeSeconds -= speed;
                move();
            }
        }

        batch.begin();
        image.draw(batch);
        batch.end();

        showHealth(new Vector2(position.x, height - (position.y - healthOffset - tileSize)), new Vector2(position.x + (tileSize / 100 * (health / originalHealth * 100)), height - (position.y - healthOffset - tileSize)), 10, PlayScreen.healthColoring(health / originalHealth * 100));
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
    }

    public void move (){
        if (actualGroundPosition.x < PlayScreen.TILES_WIDTH_NUMBER && position.x >= 0){
            if ((actualDirection.equals("right") || actualDirection.equals("left")) && position.x % tileSize == 0){

                if (groundPositions[(int)actualGroundPosition.y + 1][(int)actualGroundPosition.x]  == 1){
                    actualDirection = "down";
                    actualGroundPosition.y++;
                }
                else if (groundPositions[(int)actualGroundPosition.y - 1][(int)actualGroundPosition.x]  == 1){
                    actualDirection = "up";
                    actualGroundPosition.y--;
                }
                else {
                    if(actualDirection.equals("right")){
                        actualGroundPosition.x++;
                    }
                    else {
                        actualGroundPosition.x--;
                    }
                }
            }

            else if ((actualDirection.equals("up") || actualDirection.equals("down")) && (height - position.y) % tileSize == 0){

                if (groundPositions[(int)actualGroundPosition.y][(int)actualGroundPosition.x - 1]  == 1){
                    actualDirection = "left";
                    actualGroundPosition.x--;
                }
                else if (groundPositions[(int)actualGroundPosition.y][(int)actualGroundPosition.x + 1]  == 1){
                    actualDirection = "right";
                    actualGroundPosition.x++;
                }
                else {
                    if(actualDirection.equals("up")){
                        actualGroundPosition.y--;
                    }
                    else {
                        actualGroundPosition.y++;
                    }
                }
            }
            if (actualDirection.equals("right")){
                position.x ++;
                rotation = 270;
            }
            else if (actualDirection.equals("left")){
                position.x --;
                rotation = 90;
            }
            else if (actualDirection.equals("up")){
                position.y --;
                rotation = 0;
            }
            else if (actualDirection.equals("down")){
                position.y ++;
                rotation = 180;
            }

        }
        else if (position.x < 0){
            position.x ++;
        }
        else{
            if (position.x == width){
                isPassedAway = true;
            }
            else {
                position.x ++;
            }
        }
        image.setPosition(position.x, height - position.y);
        image.setBounds(position.x, height - position.y, tileSize, tileSize);
        image.setRotation(rotation);
    }

    public boolean destroy(){
        return toDestroy;
    }
    public boolean passedAway(){
        return isPassedAway;
    }

    public void showHealth(Vector2 start, Vector2 end, int lineWidth, Color color)
    {
        Gdx.gl.glLineWidth(lineWidth);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.line(start, end);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
    }
}

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
import com.badlogic.gdx.utils.Timer;

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
    private int step = 4;
    private int rotation = 90;

    private float damage = 1;
    private float health = 100;
    private float timeSeconds = 0f;
    private float period = 0.0004f;
    private float tileSize;

    private boolean toDestroy = false;

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
    public void setDamage(float damage) {
        this.health -= damage;
    }
    public float getHealth() {
        return health;
    }

    public Enemy (int id, float tileSize, int width, int height, float damage, Vector2 [][] centeredTilesPositions, Vector2 startPosition, Vector2 endPosition, int [][] groundPositions, Vector2 actualGroundPosition){
        this.id = id;
        this.tileSize = tileSize;
        this.width = width;
        this.height = height;
        this.damage = damage;
        this.centeredTilesPositions = centeredTilesPositions;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.groundPositions = groundPositions;
        this.actualGroundPosition = actualGroundPosition;
    }

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.position = new Vector2(this.centeredTilesPositions[(int)startPosition.x][(int)endPosition.x].x - this.tileSize, this.centeredTilesPositions[(int)startPosition.y][(int)endPosition.y].y);
        this.image = new Sprite(new Texture(PlayScreen.convertPixmaps(new Pixmap(Gdx.files.internal("enemy1.png")), tileSize)));
        this.image.setRotation(this.rotation);
        this.image.setPosition(this.position.x, height - this.position.y);
        this.image.setBounds(this.position.x,height - (height - this.position.y) , this.tileSize, this.tileSize);
        this.shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        this.timeSeconds += Gdx.graphics.getRawDeltaTime();
        if(this.timeSeconds > this.period){
            this.timeSeconds -= this.period;
            this.move();
        }

        this.batch.begin();
        this.image.draw(this.batch);
        this.batch.end();

        this.showHealth(new Vector2(this.position.x, this.height - (this.position.y - healthOffset - this.tileSize)), new Vector2(this.position.x + (this.tileSize / 100 * this.health), this.height - (this.position.y - healthOffset - this.tileSize)), 10, Color.FOREST);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        this.batch.dispose();}

    public void move (){
        if (this.actualGroundPosition.x < PlayScreen.TILES_WIDTH_NUMBER && this.position.x >= 0){
            if ((this.actualDirection.equals("right") || this.actualDirection.equals("left")) && this.position.x % this.tileSize == 0){

                if (this.groundPositions[(int)this.actualGroundPosition.y + 1][(int)this.actualGroundPosition.x]  == 1){
                    this.actualDirection = "down";
                    this.actualGroundPosition.y++;
                }
                else if (this.groundPositions[(int)this.actualGroundPosition.y - 1][(int)this.actualGroundPosition.x]  == 1){
                    this.actualDirection = "up";
                    this.actualGroundPosition.y--;
                }
                else {
                    if(this.actualDirection.equals("right")){
                        this.actualGroundPosition.x++;
                    }
                    else {
                        this.actualGroundPosition.x--;
                    }
                }
            }

            else if ((this.actualDirection.equals("up") || this.actualDirection.equals("down")) && (height - this.position.y) % this.tileSize == 0){

                if (this.groundPositions[(int)this.actualGroundPosition.y][(int)this.actualGroundPosition.x - 1]  == 1){
                    this.actualDirection = "left";
                    this.actualGroundPosition.x--;
                }
                else if (this.groundPositions[(int)this.actualGroundPosition.y][(int)this.actualGroundPosition.x + 1]  == 1){
                    this.actualDirection = "right";
                    this.actualGroundPosition.x++;
                }
                else {
                    if(this.actualDirection.equals("up")){
                        this.actualGroundPosition.y--;
                    }
                    else {
                        this.actualGroundPosition.y++;
                    }
                }
            }
            if (this.actualDirection.equals("right")){
                this.position.x += this.step;
                this.rotation = 90;
            }
            else if (this.actualDirection.equals("left")){
                this.position.x -= this.step;
                this.rotation = 270;
            }
            else if (this.actualDirection.equals("up")){
                this.position.y -= this.step;
                this.rotation = 0;
            }
            else if (this.actualDirection.equals("down")){
                this.position.y += this.step;
                this.rotation = 180;
            }

        }
        else if (this.position.x < 0){
            this.position.x += step;
        }
        else{
            if (this.position.x == this.width){
                this.toDestroy = true;
            }
            else {
                this.position.x += this.step;
            }
        }
        this.image.setPosition(this.position.x, height - this.position.y);
        this.image.setBounds(this.position.x, height - this.position.y, this.tileSize, this.tileSize);
        this.image.setRotation(this.rotation);
    }

    public boolean destroy(){
        return this.toDestroy;
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

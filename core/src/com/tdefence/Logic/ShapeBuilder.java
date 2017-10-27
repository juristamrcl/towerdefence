package com.tdefence.Logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Marcel Jurišta on 24.10.2017.
 */

public class ShapeBuilder {

    public static void drawLine(Vector2 start, Vector2 end, int lineWidth, Color color)
    {
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        Gdx.gl.glLineWidth(lineWidth);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.line(start, end);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
        shapeRenderer.dispose();
    }

    public static void drawCircle(Vector2 position, float radius, int lineWidth, float height, Color color)
    {
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        Gdx.gl.glLineWidth(lineWidth);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(position.x, height - position.y, radius);
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1);
        shapeRenderer.dispose();
    }

    public static void drawRect(Vector2 start, Vector2 size, Color color)
    {
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(start.x, start.y, size.x, size.y);
        shapeRenderer.end();
        shapeRenderer.dispose();
    }
}

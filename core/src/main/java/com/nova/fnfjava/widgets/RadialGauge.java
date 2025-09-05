package com.nova.fnfjava.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class RadialGauge extends Widget {
    private float progress = 0f;
    private Color fillColor = Color.WHITE;
    private Color backgroundColor = Color.BLACK;
    private ShapeRenderer shapeRenderer;

    private float innerRadius = 0f; // 0 means completely filled, > 0 creates donut shape
    private float start = 90f;
    private boolean clockwise = true;
    public float radius;
    private int segments = 100;
    private RadialGaugeShape shape = RadialGaugeShape.CIRCLE;

    public RadialGauge(float x, float y, float radius, Color color, int frames, RadialGaugeShape shape, boolean clockwise, float innerRadius) {
        setPosition(x, y);
        this.radius = radius;
        setSize(radius * 2, radius * 2);
        this.fillColor = color;
        this.segments = frames;
        this.shape = shape;
        this.clockwise = clockwise;
        this.innerRadius = innerRadius;
        shapeRenderer = new ShapeRenderer();

        shapeRenderer.setAutoShapeType(true);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float cx = getX() + getWidth() / 2f;
        float cy = getY() + getHeight() / 2f;

        batch.end();

        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(getStage().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw background shape
        float scale = Math.max(getScaleX(), getScaleY());
        float outerRadius = radius * scale;

        float finalAlpha = getColor().a * parentAlpha;

        // Draw full background circle
/*        if (backgroundColor.a > 0) {
            shapeRenderer.setColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a * finalAlpha);
            drawCircle(cx, cy, outerRadius);
        }*/

        // Draw progress as donut segment
        if (progress > 0 && finalAlpha > 0) {
            shapeRenderer.setColor(fillColor.r, fillColor.g, fillColor.b, fillColor.a * finalAlpha);
            float scaledInnerRadius = innerRadius * scale; // Scale the inner radius too!
            drawProgressSegment(cx, cy, outerRadius, scaledInnerRadius, start, clockwise ? -progress * 360f : progress * 360f);
        }

        shapeRenderer.end();
        batch.begin();
    }

    private void drawCircle(float cx, float cy, float radius) {
        shapeRenderer.circle(cx, cy, radius);
    }

    private void drawProgressSegment(float cx, float cy, float outerRadius, float innerRadius, float start, float sweep) {
        if (innerRadius <= 0) {
            // Simple filled arc - much more efficient
            shapeRenderer.arc(cx, cy, outerRadius, start, sweep, segments);
        } else {
            // Draw donut shape using two arcs (if using line mode) or manual triangulation
            if (shapeRenderer.getCurrentType() == ShapeRenderer.ShapeType.Line) {
                // Draw both arcs as outlines
                shapeRenderer.arc(cx, cy, outerRadius, start, sweep, segments);
                shapeRenderer.arc(cx, cy, innerRadius, start, sweep, Math.max(1, segments / 2));

                // Draw connecting lines at start and end
                float startRad = start * MathUtils.degreesToRadians;
                float endRad = (start + sweep) * MathUtils.degreesToRadians;

                shapeRenderer.line(
                    cx + MathUtils.cos(startRad) * innerRadius,
                    cy + MathUtils.sin(startRad) * innerRadius,
                    cx + MathUtils.cos(startRad) * outerRadius,
                    cy + MathUtils.sin(startRad) * outerRadius
                );

                shapeRenderer.line(
                    cx + MathUtils.cos(endRad) * innerRadius,
                    cy + MathUtils.sin(endRad) * innerRadius,
                    cx + MathUtils.cos(endRad) * outerRadius,
                    cy + MathUtils.sin(endRad) * outerRadius
                );
            } else {
                // For filled mode, we need to manually triangulate the donut
                drawDonutSegment(cx, cy, outerRadius, innerRadius, start, sweep);
            }
        }
    }

    private void drawDonutSegment(float cx, float cy, float outerRadius, float innerRadius, float start, float sweep) {
        float angleStep = sweep / segments;

        for (int i = 0; i < segments; i++) {
            float angle1 = start + i * angleStep;
            float angle2 = start + (i + 1) * angleStep;

            float cos1 = MathUtils.cosDeg(angle1), sin1 = MathUtils.sinDeg(angle1);
            float cos2 = MathUtils.cosDeg(angle2), sin2 = MathUtils.sinDeg(angle2);

            float x1o = cx + cos1 * outerRadius;
            float y1o = cy + sin1 * outerRadius;
            float x2o = cx + cos2 * outerRadius;
            float y2o = cy + sin2 * outerRadius;

            float x1i = cx + cos1 * innerRadius;
            float y1i = cy + sin1 * innerRadius;
            float x2i = cx + cos2 * innerRadius;
            float y2i = cy + sin2 * innerRadius;

            // Two triangles per segment to form a donut slice
            shapeRenderer.triangle(x1i, y1i, x1o, y1o, x2o, y2o);
            shapeRenderer.triangle(x1i, y1i, x2o, y2o, x2i, y2i);
        }
    }

    public void setAmount(float amount) {
        this.progress = MathUtils.clamp(amount, 0f, 1f);
    }

    public float getAmount() {
        return progress;
    }

    public void setInnerRadius(float innerRadius) {
        this.innerRadius = MathUtils.clamp(innerRadius, 0f, Math.min(getWidth(), getHeight()) / 2f);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    @Override
    public boolean remove() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        return super.remove();
    }

    public void setAlpha(float alpha) {
        alpha = MathUtils.clamp(alpha, 0f, 1f);
        getColor().a = alpha;
    }

    public enum RadialGaugeShape {
        CIRCLE, SQUARE
    }
}

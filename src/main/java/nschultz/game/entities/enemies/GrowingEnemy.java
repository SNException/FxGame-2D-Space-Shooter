/*
 *
 * The MIT License
 *
 * Copyright 2018 Niklas Schultz.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package nschultz.game.entities.enemies;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import nschultz.game.ui.GameCanvas;
import nschultz.game.util.NumberNegation;

public final class GrowingEnemy extends Enemy {

    private double velocity;

    private double wDelta = 0.25;
    private double hDelta = 0.25;

    private final Image image;

    public GrowingEnemy(final Point2D position, final double velocity,
                        final GameCanvas game) {

        super(position, new Dimension2D(8, 8), game);
        this.velocity = velocity;
        image = new Image(getClass().getResource("/growing enemy.png").toExternalForm());
    }


    @Override
    public void update(final long now) {
        super.update(now);
        moveLeft(velocity);
        killIfOutOfBounds();

        final double maxWidth = 128;
        final double maxHeight = 128;
        if (width() >= maxWidth)
            wDelta = new NumberNegation(wDelta).value();
        if (height() >= maxHeight)
            hDelta = new NumberNegation(hDelta).value();

        grow(new Dimension2D(width() + wDelta, height() + hDelta));

        if (width() <= 0 && height() <= 0)
            kill();
    }

    private void killIfOutOfBounds() {
        if (xPosition() <= 0)
            kill();
    }

    @Override
    public void render(final GraphicsContext brush, final long now) {
        brush.drawImage(image, xPosition(), yPosition(), width(), height());
    }
}

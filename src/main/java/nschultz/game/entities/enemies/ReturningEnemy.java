/*
 *
 * The MIT License
 *
 * Copyright 2019 Niklas Schultz.
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
import nschultz.game.ui.GameView;
import nschultz.game.util.NumberNegation;
import nschultz.game.io.SpriteSheet;

public final class ReturningEnemy extends Enemy {

    private static final Image sprite = SpriteSheet.instance().sprite(
            2, 2, 16, 16
    );

    private final GameView game;
    private double velocity;
    private boolean returnedOnce = false;

    public ReturningEnemy(final Point2D position, final double velocity,
                          final GameView game) {

        super(position, new Dimension2D(16, 16), game);
        this.game = game;
        this.velocity = velocity;
    }

    @Override
    public void update(final long now) {
        super.update(now);
        moveLeft(velocity);
        returnAfterRightIsReached();
    }

    private void returnAfterRightIsReached() {
        if (returnedOnce) {
            killIfOutOfBounds();
        }
        if (xPosition() <= 0) {
            velocity = new NumberNegation(velocity).value();
            returnedOnce = true;
        }
    }

    private void killIfOutOfBounds() {
        if (xPosition() >= game.startingWidth()) {
            kill();
        }
    }

    @Override
    public void render(final GraphicsContext brush, final long now) {
        brush.drawImage(sprite, xPosition(), yPosition(), width(), height());
    }
}

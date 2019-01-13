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
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import nschultz.game.io.SpriteSheet;
import nschultz.game.ui.GameCanvas;
import nschultz.game.util.IsWhitePixelColor;

import java.util.Random;

public final class ChargingEnemy extends Enemy {

    private static final Image sprite = SpriteSheet.instance().sprite(
            1, 2, 32, 32
    );

    private final double initialXPositionCapture;
    private final double chargingVelocity;
    private Color color = new Color(1, 1, 1, 1);
    private boolean steppedOut = false;
    private boolean chargeCooldown = false;
    private final double movingAmountBeforeCharging;
    private int chargeDelay;
    private final WritableImage image;
    private final PixelWriter writer;
    private final PixelReader reader;

    public ChargingEnemy(final Point2D position, final GameCanvas game) {
        super(position, new Dimension2D(32, 32), game);

        final Random rng = new Random();
        chargingVelocity = rng.nextInt(16) + 16;
        movingAmountBeforeCharging = rng.nextInt(128) + 64;
        initialXPositionCapture = xPosition();

        reader = sprite.getPixelReader();
        image = new WritableImage(sprite.getPixelReader(), 32, 32);
        writer = image.getPixelWriter();
    }

    @Override
    public void update(final long now) {
        super.update(now);
        killIfOutOfBounds();

        if (!steppedOut) {
            final double steppingOutVelocity = 2;
            moveLeft(steppingOutVelocity);
        }

        if (initialXPositionCapture - xPosition() >= movingAmountBeforeCharging) {
            steppedOut = true;
        }

        if (steppedOut) {
            chargeDelay++;
            if (chargeDelay >= 180) chargeCooldown = true;
            if (chargeCooldown) {
                moveLeft(chargingVelocity);
            } else {
                final double colorDelta = 0.01;
                double newValue = color.getBlue() - colorDelta;
                if (newValue <= 0) {
                    newValue = 0;
                }
                color = new Color(color.getRed(), newValue, newValue, 1);
                for (int x = 0; x < 32; x++) {
                    for (int y = 0; y < 32; y++) {
                        final Color c = reader.getColor(x, y);
                        if (new IsWhitePixelColor(c).value()) {
                            writer.setColor(x, y, color);
                        }
                    }
                }
            }
        }
    }

    private void killIfOutOfBounds() {
        if (xPosition() <= 0)
            kill();
    }

    @Override
    public void render(final GraphicsContext brush, final long now) {
        brush.setFill(color);
        brush.drawImage(image, xPosition(), yPosition(), width(), height());
    }
}

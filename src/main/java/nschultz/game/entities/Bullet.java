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
package nschultz.game.entities;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.MotionBlur;
import javafx.scene.paint.Color;
import nschultz.game.entities.enemies.Enemy;
import nschultz.game.io.SoundFile;
import nschultz.game.ui.AlphaParticle;
import nschultz.game.ui.GameCanvas;

import java.util.Random;

public final class Bullet extends Entity {

    private static final SoundFile crashSound = new SoundFile(
            "/sounds/crash.wav"
    );

    private final GameCanvas game;

    Bullet(final Point2D position, final GameCanvas game) {
        super(position, new Dimension2D(8, 4), game);
        this.game = game;
    }

    @Override
    public void update(final long now) {
        checkCollisionWithEnemies();
        final double velocity = 10;
        moveRight(velocity);
        killIfOutOfBounds();
    }

    private void killIfOutOfBounds() {
        if (xPosition() >= gameWidth())
            kill();
    }

    private void checkCollisionWithEnemies() {
        game.entities().stream()
                .filter(entity -> entity instanceof Enemy)
                .forEach(entity -> {
                    final Enemy enemy = (Enemy) entity;
                    if (hitBox().intersects(
                            enemy.xPosition(), enemy.yPosition(),
                            enemy.width(), enemy.height())) {

                        kill();
                        enemy.kill();
                        if (game.isAudioEnabled()) crashSound.play();
                        game.increaseScore();

                        if (game.particlesActive()) {
                            final var rng = new Random();
                            for (int i = 0; i < 16; i++) {
                                double randomVelocityX = rng.nextDouble() * -8;
                                double randomVelocityY;
                                if (rng.nextDouble() < 0.5) {
                                    randomVelocityY = rng.nextDouble() * 8;
                                } else {
                                    randomVelocityY = rng.nextDouble() * -8;
                                }
                                game.explosionParticles().add(new AlphaParticle(
                                        enemy.xPosition(), enemy.yPosition(),
                                        new Point2D(
                                                randomVelocityX, randomVelocityY
                                        ), Color.RED)
                                );
                            }
                        }
                    }
                });
    }

    @Override
    public void render(final GraphicsContext brush, final long now) {
        brush.setFill(Color.CYAN);
        brush.setEffect(new MotionBlur());
        brush.fillRect(xPosition(), yPosition(), width(), height());
        brush.setEffect(null);
    }
}

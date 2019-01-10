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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import nschultz.game.io.SoundFile;
import nschultz.game.io.SpriteSheet;
import nschultz.game.states.MenuState;
import nschultz.game.ui.AlphaParticle;
import nschultz.game.ui.GameCanvas;
import nschultz.game.util.TimeDelayedProcedure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class Player extends Entity {

    private static final SoundFile shootingSound = new SoundFile(
            "/sounds/shot.wav"
    );

    private static final Image sprite1 = SpriteSheet.instance().sprite(
            2, 1, 64, 32
    );

    private static final Image sprite2 = SpriteSheet.instance().sprite(
            3, 1, 64, 32
    );

    private final GameCanvas game;
    private final double velocity = 8;

    private boolean isMovingUp;
    private boolean isMovingDown;
    private boolean isMovingRight;
    private boolean isMovingLeft;
    private boolean isFaster;
    private boolean playerPaused;
    private boolean shootBullet;

    private final TimeDelayedProcedure bulletDelay = new TimeDelayedProcedure(
            200, TimeUnit.MILLISECONDS
    );
    private final TimeDelayedProcedure imageDelay = new TimeDelayedProcedure(
            800, TimeUnit.MILLISECONDS
    );

    private final Image[] image = new Image[2];
    private final List<AlphaParticle> thrustParticles = new ArrayList<>();
    private final Random rng = new Random(64);
    private static boolean thrustParticlesActive = false;
    private int imageIndex;

    public Player(final Point2D position, final GameCanvas game) {
        super(position, new Dimension2D(64, 32), game);
        this.game = game;
        image[0] = sprite1;
        image[1] = sprite2;
        playerPaused = false;
    }

    @Override
    public void update(final long now) {
        checkMoveUp();
        checkMoveDown();
        checkMoveRight();
        checkMoveLeft();
        checkShootBullet(now);
        thrustParticles.forEach(AlphaParticle::update);
        thrustParticles.removeIf(AlphaParticle::isDead);
        imageDelay.runAfterDelay(now, () -> {
            if (imageIndex == 0) {
                imageIndex = 1;
            } else {
                imageIndex = 0;
            }
        });

    }

    private void checkShootBullet(final long now) {
        if (shootBullet) {
            bulletDelay.runAfterDelay(now, () -> {
                game.spawn(new Bullet(
                        new Point2D(
                                xPosition() + (width() / 2),
                                yPosition() + (height() / 2)), game)
                );
                if (game.isAudioEnabled()) shootingSound.play();
            });
        }
    }

    private void addThrustParticles(final Color color, final double velX,
                                    final double x, final double y) {
        for (int i = 0; i < 16; i++) {
            double randomVelocityX = rng.nextDouble() * velX;
            double randomVelocityY;
            if (rng.nextDouble() < 0.5) {
                randomVelocityY = rng.nextDouble() * 4;
            } else {
                randomVelocityY = rng.nextDouble() * -4;
            }

            thrustParticles.add(new AlphaParticle(
                    x, y,
                    new Point2D(randomVelocityX, randomVelocityY),
                    color)
            );
        }
    }

    private double getCurrentVelocity()
    {
        if (isFaster)
            return velocity;
        else return velocity/2;
    }

    private void checkMoveUp() {
        if (isMovingUp) {
            if (yPosition() - getCurrentVelocity() <= 0) {
                return;
            }
            moveUp(getCurrentVelocity());
        }
    }

    private void checkMoveDown() {
        if (isMovingDown) {
            if (yPosition() + getCurrentVelocity() >= gameHeight() - height()) {
                return;
            }
            moveDown(getCurrentVelocity());
        }
    }

    private void checkMoveRight() {
        if (isMovingRight) {
            if (xPosition() + getCurrentVelocity() >= gameWidth() - width()) {
                return;
            }
            moveRight(getCurrentVelocity());
            if (thrustParticlesActive) {
                addThrustParticles(Color.DARKORANGE, -4, xPosition() - (width() / 4), yPosition() + (height() / 2) - 4);
            }

        }
    }

    private void checkMoveLeft() {
        if (isMovingLeft) {
            if (xPosition() - getCurrentVelocity() <= 0) {
                return;
            }
            moveLeft(getCurrentVelocity());
            if (!isMovingRight) { // generates to many particles otherwise
                if (thrustParticlesActive) {
                    addThrustParticles(Color.DARKBLUE, 4, xPosition() - (width() / 4), yPosition() + (height() / 2) - 4);
                }
            }
        }
    }

    @Override
    public void render(final GraphicsContext brush, final long now) {
        //brush.setFill(Color.BLUE);
        //brush.fillRect(xPosition(), yPosition(), width(), height());
        brush.drawImage(image[imageIndex], xPosition(), yPosition(), width(), height());
        thrustParticles.forEach(particle -> particle.render(brush));
    }

    public void onKeyInput(final KeyEvent event, final boolean isPressed) {
        if (!playerPaused) {
            switch (event.getCode()) {
                case W:
                    isMovingUp = isPressed;
                    break;
                case S:
                    isMovingDown = isPressed;
                    break;
                case A:
                    isMovingLeft = isPressed;
                    break;
                case D:
                    isMovingRight = isPressed;
                    break;
                case SHIFT:
                    isFaster = isPressed;
                    break;
                case SPACE:
                    shootBullet = isPressed;
                    break;
                case P:
                    game.pause();
                    game.switchGameState(new MenuState(game, game.getCurrentGameState()));
                    playerPaused = true;
                    break;
            }
        }
        else if (event.getCode()== KeyCode.ENTER)
            playerPaused = false;
    }

    public void enableThrustParticles() {
        thrustParticlesActive = true;
    }

    public void disableThrustParticles() {
        thrustParticlesActive = false;
    }

    public boolean isThrustParticlesActive() {
        return thrustParticlesActive;
    }
}

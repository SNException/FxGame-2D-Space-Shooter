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
package nschultz.game.ui;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import nschultz.game.core.GameLoop;
import nschultz.game.entities.Entity;
import nschultz.game.entities.Player;
import nschultz.game.states.CurrentGameState;
import nschultz.game.states.GameState;
import nschultz.game.states.MenuState;
import nschultz.game.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public final class GameCanvas {

    private final GameLoop gameLoop = new GameLoop(this);
    private GraphicsContext brush;
    private final List<Entity> entities = new CopyOnWriteArrayList<>();
    private final List<MovingParticle> stars = new ArrayList<>(128);
    private final List<AlphaParticle> explosionParticles = new ArrayList<>(128);
    private final Random starRandom = new Random(64L);
    private final TimeDelayedProcedure starDelay = new TimeDelayedProcedure(
            250L, TimeUnit.MILLISECONDS
    );
    private final Dimension2D resolution;

    private Player player;
    private int score = 0;
    private CurrentGameState currentGameState = new CurrentGameState(
            new MenuState(this)
    );

    private double startingWidth;
    private double startingHeight;
    private boolean shouldRenderLevelChange;
    private boolean particlesActive = false;
    private boolean isAudioEnabled = true;

    private double alpha = 0.0;
    private double delta = 0.009;
    private boolean fadedIn = false;

    private final Result<Canvas> view;

    GameCanvas(final Dimension2D resolution) {
        this.resolution = resolution;

        view = new Cache<>(() -> {
            final var canvas = new Canvas();
            brush = canvas.getGraphicsContext2D();
            canvas.setFocusTraversable(true);
            canvas.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.P) {
                    pause();
                }
                player.onKeyInput(event, true);
                currentGameState.onKeyInput(event, true);
            });
            canvas.setOnKeyReleased(event -> {
                player.onKeyInput(event, false);
                currentGameState.onKeyInput(event, false);
            });
            return canvas;
        });
    }

    public Canvas view() {
        return view.value();
    }

    void startGameLoop() {
        if (gameLoop.isRunning())
            return;

        startingWidth = view().getWidth();
        startingHeight = view().getHeight();
        player = new Player(new Point2D(startingWidth / 2, (startingHeight / 2) + 64), this);
        entities.add(player);
        new AttemptsToEnsureGc(2).run();
        gameLoop.start();
    }

    public void update(final long now) {
        currentGameState.update(now);
        if (particlesActive) {
            starDelay.runAfterDelayExact(now, () ->
                    stars.add(new MovingParticle(
                            startingWidth, starRandom.nextInt((int) startingHeight),
                            new Point2D(starRandom.nextInt(2) + 2, 0), Color.WHITE))
            );
        }
        stars.forEach(MovingParticle::update);
        stars.removeIf(MovingParticle::isDead);
        explosionParticles.forEach(AlphaParticle::update);
        explosionParticles.removeIf(AlphaParticle::isDead);
    }

    public void render(final long now) {
        currentGameState.render(brush, now);
        stars.forEach(particle -> particle.render(brush));
        explosionParticles.forEach(particle -> particle.render(brush));
        if (new IsPlayableState(currentGameState.value()).value()) {
            renderCurrentLevel();
            renderScore();
        }
        renderFPSCounter();
        renderLevelChange();
    }

    private void renderLevelChange() {
        if (shouldRenderLevelChange) {
            alpha += delta;
            if (alpha >= 0.9) {
                fadedIn = true;
            }
            if (fadedIn) {
                delta = new NumberNegation(delta).value();
                fadedIn = false;
            }

            if (alpha <= 0) {
                shouldRenderLevelChange = false;
                fadedIn = false;
                alpha = 0.0;
                delta = 0.005;
                return;
            }

            brush.setTextBaseline(VPos.CENTER);
            brush.setTextAlign(TextAlignment.CENTER);
            final double maxWidth = 800;
            brush.setGlobalAlpha(alpha);
            brush.setFont(Font.font(64));
            brush.setFill(Color.WHITE);
            brush.fillText(currentGameState.value().toString(),
                    (startingWidth / 2),
                    startingHeight / 2, maxWidth
            );
            brush.setGlobalAlpha(1);
        }
        brush.setTextBaseline(VPos.BASELINE);
        brush.setTextAlign(TextAlignment.LEFT);
    }

    private void renderScore() {
        brush.setFont(Font.font(14));
        brush.setFill(Color.LIGHTGREEN);
        brush.fillText("Score: " + score, 32, 64);
    }

    private void renderCurrentLevel() {
        brush.setFont(Font.font(14));
        brush.setFill(Color.CYAN);
        brush.fillText(currentGameState.toString(), 32, 48);
    }

    private void renderFPSCounter() {
        brush.setFont(Font.font(14));
        brush.setFill(gameLoop.isLagging() ? Color.RED : Color.YELLOW);
        brush.fillText("FPS/UPS: " + gameLoop.fps(), 32, 32);
    }

    public List<Entity> entities() {
        return entities;
    }

    public List<AlphaParticle> explosionParticles() {
        return explosionParticles;
    }

    public Player player() {
        return Objects.requireNonNull(player);
    }

    public void spawn(final Entity entity) {
        entities.add(entity);
    }

    public Dimension2D resolution() {
        return resolution;
    }

    public void switchGameState(final GameState newState) {
        shouldRenderLevelChange = false;
        currentGameState = currentGameState.switchTo(newState);
        if (new IsPlayableState(newState).value()) {
            shouldRenderLevelChange = true;
        }
    }

    public void restart() {
        player = new Player(new Point2D(startingWidth / 2, (startingHeight / 2) + 64), this);
        entities.add(player);

        view().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.P) {
                pause();
            }
            player.onKeyInput(event, true);
            currentGameState.onKeyInput(event, true);
        });
        view().setOnKeyReleased(event -> {
            player.onKeyInput(event, false);
            currentGameState.onKeyInput(event, false);
        });
        score = 0;
        new AttemptsToEnsureGc(2).run();
    }

    public void pause() {
        if (new IsPlayableState(currentGameState.value()).value()) {
            if (gameLoop.isRunning()) {
                gameLoop.stop();
            } else {
                gameLoop.start();
            }
        }
    }

    public double startingWidth() {
        return startingWidth;
    }

    public double startingHeight() {
        return startingHeight;
    }

    public void increaseScore() {
        score++;
    }

    public int score() {
        return score;
    }

    public void disableParticles() {
        particlesActive = false;
    }

    public void enableParticles() {
        particlesActive = true;
    }

    public boolean particlesActive() {
        return particlesActive;
    }

    public void enableSound() {
        isAudioEnabled = true;
    }

    public void disableSound() {
        isAudioEnabled = false;
    }

    public boolean isAudioEnabled() {
        return isAudioEnabled;
    }

    public GameState currentGameState() {
        return currentGameState.value();
    }
}

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
package nschultz.game.states.levels;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import nschultz.game.entities.Entity;
import nschultz.game.entities.enemies.JumpingEnemy;
import nschultz.game.entities.enemies.SimpleEnemy;
import nschultz.game.states.GameOverState;
import nschultz.game.states.GameState;
import nschultz.game.ui.GameCanvas;
import nschultz.game.util.TimeDelayedProcedure;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public final class Level13State extends GameState {

    private static final int MAX_AMOUNT_OF_ENEMIES = 1000;

    private final Random rng = new Random(1300);
    private final TimeDelayedProcedure spawnDelaySimpleEnemies =
            new TimeDelayedProcedure(
                    150, TimeUnit.MILLISECONDS
            );
    private final TimeDelayedProcedure spawnDelayJumpingEnemies =
            new TimeDelayedProcedure(
                    100, TimeUnit.MILLISECONDS
            );

    private int totalAmountOfEnemySpawned;

    Level13State(final GameCanvas game) {
        super(game);
    }

    private void spawnEnemy(final long now) {
        if (totalAmountOfEnemySpawned < MAX_AMOUNT_OF_ENEMIES) {
            final int yOffset = 64;
            spawnDelaySimpleEnemies.runAfterDelayExact(now, () -> {
                game().entities().add(new SimpleEnemy(new Point2D(
                        game().resolution().getWidth(), 32
                ), 8, game()));
                totalAmountOfEnemySpawned++;

                game().entities().add(new SimpleEnemy(new Point2D(
                        game().resolution().getWidth(), 720
                ), 8, game()));
                totalAmountOfEnemySpawned++;
            });
            spawnDelayJumpingEnemies.runAfterDelay(now, () -> {
                game().entities().add(new JumpingEnemy(new Point2D(
                        game().resolution().getWidth(),
                        rng.nextInt(
                                (int) game().resolution().getHeight() - yOffset
                        )
                ), game()));
                totalAmountOfEnemySpawned++;
            });
        }
    }

    @Override
    public void update(final long now) {
        spawnEnemy(now);
        game().entities().forEach(entity -> entity.update(now));
        game().entities().removeIf(Entity::isDead);

        if (hasPlayerDied()) {
            game().switchGameState(new GameOverState(game()));
        }

        if (isLevelCompleted() && totalAmountOfEnemySpawned == MAX_AMOUNT_OF_ENEMIES) {
            game().switchGameState(new Level14State(game()));
        }
    }

    @Override
    public void render(final GraphicsContext brush, final long now) {
        brush.setFill(Color.BLACK);
        brush.fillRect(0, 0, game().resolution().getWidth(),
                game().resolution().getHeight()
        );
        game().entities().forEach(entity -> entity.render(brush, now));
    }

    @Override
    public String toString() {
        return "Level 13 (Passage)";
    }
}
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
package nschultz.game.states.levels;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import nschultz.game.entities.Entity;
import nschultz.game.entities.enemies.ReturningEnemy;
import nschultz.game.states.GameOverState;
import nschultz.game.states.GameState;
import nschultz.game.ui.GameCanvas;

import java.util.Random;

public final class Level10State extends GameState {

    private static final int MAX_AMOUNT_OF_ENEMIES = 200;
    private final Random rng = new Random(1000);
    private int totalAmountOfEnemySpawned;
    private int spawnDelay;

    Level10State(final GameCanvas game) {
        super(game);
    }

    private void spawnEnemy() {
        spawnDelay++;
        if (spawnDelay >= 8) {
            if (totalAmountOfEnemySpawned < MAX_AMOUNT_OF_ENEMIES) {
                final int yOffset = 64;
                game().entities().add(new ReturningEnemy(new Point2D(
                        game().resolution().getWidth(),
                        rng.nextInt(
                                (int) game().resolution().getHeight() - yOffset
                        )
                ), rng.nextInt(8) + 2, game()));

                totalAmountOfEnemySpawned++;
                spawnDelay = 0;
            }
        }
    }

    @Override
    public void update(final long now) {
        spawnEnemy();
        game().entities().forEach(entity -> entity.update(now));
        game().entities().removeIf(Entity::isDead);

        if (hasPlayerDied()) {
            game().switchGameState(new GameOverState(game()));
        }

        if (isLevelCompleted() && totalAmountOfEnemySpawned == MAX_AMOUNT_OF_ENEMIES) {
            game().switchGameState(new Level11State(game()));
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
        return "Level 10 (Cowards)";
    }
}

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
package nschultz.game.states;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import nschultz.game.ui.GameView;

public abstract class GameState {

    private final GameView game;
    private GameState lastGameState;

    public GameState(final GameView game) {
        this.game = game;
    }

    public abstract void update(final long now);

    public abstract void render(final GraphicsContext brush, final long now);

    /**
     * Override when needed.
     * We do not use game loop for that; reason:
     * KeyEvents can be event driven for better performance.
     *
     * @param event     the event
     * @param isPressed whether the key is pressed or not
     */
    public void onKeyInput(final KeyEvent event, final boolean isPressed) {
    }

    public final GameView game() {
        return game;
    }

    public void setLastGameState(final GameState newState) {
        lastGameState = newState;
    }

    protected final GameState lastGameState() {
        return lastGameState;
    }
}

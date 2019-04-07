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

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import nschultz.game.ui.GameView;
import nschultz.game.util.Highscore;
import nschultz.game.util.IsNewHighscore;

public final class GameOverState extends GameState {

    public GameOverState(final GameView game) {
        super(game);
        game().entities().clear();
        if (new IsNewHighscore(game).value()) {
            new Highscore().save(game.score());
        }
    }

    @Override
    public void update(final long now) {
    }

    @Override
    public void render(final GraphicsContext brush, final long now) {
        final double w = game().resolution().getWidth();
        final double h = game().resolution().getHeight();

        brush.setFill(Color.BLACK);
        brush.fillRect(0, 0, w, h);

        brush.setTextAlign(TextAlignment.CENTER);
        brush.setTextBaseline(VPos.CENTER);
        brush.setEffect(new Reflection());
        brush.setFill(Color.RED);
        brush.setFont(Font.font(64));
        final int maxWidth = 512;
        brush.fillText("Game Over!", w / 2, h / 3.5, maxWidth);
        brush.setFont(Font.font(48));
        brush.setEffect(null);
        brush.setFill(Color.ORANGE);
        brush.fillText("You reached a score of " + game().score(),
                w / 2, h / 2, maxWidth
        );

        brush.setFill(Color.GRAY);
        brush.setFont(Font.font(32));
        brush.fillText("Press ENTER to continue", w / 2, h / 1.75, maxWidth);
        brush.setTextBaseline(VPos.BASELINE);
        brush.setTextAlign(TextAlignment.LEFT);
    }

    @Override
    public void onKeyInput(final KeyEvent event, final boolean isPressed) {
        if (isPressed) {
            switch (event.getCode()) {
                case ENTER:
                    game().restart();
                    game().switchGameState(new MenuState(game()));
                    break;
            }
        }
    }
}
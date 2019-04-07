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
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import nschultz.game.ui.GameView;
import nschultz.game.util.Highscore;
import nschultz.game.util.NumberNegation;

public class HighscoreState extends GameState {

    private final int highScore = new Highscore().score();
    private double delta = 0.008;
    private double alpha = 1.0;

    HighscoreState(final GameView game) {
        super(game);
    }

    @Override
    public void onKeyInput(final KeyEvent event, final boolean isPressed) {
        if (isPressed) {
            if (event.getCode() == KeyCode.ENTER) {
                if (this.lastGameState()==null)
                    game().switchGameState(new MenuState(game()));
                else game().switchGameState(new MenuState(game(), this.lastGameState()));
            }
        }
    }

    @Override
    public void update(final long now) {
        alpha -= delta;
        if (alpha <= 0.4 || alpha >= 1) {
            delta = new NumberNegation(delta).value();
        }
    }

    @Override
    public void render(final GraphicsContext brush, final long now) {
        final double w = game().resolution().getWidth();
        final double h = game().resolution().getHeight();

        brush.setFill(Color.BLACK);
        brush.fillRect(0, 0, w, h);

        brush.setFill(Color.YELLOW);
        brush.setEffect(new Glow(1.0));
        brush.setFont(Font.font(64));
        brush.setTextAlign(TextAlignment.CENTER);
        brush.setTextBaseline(VPos.CENTER);
        brush.fillText("You current high score is: ", w / 2, h / 4);
        brush.setFill(Color.LIGHTYELLOW);

        brush.setGlobalAlpha(alpha);
        brush.fillText(String.valueOf(highScore), w / 2, h / 2);

        // reset for other render() users
        brush.setGlobalAlpha(1);
        brush.setEffect(null);
        brush.setTextAlign(TextAlignment.LEFT);
        brush.setTextBaseline(VPos.BASELINE);
    }
}


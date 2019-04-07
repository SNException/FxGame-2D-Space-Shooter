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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import nschultz.game.io.InputStreamAsString;
import nschultz.game.ui.GameView;
import nschultz.game.util.Cache;
import nschultz.game.util.Result;

import java.util.concurrent.Callable;

public class CreditsState extends GameState {

    private final Result<String> license;
    private double y = game().resolution().getHeight() + 400;

    CreditsState(final GameView game) {
        super(game);
        license = new Cache<>(new Callable<>() {
            @Override
            public String call() {
                return new InputStreamAsString(
                        getClass().getResourceAsStream("/other/mit.txt"), 512
                ).toString();
            }
        });
    }

    @Override
    public void update(final long now) {
        if (y == -400) {
            if (lastGameState() == null) {
                game().switchGameState(new MenuState(game()));
            } else {
                game().switchGameState(new MenuState(game(), lastGameState()));
            }
        } else {
            y -= 1;
        }
    }

    @Override
    public void render(final GraphicsContext brush, final long now) {
        final double w = game().resolution().getWidth();

        brush.setFill(Color.BLACK);
        brush.fillRect(0, 0, game().resolution().getWidth(),
                game().resolution().getHeight());

        brush.setTextBaseline(VPos.CENTER);
        brush.setTextAlign(TextAlignment.CENTER);
        brush.setFont(Font.font(28));
        brush.setFill(Color.WHITE);
        brush.fillText(license.value(), w / 2, y);

        // reset for the other render() users
        brush.setTextBaseline(VPos.BASELINE);
        brush.setTextAlign(TextAlignment.LEFT);
    }
}

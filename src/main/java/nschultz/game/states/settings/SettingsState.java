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
package nschultz.game.states.settings;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import nschultz.game.io.SoundFile;
import nschultz.game.states.GameState;
import nschultz.game.states.MenuState;
import nschultz.game.ui.GameCanvas;

public final class SettingsState extends GameState {

    private static final SoundFile selectSound = new SoundFile(
            "/sounds/select.wav"
    );

    private static final SoundFile selectionSound = new SoundFile(
            "/sounds/selection.wav"
    );

    private static final int VIDEO_OPTION_INDEX = 0;
    private static final int AUDIO_OPTION_INDEX = 1;

    private final String[] options = new String[]{
            "Video",
            "Audio",
            "Back"
    };

    private int currentIndex = 0;

    public SettingsState(final GameCanvas game) {
        super(game);
    }

    @Override
    public void update(final long now) {
    }

    @Override
    public void render(final GraphicsContext brush, final long now) {
        brush.setFill(Color.BLACK);
        brush.fillRect(0, 0, game().resolution().getWidth(),
                game().resolution().getHeight()
        );

        final double w = game().resolution().getWidth();
        final double h = game().resolution().getHeight();
        brush.setFill(Color.BLACK);
        brush.fillRect(0, 0, w, h);

        final double maxWidth = 256;
        brush.setFont(Font.font(64));
        brush.setTextAlign(TextAlignment.CENTER);
        brush.setTextBaseline(VPos.CENTER);
        brush.setFill(Color.CYAN);
        brush.fillText("Settings", w / 2, h / 3, 600);

        brush.setFont(Font.font(48));
        for (int i = 0; i < options.length; i++) {
            if (i == currentIndex) {
                brush.setFill(Color.YELLOW);
                brush.setEffect(new Glow(0.5));
            } else {
                brush.setFill(Color.WHITE);
                brush.setEffect(null);
            }
            brush.fillText(options[i], w / 2, (h / 2) + i * 64, maxWidth);
        }
        // reset for the other render methods...
        brush.setEffect(null);
        brush.setTextAlign(TextAlignment.LEFT);
        brush.setTextBaseline(VPos.BASELINE);
    }

    @Override
    public void onKeyInput(final KeyEvent event, final boolean isPressed) {
        if (isPressed) {
            switch (event.getCode()) {
                case W:
                    if (currentIndex <= 0)
                        currentIndex = 0;
                    else {
                        if (game().isAudioEnabled()) selectSound.play();
                        currentIndex--;
                    }
                    break;
                case S:
                    if (currentIndex >= options.length - 1) {
                        currentIndex = options.length - 1;
                    } else {
                        if (game().isAudioEnabled()) selectSound.play();
                        currentIndex++;
                    }
                    break;
                case ENTER:
                    if (game().isAudioEnabled()) selectionSound.play();
                    if (currentIndex == VIDEO_OPTION_INDEX) {
                        GameState newState = new VideoSettingsState(game());
                        newState.setLastGameState(this.getLastGameState());
                        game().switchGameState(newState);
                    } else if (currentIndex == AUDIO_OPTION_INDEX) {
                        GameState newState = new AudioSettingsState(game());
                        newState.setLastGameState(this.getLastGameState());
                        game().switchGameState(newState);
                    } else {
                        if (this.getLastGameState()==null)
                            game().switchGameState(new MenuState(game()));
                        else game().switchGameState(new MenuState(game(), this.getLastGameState()));
                    }
                    break;
            }
        }
    }
}

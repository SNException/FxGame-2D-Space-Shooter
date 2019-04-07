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

import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import nschultz.game.io.SoundFile;
import nschultz.game.states.levels.Level1State;
import nschultz.game.states.settings.SettingsState;
import nschultz.game.ui.GameView;

public final class MenuState extends GameState {

    private static final SoundFile selectSound = new SoundFile(
            "/sounds/select.wav"
    );

    private static final SoundFile selectionSound = new SoundFile(
            "/sounds/selection.wav"
    );

    private static final SoundFile startSound = new SoundFile(
            "/sounds/start.wav"
    );

    private String[] options;

    private void initiateOptions() {
        options = new String[]{
                "New game",
                "Highscore",
                "Settings",
                "Credits",
                "Exit"
        };
    }

    private static final int START_INDEX = 0;
    private static final int HIGHSCORE_INDEX = 1;
    private static final int SETTINGS_INDEX = 2;
    private static final int CREDITS_STATE = 3;
    private int currentIndex = 0;

    public MenuState(final GameView game) {
        super(game);
        setLastGameState(null);
        initiateOptions();
    }

    public MenuState(final GameView game, final GameState lastGameState) {
        super(game);
        this.setLastGameState(lastGameState);
        initiateOptions();
        if (lastGameState != null) {
            options[0] = "Resume Game";
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

        final double maxWidth = 256;
        brush.setFont(Font.font(64));
        brush.setTextAlign(TextAlignment.CENTER);
        brush.setTextBaseline(VPos.CENTER);
        brush.setFill(Color.CYAN);
        brush.fillText("Space shooter game", w / 2, h / 3, 600);

        brush.setFont(Font.font(48));
        for (int i = 0; i < options.length; i++) {
            if (i == currentIndex) {
                brush.setEffect(new Glow(0.5));
                brush.setFill(Color.YELLOW);
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

    private void menuMoveUP() {
        if (currentIndex <= 0)
            currentIndex = 0;
        else {
            if (game().isAudioEnabled()) selectSound.play();
            currentIndex--;
        }
    }

    private void menuMoveDown() {
        if (currentIndex >= options.length - 1)
            currentIndex = options.length - 1;
        else {
            if (game().isAudioEnabled()) selectSound.play();
            currentIndex++;
        }
    }

    @Override
    public void onKeyInput(final KeyEvent event, final boolean isPressed) {
        if (isPressed) {
            switch (event.getCode()) {
                case W:
                    menuMoveUP();
                    break;
                case UP:
                    menuMoveUP();
                    break;
                case S:
                    menuMoveDown();
                    break;
                case DOWN:
                    menuMoveDown();
                    break;
                case ENTER:
                    if (currentIndex == START_INDEX) {
                        if (options[0].equals("New game")) {
                            game().switchGameState(new Level1State(game()));
                            if (game().isAudioEnabled()) startSound.play();
                        } else game().switchGameState(lastGameState());
                    } else if (currentIndex == HIGHSCORE_INDEX) {
                        if (game().isAudioEnabled()) selectionSound.play();
                        GameState newState = new HighscoreState(game());
                        newState.setLastGameState(lastGameState());
                        game().switchGameState(newState);
                    } else if (currentIndex == SETTINGS_INDEX) {
                        if (game().isAudioEnabled()) selectionSound.play();
                        GameState newState = new SettingsState(game());
                        newState.setLastGameState(lastGameState());
                        game().switchGameState(newState);
                    } else if (currentIndex == CREDITS_STATE) {
                        if (game().isAudioEnabled()) selectionSound.play();
                        GameState newState = new CreditsState(game());
                        newState.setLastGameState(lastGameState());
                        game().switchGameState(newState);
                    } else {
                        if (game().isAudioEnabled()) selectionSound.play();
                        Platform.exit();
                    }
                    break;
            }
        }
    }
}

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
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import nschultz.game.entities.Player;
import nschultz.game.io.SoundFile;
import nschultz.game.states.GameState;
import nschultz.game.ui.GameCanvas;
import nschultz.game.ui.LightFromEast;

public final class VideoSettingsState extends GameState {

    private static final SoundFile selectSound = new SoundFile(
            "/sounds/select.wav"
    );

    private static final SoundFile selectionSound = new SoundFile(
            "/sounds/selection.wav"
    );

    private final String[] options = new String[]{
            "Fullscreen",
            "Enable particles",
            "Enable lighting",
            "Back"
    };

    private int currentIndex = 0;
    private final Stage gameWindow =
            ((Stage) game().getScene().getWindow());

    VideoSettingsState(final GameCanvas game) {
        super(game);

        options[0] = gameWindow.isFullScreen() ? "Windowed" : "Fullscreen";
        game().entities().stream().filter(e -> e instanceof Player).forEach(e -> {
            final Player player = (Player) e;
            options[1] = player.isThrustParticlesActive() ? "Disable particles" : "Enable particles";
        });
        if (game().getEffect() == null) {
            options[2] = "Enable lighting";
        } else {
            options[2] = "Disable lighting";
        }
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

        final double maxWidth = 512;
        brush.setFont(Font.font(64));
        brush.setTextAlign(TextAlignment.CENTER);
        brush.setTextBaseline(VPos.CENTER);
        brush.setFill(Color.CYAN);
        brush.fillText("Video settings", w / 2, h / 3, 600);

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
                    if (currentIndex >= 3) {
                        currentIndex = 3;
                    } else {
                        if (game().isAudioEnabled()) selectSound.play();
                        currentIndex++;
                    }
                    break;
                case ENTER:
                    if (game().isAudioEnabled()) selectionSound.play();
                    if (currentIndex == 0) {
                        gameWindow.setFullScreen(!gameWindow.isFullScreen());
                        final boolean isFullScreenNow = gameWindow.isFullScreen();
                        if (isFullScreenNow) {
                            gameWindow.getScene().setCursor(Cursor.NONE);
                            options[0] = "Windowed";
                        } else {
                            gameWindow.getScene().setCursor(Cursor.DEFAULT);
                            options[0] = "Fullscreen";
                        }
                    } else if (currentIndex == 1) {
                        toggleParticlesActivation();

                    } else if (currentIndex == 2) {
                        if (game().getEffect() == null) {
                            new LightFromEast(game()).enlighten();
                            options[2] = "Disable lighting";
                        } else {
                            game().setEffect(null);
                            options[2] = "Enable lighting";
                        }
                    } else {
                        GameState newState = new SettingsState(game());
                        newState.setLastGameState(this.getLastGameState());
                        game().switchGameState(newState);
                    }
                    break;
            }
        }
    }

    private void toggleParticlesActivation() {
        game().entities().stream().filter(e -> e instanceof Player).forEach(e -> {
            final Player player = (Player) e;
            if (!player.isThrustParticlesActive()) {
                player.enableThrustParticles();
                game().enableParticles();
                options[1] = "Disable particles";
            } else {
                player.disableThrustParticles();
                game().disableParticles();
                options[1] = "Enable particles";
            }
        });
    }
}

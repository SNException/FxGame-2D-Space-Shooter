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
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import nschultz.game.io.SpriteSheet;

public final class GameWindow {

    // consider the amount of pixels we move objects
    // e.g the bullets travel at x pixel per 0.016 seconds. Which means
    // it would be best if resolution is divisible by x
    private final Dimension2D resolution = new Dimension2D(1280, 800);
    private final Stage primaryStage;

    public GameWindow(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        if (primaryStage.isShowing())
            return;

        final GameCanvas canvas = new GameCanvas(resolution);
        final Scene scene = new Scene(new StackPane(canvas));
        canvas.widthProperty().bind(scene.widthProperty());
        canvas.heightProperty().bind(scene.heightProperty());

        primaryStage.getIcons().add(
                SpriteSheet.instance().sprite(1, 1, 16, 16)
        );
        primaryStage.setTitle("FxSpace shooter v0.2.0 ALPHA");
        primaryStage.setScene(scene);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setWidth(resolution.getWidth());
        primaryStage.setHeight(resolution.getHeight());
        primaryStage.setMinWidth(resolution.getWidth());
        primaryStage.setMinHeight(resolution.getHeight());
        primaryStage.iconifiedProperty().addListener((obs, oVal, nVal)
                -> canvas.pause());
        primaryStage.show();

        new Scaling(scene).apply();
        canvas.startGameLoop();
    }
}

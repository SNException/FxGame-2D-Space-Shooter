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

import javafx.scene.canvas.Canvas;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;

public final class LightFromEast {

    private final Canvas canvas;

    public LightFromEast(final Canvas canvas) {
        this.canvas = canvas;
    }

    public void enlighten() {
        final Lighting light = new Lighting();
        final Light.Point pointingLight = new Light.Point();
        light.setLight(pointingLight);

        pointingLight.xProperty().bind(canvas.widthProperty().add(256));
        pointingLight.yProperty().bind(canvas.heightProperty().divide(2));
        pointingLight.zProperty().bind(canvas.heightProperty());

        canvas.setEffect(light);
    }
}

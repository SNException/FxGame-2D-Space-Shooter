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
package nschultz.game.core;

import javafx.animation.AnimationTimer;
import nschultz.game.util.TimeDelayedProcedure;

import java.util.concurrent.TimeUnit;

public final class GameLoop extends AnimationTimer {

    private final TimeDelayedProcedure delay = new TimeDelayedProcedure(
            1, TimeUnit.SECONDS
    );
    private final Game game;
    private int fps;
    private int frames;
    private boolean isRunning;

    public GameLoop(final Game game) {
        this.game = game;
    }

    @Override
    public void handle(final long now) {
        delay.runAfterDelayExact(now, () -> {
            fps = frames;
            frames = 0;
        });

        game.update(now);
        game.render(now);

        frames++;
    }

    @Override
    public void start() {
        super.start();
        isRunning = true;
    }

    @Override
    public void stop() {
        super.stop();
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int fps() {
        return fps;
    }

    public boolean isLagging() {
        return fps() < 60;
    }
}

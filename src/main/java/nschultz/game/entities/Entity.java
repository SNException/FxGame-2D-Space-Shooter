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
package nschultz.game.entities;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import nschultz.game.ui.GameView;

public abstract class Entity {

    private final Rectangle hitBox;
    private final double gameWidth;
    private final double gameHeight;

    private boolean isAlive = true;

    public Entity(final Point2D position, final Dimension2D size,
                  final GameView game) {

        hitBox = new Rectangle(
                position.getX(), position.getY(),
                size.getWidth(), size.getHeight()
        );

        gameWidth = game.startingWidth();
        gameHeight = game.startingHeight();
    }

    public abstract void update(final long now);

    public abstract void render(final GraphicsContext brush, final long now);

    protected final void moveToX(final double x) {
        hitBox.setX(x);
    }

    protected final void moveToY(final double y) {
        hitBox.setY(y);
    }

    protected final void moveUp(final double amount) {
        hitBox.setY(hitBox.getY() - amount);
    }

    final void moveDown(final double amount) {
        hitBox.setY(hitBox.getY() + amount);
    }

    final void moveRight(final double amount) {
        hitBox.setX(hitBox.getX() + amount);
    }

    protected final void moveLeft(final double amount) {
        hitBox.setX(hitBox.getX() - amount);
    }

    public final double xPosition() {
        return hitBox.getX();
    }

    public final double yPosition() {
        return hitBox.getY();
    }

    public final double width() {
        return hitBox.getWidth();
    }

    public final double height() {
        return hitBox.getHeight();
    }

    protected final Rectangle hitBox() {
        return hitBox;
    }

    final double gameWidth() {
        return gameWidth;
    }

    final double gameHeight() {
        return gameHeight;
    }

    public final void kill() {
        isAlive = false;
    }

    protected final void grow(final Dimension2D newSize) {
        hitBox.setWidth(newSize.getWidth());
        hitBox.setHeight(newSize.getHeight());
    }

    public final void respawn() {
        isAlive = true;
    }

    public final boolean isDead() {
        return !isAlive;
    }

    @Override
    public String toString() {
        return hashCode() + "\n" +
                getClass().getName() + "\n" +
                "IsDead: " + isDead() + "\n" +
                "Position: " + xPosition() + "/" + yPosition() + "\n" +
                "Size: " + width() + "/" + height() + "\n";
    }
}

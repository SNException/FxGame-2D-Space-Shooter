package nschultz.game.entities.enemies;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import nschultz.game.entities.Entity;
import nschultz.game.ui.GameView;
import nschultz.game.io.SpriteSheet;

import java.util.Objects;

public final class HomingEnemy extends Enemy {

    private static final Image sprite = SpriteSheet.instance().sprite(
            3, 2, 16, 16
    );

    private final Entity target;
    private double followingPower = 0.01;

    public HomingEnemy(final Point2D position, final GameView game,
                       final Entity target) {

        super(position, new Dimension2D(32, 32), game);
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public void update(final long now) {
        super.update(now);
        followTarget();
        followingPower += 0.0001;
    }

    private void followTarget() {
        final double diffX = target.xPosition() - xPosition();
        final double diffY = target.yPosition() - yPosition();
        // interpolation
        moveToX(xPosition() + diffX * followingPower);
        moveToY(yPosition() + diffY * followingPower);
    }

    @Override
    public void render(final GraphicsContext brush, final long now) {
        brush.drawImage(sprite, xPosition(), yPosition(), width(), height());
    }
}

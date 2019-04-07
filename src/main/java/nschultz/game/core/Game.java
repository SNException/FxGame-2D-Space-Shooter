package nschultz.game.core;

import javafx.scene.Node;

public interface Game {

    Node view();

    void update(final long tpf);

    void render(final long tpf);
}

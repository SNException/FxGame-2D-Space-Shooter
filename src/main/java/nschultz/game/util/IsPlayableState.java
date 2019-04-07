package nschultz.game.util;

import nschultz.game.states.*;
import nschultz.game.states.settings.AudioSettingsState;
import nschultz.game.states.settings.SettingsState;
import nschultz.game.states.settings.VideoSettingsState;

public final class IsPlayableState implements Result<Boolean> {

    private final GameState state;

    public IsPlayableState(final GameState state) {
        this.state = state;
    }

    @Override
    public Boolean value() {
        return !(state instanceof MenuState) &&
                !(state instanceof VictoryState) &&
                !(state instanceof GameOverState) &&
                !(state instanceof SettingsState) &&
                !(state instanceof AudioSettingsState) &&
                !(state instanceof VideoSettingsState) &&
                !(state instanceof CreditsState) &&
                !(state instanceof HighscoreState);
    }
}

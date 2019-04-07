package nschultz.game.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Callable;

public final class Cache<T> implements Result<T> {

    private final Deque<T> stack = new ArrayDeque<>(1);
    private final Callable<T> procedure;

    public Cache(final Callable<T> procedure) {
        this.procedure = procedure;
    }

    @Override
    public T value() {
        if (stack.isEmpty()) {
            try {
                stack.push(procedure.call());
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
        return stack.peek();
    }
}

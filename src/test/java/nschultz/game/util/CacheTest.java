package nschultz.game.util;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

public final class CacheTest {

    @Test
    public void shouldCacheObjects() {
        final Result<Object> cache = new Cache<>(Object::new);
        MatcherAssert.assertThat(
                cache.value().hashCode(),
                CoreMatchers.is(cache.value().hashCode())
        );
    }
}

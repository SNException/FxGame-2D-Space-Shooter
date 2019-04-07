package nschultz.game.io;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public final class InputStreamAsStringTest {

    @Test
    public void shouldReadStream() {
        MatcherAssert.assertThat(
                new InputStreamAsString(
                        new ByteArrayInputStream(
                                "Hello World!".getBytes(StandardCharsets.UTF_8)
                        ), 1024
                ).toString(),
                CoreMatchers.is("Hello World!")
        );
    }
}
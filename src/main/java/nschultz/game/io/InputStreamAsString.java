package nschultz.game.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public final class InputStreamAsString {

    private final InputStream source;
    private final int size;

    public InputStreamAsString(final InputStream source, final int size) {
        this.source = source;
        this.size = size;
    }

    @Override
    public String toString() {
        final byte[] buffer = new byte[size];
        final var result = new StringBuilder(buffer.length);
        while (true) {
            try {
                final int read = source.read(buffer);
                if (read < 0) break;
                result.append(
                        new String(buffer, StandardCharsets.UTF_8), 0, read
                );
            } catch (final IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
        return result.toString();
    }
}

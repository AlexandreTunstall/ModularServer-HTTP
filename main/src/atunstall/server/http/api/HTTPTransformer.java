package atunstall.server.http.api;

import atunstall.server.core.api.Version;

import java.util.function.BiConsumer;

/**
 * Models an object that transforms data relevant to the HTTP protocol.
 */
public interface HTTPTransformer<I, O> extends BiConsumer<I, O> {
    /**
     * Checks if this transformer supports the given HTTP version.
     * If a transformer supports a certain version, it must be compatible with all previous versions.
     * @param version The version to check.
     * @return True if this transformer can transform messages written with this version, false otherwise.
     */
    default boolean supports(Version version) {
        Version latestSupported = getLatestSupported();
        return version.major() <= latestSupported.major() && (version.major() < latestSupported.major() || version.minor() <= latestSupported.minor());
    }

    /**
     * Returns the latest version of the HTTP protocol this transformer supports.
     * @return The latest supported version.
     */
    Version getLatestSupported();
}

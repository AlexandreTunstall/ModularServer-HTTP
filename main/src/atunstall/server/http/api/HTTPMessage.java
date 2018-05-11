package atunstall.server.http.api;

import atunstall.server.core.api.Version;
import atunstall.server.io.api.InputStream;

import java.util.Map;
import java.util.Optional;

/**
 * Models the common parts of HTTP requests and responses.
 */
public interface HTTPMessage {
    /**
     * Returns whether or not this message was correctly parsed or created.
     * If this method returns false, all other methods' behaviour is undefined.
     * @return True if this message was correctly parsed or created, false otherwise.
     */
    boolean isValid();

    /**
     * Returns the version of the HTTP protocol this message is written for.
     * @return The version.
     */
    Version getVersion();

    /**
     * Returns the header fields of this message.
     * @return The header fields.
     */
    Map<String, String> getFields();

    /**
     * Returns the body of this message.
     * @return The body if there is one, {@link Optional#empty()} otherwise.
     */
    Optional<InputStream> getBody();
}

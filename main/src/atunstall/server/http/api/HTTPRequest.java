package atunstall.server.http.api;

import java.net.URI;

/**
 * Models a HTTP request message.
 */
public interface HTTPRequest extends HTTPMessage {
    /**
     * Returns the method for this request.
     * @return The method as a string.
     */
    String getMethod();

    /**
     * Returns the resource URI for this request.
     * @return The resource URI.
     */
    URI getResourceURI();
}

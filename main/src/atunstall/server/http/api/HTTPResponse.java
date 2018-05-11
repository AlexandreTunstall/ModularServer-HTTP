package atunstall.server.http.api;

/**
 * Models a HTTP response message.
 */
public interface HTTPResponse extends HTTPMessage {
    /**
     * Returns the status code of this response.
     * @return The status code as an integer.
     */
    int getStatusCode();

    /**
     * Returns the status message of this response.
     * @return The status message as a string.
     */
    String getStatusMessage();
}

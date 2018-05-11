package atunstall.server.http.api;

import atunstall.server.core.api.Unique;
import atunstall.server.core.api.Version;
import atunstall.server.io.api.InputStream;

import java.util.function.Consumer;

/**
 * Object that parses HTTP responses.
 */
@Version(major = 1, minor = 0)
@Unique
public interface ResponseParser extends HTTPTransformer<InputStream, Consumer<HTTPResponse>> {
    // Empty
}

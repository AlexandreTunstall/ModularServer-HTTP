package atunstall.server.http.api;

import atunstall.server.core.api.Unique;
import atunstall.server.core.api.Version;
import atunstall.server.io.api.InputStream;

import java.util.function.Consumer;

/**
 * An object that parses HTTP requests.
 */
@Version(major = 1, minor = 0)
@Unique
public interface RequestParser extends HTTPTransformer<InputStream, Consumer<HTTPRequest>> {
    // Empty
}

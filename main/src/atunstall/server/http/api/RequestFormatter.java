package atunstall.server.http.api;

import atunstall.server.core.api.Unique;
import atunstall.server.core.api.Version;
import atunstall.server.io.api.OutputStream;

/**
 * An object that formats HTTP requests.
 */
@Version(major = 1, minor = 0)
@Unique
public interface RequestFormatter extends HTTPTransformer<HTTPRequest, OutputStream> {
    // Empty
}

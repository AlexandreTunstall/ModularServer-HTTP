package atunstall.server.http.api;

import atunstall.server.core.api.Unique;
import atunstall.server.core.api.Version;
import atunstall.server.io.api.OutputStream;

/**
 * Object that formats HTTP responses.
 */
@Version(major = 1, minor = 0)
@Unique
public interface ResponseFormatter extends HTTPTransformer<HTTPResponse, OutputStream> {
    /**
     * Gets the best version compatible with both this formatter and the given request version.
     * @param requestVersion The version of the request
     * @return The ideal version for a response to the given request's version.
     */
    Version getBestCompatibleVersion(Version requestVersion);
}

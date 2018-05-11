package atunstall.server.http.impl;

import atunstall.server.core.api.Module;
import atunstall.server.core.api.Version;
import atunstall.server.http.api.HTTPRequest;
import atunstall.server.http.api.RequestFormatter;
import atunstall.server.io.api.OutputStream;
import atunstall.server.io.api.util.ArrayStreams;

@Module
public class RequestFormatterImpl extends MessageFormatterImpl<HTTPRequest> implements RequestFormatter {
    public RequestFormatterImpl(@Version(major = 1, minor = 0) ArrayStreams streams) {
        super(streams);
    }

    @Override
    protected void appendLine(StringBuilder builder, HTTPRequest request) {
        builder.append(request.getMethod());
        builder.append(' ');
        builder.append(request.getResourceURI().toASCIIString());
        builder.append(" HTTP/");
        builder.append(request.getVersion().major());
        builder.append('.');
        builder.append(request.getVersion().minor());
        builder.append("\r\n");
    }

    @Override
    protected void onWritten(OutputStream output, boolean close) {
        // Do nothing.
    }
}

package atunstall.server.http.impl;

import atunstall.server.core.api.Module;
import atunstall.server.core.api.Version;
import atunstall.server.http.api.HTTPResponse;
import atunstall.server.http.api.ResponseFormatter;
import atunstall.server.io.api.OutputStream;
import atunstall.server.io.api.util.ArrayStreams;

@Module
public class ResponseFormatterImpl extends MessageFormatterImpl<HTTPResponse> implements ResponseFormatter {
    public ResponseFormatterImpl(@Version(major = 1, minor = 0) ArrayStreams streams) {
        super(streams);
    }

    @Override
    public void appendLine(StringBuilder builder, HTTPResponse response) {
        builder.append("HTTP/");
        builder.append(response.getVersion().major());
        builder.append('.');
        builder.append(response.getVersion().minor());
        builder.append(' ');
        builder.append(response.getStatusCode());
        builder.append(' ');
        builder.append(response.getStatusMessage());
        builder.append("\r\n");
    }

    @Override
    protected void onWritten(OutputStream output, boolean close) {
        if (close) {
            System.out.println("Closing stream");
            try {
                output.close();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public Version getBestCompatibleVersion(Version requestVersion) {
        Version latest = getLatestSupported();
        if (latest.major() != requestVersion.major()) {
            switch (Math.min(latest.major(), requestVersion.major())) {
                case 0:
                    return new VersionImpl(0, 9);
                case 1:
                    return new VersionImpl(1, 1);
            }
        }
        return latest;
    }
}

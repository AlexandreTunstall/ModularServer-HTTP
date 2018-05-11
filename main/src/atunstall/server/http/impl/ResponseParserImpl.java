package atunstall.server.http.impl;

import atunstall.server.core.api.Module;
import atunstall.server.core.api.Version;
import atunstall.server.http.api.HTTPResponse;
import atunstall.server.http.api.ResponseParser;
import atunstall.server.io.api.InputStream;
import atunstall.server.io.api.ParsableByteBuffer;
import atunstall.server.io.api.util.ArrayStreams;

import java.util.Map;
import java.util.Optional;

@Module
public class ResponseParserImpl extends MessageParserImpl<HTTPResponse> implements ResponseParser {
    public ResponseParserImpl(@Version(major = 1, minor = 0) ArrayStreams streams) {
        super(streams);
    }

    @Override
    MessageBuilder<HTTPResponse> parseLine(ParsableByteBuffer header) {
        ResponseBuilder builder = new ResponseBuilder();
        long index = header.find(0L, VersionImpl.SP);
        String version = header.toString(0L, index, VersionImpl.HEADER_CHARSET);
        header.consume(0L, index + VersionImpl.SP.length);
        if (!version.startsWith(VersionImpl.VERSION_PREFIX)) {
            throw new IllegalArgumentException("bad request");
        }
        version = version.substring(VersionImpl.VERSION_PREFIX.length());
        int dotIndex = version.indexOf('.');
        builder.version = new VersionImpl(Integer.parseInt(version.substring(0, dotIndex)), Integer.parseInt(version.substring(dotIndex + 1)));
        index = header.find(0L, VersionImpl.SP);
        builder.statusCode = Integer.parseInt(header.toString(0L, index, VersionImpl.HEADER_CHARSET));
        header.consume(0L, index + VersionImpl.SP.length);
        builder.statusMessage = header.toString(0L, header.count(), VersionImpl.HEADER_CHARSET);
        header.consumeAll();
        return builder;
    }

    @Override
    HTTPResponse invalidMessage() {
        return new InvalidResponseImpl();
    }

    private class ResponseBuilder extends MessageBuilder<HTTPResponse> {
        private int statusCode;
        private String statusMessage;
        private Version version;

        @Override
        HTTPResponse build(InputStream body) {
            return new ResponseImpl(this, body);
        }
    }

    private class ResponseImpl implements HTTPResponse {
        private int statusCode;
        private String statusMessage;
        private Version version;
        private Map<String, String> fields;
        private InputStream body;

        private ResponseImpl(ResponseBuilder builder, InputStream body) {
            statusCode = builder.statusCode;
            statusMessage = builder.statusMessage;
            version = builder.version;
            fields = builder.fields;
            this.body = body;
        }

        @Override
        public int getStatusCode() {
            return statusCode;
        }

        @Override
        public String getStatusMessage() {
            return statusMessage;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Version getVersion() {
            return version;
        }

        @Override
        public Map<String, String> getFields() {
            return fields;
        }

        @Override
        public Optional<InputStream> getBody() {
            return Optional.ofNullable(body);
        }
    }

    private class InvalidResponseImpl implements HTTPResponse {
        @Override
        public int getStatusCode() {
            throw new UnsupportedOperationException("invalid message");
        }

        @Override
        public String getStatusMessage() {
            throw new UnsupportedOperationException("invalid message");
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public Version getVersion() {
            throw new UnsupportedOperationException("invalid message");
        }

        @Override
        public Map<String, String> getFields() {
            throw new UnsupportedOperationException("invalid message");
        }

        @Override
        public Optional<InputStream> getBody() {
            throw new UnsupportedOperationException("invalid message");
        }
    }
}

package atunstall.server.http.impl;

import atunstall.server.core.api.Module;
import atunstall.server.core.api.Version;
import atunstall.server.http.api.HTTPRequest;
import atunstall.server.http.api.RequestParser;
import atunstall.server.io.api.InputStream;
import atunstall.server.io.api.ParsableByteBuffer;
import atunstall.server.io.api.util.ArrayStreams;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Module
public class RequestParserImpl extends MessageParserImpl<HTTPRequest> implements RequestParser {
    public RequestParserImpl(@Version(major = 1, minor = 0) ArrayStreams streams) {
        super(streams);
    }

    @Override
    MessageBuilder<HTTPRequest> parseLine(ParsableByteBuffer header) {
        RequestBuilder builder = new RequestBuilder();
        long index = header.find(0L, VersionImpl.SP);
        builder.method = header.toString(0L, index, VersionImpl.HEADER_CHARSET);
        header.consume(0L, index + VersionImpl.SP.length);
        index = header.find(0L, VersionImpl.SP);
        builder.resourceURI = URI.create(header.toString(0L, index, VersionImpl.HEADER_CHARSET));
        header.consume(0L, index + VersionImpl.SP.length);
        String version = header.toString(0L, header.count(), VersionImpl.HEADER_CHARSET);
        if (!version.startsWith(VersionImpl.VERSION_PREFIX)) {
            throw new IllegalArgumentException("bad request");
        }
        version = version.substring(VersionImpl.VERSION_PREFIX.length());
        int dotIndex = version.indexOf('.');
        builder.version = new VersionImpl(Integer.parseInt(version.substring(0, dotIndex)), Integer.parseInt(version.substring(dotIndex + 1)));
        return builder;
    }

    @Override
    HTTPRequest invalidMessage() {
        return new InvalidRequestImpl();
    }

    private class RequestBuilder extends MessageBuilder<HTTPRequest> {
        private String method;
        private URI resourceURI;
        private Version version;

        @Override
        HTTPRequest build(InputStream body) {
            return new RequestImpl(this, body);
        }
    }

    private class RequestImpl implements HTTPRequest {
        private String method;
        private URI resourceURI;
        private Version version;
        private Map<String, String> fields;
        private InputStream body;

        private RequestImpl(RequestBuilder builder, InputStream body) {
            method = builder.method;
            resourceURI = builder.resourceURI;
            version = builder.version;
            fields = builder.fields;
            this.body = body;
        }

        @Override
        public String getMethod() {
            return method;
        }

        @Override
        public URI getResourceURI() {
            return resourceURI;
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

    private class InvalidRequestImpl implements HTTPRequest {
        @Override
        public String getMethod() {
            throw new UnsupportedOperationException("invalid message");
        }

        @Override
        public URI getResourceURI() {
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

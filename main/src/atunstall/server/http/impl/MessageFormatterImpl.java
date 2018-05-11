package atunstall.server.http.impl;

import atunstall.server.core.api.Version;
import atunstall.server.http.api.HTTPMessage;
import atunstall.server.http.api.HTTPTransformer;
import atunstall.server.io.api.ByteBuffer;
import atunstall.server.io.api.InputStream;
import atunstall.server.io.api.OutputStream;
import atunstall.server.io.api.util.ArrayStreams;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

abstract class MessageFormatterImpl<T extends HTTPMessage> implements HTTPTransformer<T, OutputStream> {
    private static final Version LATEST_SUPPORTED = new VersionImpl(1, 1);
    private final ArrayStreams streams;

    MessageFormatterImpl(ArrayStreams streams) {
        this.streams = streams;
    }

    @Override
    public void accept(T message, OutputStream consumer) {
        if (!supports(message.getVersion())) {
            throw new IllegalArgumentException("unsupported HTTP version");
        }
        StringBuilder builder = new StringBuilder();
        appendLine(builder, message);
        AtomicLong longContainer = new AtomicLong(-1L);
        AtomicBoolean booleanContainer = new AtomicBoolean(false);
        message.getFields().forEach((k, v) -> {
            builder.append(k);
            builder.append(": ");
            builder.append(v);
            builder.append("\r\n");
            if ("content-length".equalsIgnoreCase(k)) {
                longContainer.setPlain(Long.parseLong(v));
            } else if ("connection".equalsIgnoreCase(k) && VersionImpl.fieldContains(v, "close")) {
                booleanContainer.set(true);
            }
        });
        builder.append("\r\n");
        ByteBuffer header = streams.createByteBuffer(builder.toString().getBytes(VersionImpl.HEADER_CHARSET));
        if (longContainer.getPlain() > 0L) {
            InputStream body = message.getBody().orElseThrow(() -> new IllegalArgumentException("request contains content-length field but no entity body"));
            consumer.accept(header);
            body.queueConsumer(b -> {
                long contentLength = longContainer.get();
                b = b.partition(0L, Math.min(b.count(), contentLength));
                try {
                    consumer.accept(b);
                } catch (RuntimeException ignored) {}
                longContainer.set(contentLength -= b.count());
                b.consumeAll();
                if (contentLength <= 0) {
                    onWritten(consumer, booleanContainer.get());
                }
            });
        } else {
            consumer.accept(header);
        }
    }

    @Override
    public Version getLatestSupported() {
        return LATEST_SUPPORTED;
    }

    protected abstract void appendLine(StringBuilder builder, T t);

    protected abstract void onWritten(OutputStream output, boolean close);
}

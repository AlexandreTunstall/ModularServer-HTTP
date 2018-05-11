package atunstall.server.http.impl;

import atunstall.server.core.api.Version;
import atunstall.server.http.api.HTTPMessage;
import atunstall.server.http.api.HTTPTransformer;
import atunstall.server.io.api.InputStream;
import atunstall.server.io.api.ParsableByteBuffer;
import atunstall.server.io.api.util.ArrayStreams;
import atunstall.server.io.api.util.HandledInputStream;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

abstract class MessageParserImpl<T extends HTTPMessage> implements HTTPTransformer<InputStream, Consumer<T>> {
    private static final Version LATEST_SUPPORTED = new VersionImpl(1, 1);

    private final ArrayStreams streams;

    MessageParserImpl(ArrayStreams streams) {
        this.streams = streams;
    }

    @Override
    public void accept(InputStream stream, Consumer<T> consumer) {
        InfoWrapper wrapper = new InfoWrapper();
        stream.queueConsumer(b -> {
            if (wrapper.info != null) return;
            wrapper.info = parseHeader(b, consumer);
            if (b.count() > 0L) {
                System.out.println("Remaining: " + b.toString(0L, b.count(), VersionImpl.HEADER_CHARSET));
            }
        });
        stream.queueConsumer(b -> {
            if (wrapper.info == null) {
                consumer.accept(invalidMessage());
                return;
            } else if (wrapper.info.length == 0L) {
                if (!wrapper.info.close) accept(stream, consumer);
                return;
            }
            wrapper.info.bodyWrapper.consume(b.partition(0L, Math.min(b.count(), wrapper.info.length)));
            wrapper.info.length -= b.bytesConsumed();
        });
    }

    @Override
    public Version getLatestSupported() {
        return LATEST_SUPPORTED;
    }

    abstract MessageBuilder<T> parseLine(ParsableByteBuffer buffer);

    abstract T invalidMessage();

    private BodyInfo parseHeader(ParsableByteBuffer buffer, Consumer<T> consumer) {
        //System.out.println(buffer.toString(0L, buffer.count(), VersionImpl.HEADER_CHARSET));
        long headerSize = buffer.find(0L, VersionImpl.CRLF);
        ParsableByteBuffer partition = buffer.partition(0L, headerSize);
        MessageBuilder<T> builder = parseLine(partition);
        partition.consumeAll();
        buffer.consume(0L, VersionImpl.CRLF.length);
        headerSize = buffer.find(0L, VersionImpl.CRLF_CRLF);
        ParsableByteBuffer header = buffer.partition(0L, headerSize);
        AtomicLong longContainer = new AtomicLong(0L);
        AtomicBoolean booleanContainer = new AtomicBoolean(false);
        header.split(VersionImpl.CRLF).forEach(line -> {
            long kv_index = line.find(0L, VersionImpl.KV_SEPARATOR);
            line.consume(kv_index, VersionImpl.KV_SEPARATOR.length);
            String key = line.toString(0L, kv_index, VersionImpl.HEADER_CHARSET);
            String value = line.toString(kv_index, line.count() - kv_index, VersionImpl.HEADER_CHARSET);
            builder.addField(key, value);
            if ("content-length".equalsIgnoreCase(key)) {
                longContainer.set(Long.parseLong(value));
            } else if ("connection".equalsIgnoreCase(key)) {
                booleanContainer.setPlain(VersionImpl.fieldContains(value, "close"));
            }
        });
        header.consumeAll();
        buffer.consume(0L, VersionImpl.CRLF_CRLF.length);
        long bodySize = longContainer.longValue();
        HandledInputStream body = null;
        if (bodySize > 0L) {
            body = streams.createInputStream();
        }
        consumer.accept(builder.build(body));
        return new BodyInfo(body, bodySize, booleanContainer.getPlain());
    }

    /*private long findSafe(ParsableByteBuffer byteBuffer, byte[] sequence, int lengthLimit) {
        try {
            return byteBuffer.find(0L, sequence);
        } catch (RuntimeException e) {
            if (byteBuffer.count() > lengthLimit) {
                byteBuffer.consumeAll();
                return -1L;
            }
            throw e;
        }
    }*/

    private class BodyInfo {
        private HandledInputStream bodyWrapper;
        private long length;
        private boolean close;

        private BodyInfo(HandledInputStream bodyWrapper, long length, boolean close) {
            this.bodyWrapper = bodyWrapper;
            this.length = length;
            this.close = close;
        }
    }

    private class InfoWrapper {
        private BodyInfo info;
    }
}
